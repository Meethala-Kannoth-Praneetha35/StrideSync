package uk.ac.tees.mad.stridesync.ui

import android.app.Application
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.ac.tees.mad.stridesync.services.StepTrackingService
import uk.ac.tees.mad.stridesync.data.StepRepository.StepRepository
import kotlinx.coroutines.flow.MutableStateFlow
import android.app.ActivityManager
import android.content.Context

@HiltViewModel
class StepViewModel @Inject constructor(
    application: Application,
    private val repository: StepRepository,
    private val auth: FirebaseAuth
) : AndroidViewModel(application) {

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking

    val todaySteps: StateFlow<Int> = repository.getTodaySteps()
        .map { entity -> entity?.steps ?: 0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val strideLength = 0.762f
    val distanceMeters: StateFlow<Float> = todaySteps.map { it * strideLength }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0f)

    val distanceKm: StateFlow<Float> = distanceMeters.map { it / 1000f }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0f)

    val kcal: StateFlow<Float> = todaySteps.map { it * 0.05f }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0f)

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val todayStepsFromRoom = repository.getStepsByDateSync(repository.todayDate())?.steps ?: 0
            println("Initial steps from Room: $todayStepsFromRoom")
            // Check if StepTrackingService is running
            if (isServiceRunning(StepTrackingService::class.java)) {
                _isTracking.value = true
                startTimerIfNotRunning()
            }
        }
    }

    private fun isServiceRunning(serviceClass: Class<out Service>): Boolean {
        val manager = getApplication<Application>().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        return manager.getRunningServices(Integer.MAX_VALUE).any { it.service.className == serviceClass.name }
    }

    private fun startTimerIfNotRunning() {
        if (timerJob?.isActive != true) {
            timerJob = viewModelScope.launch {
                while (true) {
                    kotlinx.coroutines.delay(1000)
                    _elapsedTime.value += 1000
                }
            }
        }
    }

    fun startTracking() {
        if (_isTracking.value) return
        _isTracking.value = true
        val intent = Intent(getApplication(), StepTrackingService::class.java)
        getApplication<Application>().startForegroundService(intent)
        startTimerIfNotRunning()
    }

    fun stopTracking() {
        if (!_isTracking.value) return
        _isTracking.value = false
        val intent = Intent(getApplication(), StepTrackingService::class.java)
        getApplication<Application>().stopService(intent)
        timerJob?.cancel()
    }

    fun refreshSteps() {
        viewModelScope.launch {
            val todayStepsFromRoom = repository.getStepsByDateSync(repository.todayDate())?.steps ?: 0
            println("Refreshed steps from Room: $todayStepsFromRoom")
        }
    }
}