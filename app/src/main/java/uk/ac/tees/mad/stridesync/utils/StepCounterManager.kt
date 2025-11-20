package uk.ac.tees.mad.stridesync.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class StepCounterManager(context: Context) : SensorEventListener {

    private var sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var stepDetector: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private var stepCounter: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private var initialStepCount: Int = -1
    private val _todaySteps = MutableStateFlow(0)
    val todaySteps: StateFlow<Int> = _todaySteps

    private var todayDate: Int = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    fun startListening() {
        stepDetector?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        } ?: stepCounter?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        if (currentDay != todayDate) {
            todayDate = currentDay
            initialStepCount = -1
            _todaySteps.value = 0
        }

        when (event.sensor.type) {
            Sensor.TYPE_STEP_DETECTOR -> {
                if (event.values[0] == 1.0f) {
                    _todaySteps.value = _todaySteps.value + 1
                }
            }

            Sensor.TYPE_STEP_COUNTER -> {
                val totalSteps = event.values[0].toInt()
                if (initialStepCount == -1) {
                    initialStepCount = totalSteps
                }
                _todaySteps.value = totalSteps - initialStepCount
            }
        }
    }

    var onReset: () -> Unit = {}
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
