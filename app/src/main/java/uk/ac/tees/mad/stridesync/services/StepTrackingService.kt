package uk.ac.tees.mad.stridesync.services

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uk.ac.tees.mad.stridesync.MainActivity
import uk.ac.tees.mad.stridesync.R
import uk.ac.tees.mad.stridesync.data.StepRepository.StepRepository
import uk.ac.tees.mad.stridesync.utils.StepCounterManager
import javax.inject.Inject

@AndroidEntryPoint
class StepTrackingService : Service() {

    @Inject lateinit var stepCounterManager: StepCounterManager
    @Inject lateinit var repository: StepRepository
    @Inject lateinit var auth: com.google.firebase.auth.FirebaseAuth

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "step_tracking_channel"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceScope.launch {
            val todayStepsFromRoom = repository.getStepsByDateSync(repository.todayDate())?.steps ?: 0
            stepCounterManager.setInitialSteps(todayStepsFromRoom)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            startForeground(NOTIFICATION_ID, createNotification(0, 0f, 0f))
        } else {
            stopSelf()
            return START_NOT_STICKY
        }

        serviceScope.launch {
            stepCounterManager.todaySteps.collectLatest { steps ->
                val distanceMeters = steps * 0.762f
                val kcal = steps * 0.05f
                updateNotification(steps, distanceMeters, kcal)
                auth.currentUser?.uid?.let { uid ->
                    repository.saveSteps(steps, uid)
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            stepCounterManager.startListening()
        } else {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    private fun createNotification(steps: Int, distanceMeters: Float, kcal: Float) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Step Tracking Active")
            .setContentText("Steps: $steps | Distance: ${String.format("%.2f", distanceMeters / 1000)} km | Calories: ${kcal.toInt()} kcal")
            .setSmallIcon(R.drawable.app_icons)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

    private fun updateNotification(steps: Int, distanceMeters: Float, kcal: Float) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notification = createNotification(steps, distanceMeters, kcal)
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
        }
    }

    override fun onDestroy() {
        stepCounterManager.stopListening()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}