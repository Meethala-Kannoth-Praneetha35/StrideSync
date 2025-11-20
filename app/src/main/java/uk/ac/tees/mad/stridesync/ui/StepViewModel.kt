package uk.ac.tees.mad.stridesync.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.ac.tees.mad.stridesync.utils.StepCounterManager
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel
class StepViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    private val stepCounterManager = StepCounterManager(application)

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking

    val todaySteps: StateFlow<Int> = stepCounterManager.todaySteps
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
        stepCounterManager.onReset = {
            _elapsedTime.value = 0L
        }
    }

    fun startTracking() {
        if (_isTracking.value) return
        _isTracking.value = true
        stepCounterManager.startListening()
        timerJob = viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                _elapsedTime.value += 1000
            }
        }
    }

    fun stopTracking() {
        if (!_isTracking.value) return
        _isTracking.value = false
        stepCounterManager.stopListening()
        timerJob?.cancel()
    }
}