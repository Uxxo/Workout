package com.example.personalworkoutnotebook.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.personalworkoutnotebook.model.Exercise
import com.example.personalworkoutnotebook.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private var _exercise = MutableLiveData<Exercise>()
    val exercise: LiveData<Exercise>
        get() = _exercise

    private var _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    suspend fun saveExercise(exercise: Exercise){
        exerciseRepository.save(exercise)
    }

}