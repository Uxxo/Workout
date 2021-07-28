package com.example.personalworkoutnotebook.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.personalworkoutnotebook.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    private var _loadingIndicator = MutableLiveData<Boolean>()
    val loadingIndicator : LiveData<Boolean>
    get() = _loadingIndicator

    suspend fun loadData() {
        _loadingIndicator.postValue(true)
        delay(2000)
        _loadingIndicator.postValue(false)
    }
}