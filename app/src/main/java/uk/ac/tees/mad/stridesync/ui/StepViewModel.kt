package uk.ac.tees.mad.stridesync.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import uk.ac.tees.mad.stridesync.utils.StepCounterManager

@HiltViewModel
class StepViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    private val stepCounterManager = StepCounterManager(application)

    val todaySteps: StateFlow<Int> = stepCounterManager.todaySteps
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun startTracking() {
        stepCounterManager.startListening()
    }

    fun stopTracking() {
        stepCounterManager.stopListening()
    }
}
