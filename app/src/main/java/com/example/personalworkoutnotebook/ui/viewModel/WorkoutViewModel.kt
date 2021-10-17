package com.example.personalworkoutnotebook.ui.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.personalworkoutnotebook.DBFactory
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.model.Approach
import com.example.personalworkoutnotebook.model.Exercise
import com.example.personalworkoutnotebook.model.Workout
import com.example.personalworkoutnotebook.repository.ApproachRepository
import com.example.personalworkoutnotebook.repository.ExerciseRepository
import com.example.personalworkoutnotebook.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val approachRepository: ApproachRepository,
    private val dbFactory: DBFactory
) : ViewModel() {
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    // Скрытый объект LiveData, содержимое которого можно менять
    private var _workouts = MutableLiveData<List<Workout>>()

    // Объект-фасад, используемый снаружи (getter)
    val workouts: LiveData<List<Workout>>
        get() = _workouts

    private var _workout = MutableLiveData<Workout>()
    val workout: LiveData<Workout>
        get() = _workout

    private var idValue: Long = -1

    suspend fun updateWorkoutData(){
        _workout.postValue(workoutRepository.getById(_workout.value!!.id))
    }

    suspend fun loadWorkoutById(context: Context, id: Long) {
        idValue = id
        if (id == -1L) {
            idValue = createWorkout(context)
        }
        _workout.postValue(workoutRepository.getById(idValue))
    }

    suspend fun loadData() {
        _isLoading.postValue(true)

        // загружаем данные и помещаем их в объект LiveData
        _workouts.postValue(workoutRepository.getAll().sortedByDescending { it.date.timeInMillis })

        _isLoading.postValue(false)

    }

    suspend fun updateViewModel(workoutId: Long){
        _isLoading.postValue(true)
        val workout : Workout? = workoutRepository.getById(workoutId)
        if(workout != null){_workout.postValue(workout!!)}

        _isLoading.postValue(false)
    }

    suspend fun updateWorkout(newWorkout: Workout) {
        workoutRepository.save(newWorkout)
    }

    private suspend fun createWorkout(context: Context): Long {
        val newWorkout = Workout(
            name = context.getString(R.string.new_workout_title),
            date = Calendar.getInstance(),
            exercises = mutableListOf()
        )
        return workoutRepository.save(newWorkout).id
    }

    suspend fun deleteWorkout(workout: Workout) {
        _isLoading.postValue(true)
        val changingListWorkout: MutableList<Workout> = _workouts.value!!.toMutableList()
        changingListWorkout.remove(workout)
        _workouts.postValue(changingListWorkout)
        _isLoading.postValue(false)

        workoutRepository.delete(workout.id)
    }

    suspend fun deleteExercise(exercise: Exercise) {
        _isLoading.postValue(true)
        val existingWorkout = _workout.value ?: return
        val existingExerciseList = existingWorkout.exercises as MutableList
        existingExerciseList.remove(exercise)
        _workout.postValue(existingWorkout.copy(exercises = existingExerciseList))
        _isLoading.postValue(false)
        exerciseRepository.delete(exercise)
    }

    suspend fun saveExercise(exercise: Exercise) {
        exerciseRepository.save(exercise)
    }

    suspend fun saveApproach(approach: Approach){
        approachRepository.save(approach)
        val exitingWorkout = _workout.value ?: return
        val existingExercise = exitingWorkout.exercises.first { it.id == approach.exerciseId }
        val existingApproachList = existingExercise.approaches as MutableList
        existingApproachList.add(approach)
        val updatedExercise = existingExercise.copy(approaches = existingApproachList)
        val existingExerciseList = exitingWorkout.exercises as MutableList
        existingExerciseList.add(updatedExercise)
        val updatedWorkout = exitingWorkout.copy(exercises = existingExerciseList)
        _workout.postValue(updatedWorkout)
    }

    suspend fun deleteApproach(approach: Approach) {
        approachRepository.delete(approach.id)
    }

    suspend fun addNewExerciseToWorkout(context: Context, workout: Workout){
        val savedExercise = exerciseRepository.save(createNewExercise(context, workout))
        val newApproachesList = mutableListOf<Approach>()
        for (i in 1..4){
            newApproachesList.add(approachRepository.save(createNewApproach(savedExercise)))
        }
        val updatedExercise = savedExercise.copy(approaches = newApproachesList)
        val exerciseListForUpdate = workout.exercises as MutableList
        exerciseListForUpdate.add(updatedExercise)
        val updatedWorkout = workout.copy(exercises = exerciseListForUpdate)
        workoutRepository.save(updatedWorkout)
        _workout.postValue(updatedWorkout)
    }

    suspend fun addApproachToExercise(exercise: Exercise){
        val existingWorkout = _workout.value ?: return
        val savedApproach = approachRepository.save(createNewApproach(exercise))


//        val editedApproachList = exercise.approaches as MutableList
//        editedApproachList.add(savedApproach)
//        val updatedExercise = exercise.copy(approaches = editedApproachList)
//
//        val editedExerciseList = existingWorkout.exercises as MutableList
//        val index = editedExerciseList.indexOf(editedExerciseList.first { it.id == exercise.id })
//        editedExerciseList.removeAt(index)
//        editedExerciseList[index] = updatedExercise
//        val updatedWorkout = existingWorkout.copy(exercises = editedExerciseList)
//        _workout.postValue(updatedWorkout)
    }

    private suspend fun createNewApproach(exercise: Exercise) : Approach {
        return Approach(id = 0L, exerciseId = exercise.id, mass = 0.0, repeat = 0)
    }

    private suspend fun createNewExercise(context: Context, workout: Workout): Exercise {
        return Exercise(
            id = 0L,
            workoutId = workout.id,
            name = context.getString(R.string.exercise_title),
            notes = null
        )
    }

    suspend fun createTestDB() {
        dbFactory.testCreateDB()
    }
}