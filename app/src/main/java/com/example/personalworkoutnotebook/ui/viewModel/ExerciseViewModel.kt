//package com.example.personalworkoutnotebook.ui.viewModel
//
//import android.content.Context
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.example.personalworkoutnotebook.R
//import com.example.personalworkoutnotebook.model.Approach
//import com.example.personalworkoutnotebook.model.Exercise
//import com.example.personalworkoutnotebook.repository.ApproachRepository
//import com.example.personalworkoutnotebook.repository.ExerciseRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//
//@HiltViewModel
//class ExerciseViewModel @Inject constructor(
//    private val exerciseRepository: ExerciseRepository,
//    private val approachRepository: ApproachRepository
//) : ViewModel() {
//
//    private var _exercises = MutableLiveData<List<Exercise>>()
//    val exercises : LiveData<List<Exercise>>
//    get() = _exercises
//
//    private var _exercise = MutableLiveData<Exercise>()
//    val exercise: LiveData<Exercise>
//        get() = _exercise
//
//    private var _isLoading = MutableLiveData(false)
//    val isLoading: LiveData<Boolean>
//        get() = _isLoading
//
//
//    suspend fun loadData(workoutId : Long){
//        _isLoading.postValue(true)
//        _exercises.postValue(exerciseRepository.getByWorkoutId(workoutId).sortedBy { it.id })
//        _isLoading.postValue(false)
//    }
//
//    suspend fun updateExerciseViewModel(exerciseId : Long){
//        _isLoading.postValue(true)
//        val exercise : Exercise? = exerciseRepository.getById(exerciseId)
//        if(exercise != null) _exercise.postValue(exercise!!)
//        _isLoading.postValue(false)
//    }
//
//    suspend fun createExercise(context : Context,workoutId: Long){
//        _isLoading.postValue(true)
//
//        val exercise = Exercise(
//            id = 0L,
//            workoutId = workoutId,
//            name = context.getString(R.string.exercise_title),
//            notes = null
//        )
//
//        val savedExercise = exerciseRepository.save(exercise)
//        val approachesList = mutableListOf<Approach>()
//
//        for (i in 1..4) {
//            val approach = Approach(
//                id = 0L, exerciseId = savedExercise.id, mass = 0.0, repeat = 0
//            )
//            val savedApproach = approachRepository.save(approach)
//            approachesList.add(savedApproach)
//        }
//
//        val updatedExercise = savedExercise.copy(approaches = approachesList)
//        saveExercise(updatedExercise)
//        loadData(workoutId)
//        _isLoading.postValue(false)
//    }
//
//    suspend fun saveExercise(exercise: Exercise){
//        exerciseRepository.save(exercise)
//    }
//
//    suspend fun deleteExercise(exercise: Exercise){
//        exerciseRepository.delete(exercise)
//        loadData(exercise.workoutId)
//    }
//}