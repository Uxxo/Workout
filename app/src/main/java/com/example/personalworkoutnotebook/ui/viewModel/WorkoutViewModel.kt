package com.example.personalworkoutnotebook.ui.viewModel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.personalworkoutnotebook.model.*
import com.example.personalworkoutnotebook.model.Set
import com.example.personalworkoutnotebook.repository.SetRepository
import com.example.personalworkoutnotebook.repository.ExerciseRepository
import com.example.personalworkoutnotebook.repository.WorkoutTimerRepository
import com.example.personalworkoutnotebook.repository.WorkoutRepository
import com.example.personalworkoutnotebook.ui.WorkoutDataService
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject


@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val setRepository: SetRepository,
    private val timerRepository: WorkoutTimerRepository,


//    private val dbFactory: DBFactory
) : ViewModel() {

    private var allExercise: List<Exercise?> = listOf()

    private var _isLoading = MutableLiveData<Boolean>()

    val isLoading: LiveData<Boolean>
        get() = _isLoading


    private var _workouts = MutableLiveData<List<Workout>>()
    val workouts: LiveData<List<Workout>>
        get() = _workouts

    private var _workout = MutableLiveData<Workout>()

    val workout: LiveData<Workout>
        get() = _workout

    private var _exercise = MutableLiveData<Exercise>()
    val exercise: LiveData<Exercise>
        get() = _exercise

    private var _exercisesWithDates = MutableLiveData <List<ExerciseWithDate>>()
    val exerciseWithDate: LiveData<List<ExerciseWithDate>>
        get() = _exercisesWithDates

//    private var _exercisesGraphData = MutableLiveData<Map<Calendar, Double>>()
//    val exerciseGraphData: LiveData<Map<Calendar, Double>>
//        get() = _exercisesGraphData

    private var _groups = MutableLiveData<List<Group>>()
    val groups: LiveData<List<Group>>
        get() = _groups

    private var _uniqueExercises = MutableLiveData<List<Exercise>>()
    val uniqueExercises: LiveData<List<Exercise>>
        get() = _uniqueExercises


    suspend fun updateWorkoutValue(workoutId: Long) {
        _workout.value = workoutRepository.getById(workoutId)
    }


    suspend fun loadAllWorkouts() {
        _isLoading.postValue(true)
        _workouts.value = workoutRepository.getAll()
        _isLoading.postValue(false)

    }

    suspend fun loadExerciseById(id: Long) {
        _exercise.value = exerciseRepository.getById(id)
    }

    suspend fun saveWorkout(newWorkout: Workout) {
        workoutRepository.save(newWorkout)
    }

    suspend fun updateWorkout(newWorkout: Workout) {
        saveWorkout(newWorkout)
        updateWorkoutValue(newWorkout.id)
    }

    suspend fun createWorkout(): Workout {
        val newWorkout = Workout(
            name = "",
            date = Calendar.getInstance(),
            exercises = mutableListOf(),
            status = Workout.CREATED,
            timers = mutableListOf()
        )
        val createdWorkout = workoutRepository.save(newWorkout)
        addNewTimersToWorkout(createdWorkout.id)
        return createdWorkout
    }

    suspend fun duplicateWorkout(workout: Workout) {
        val newWorkout = createWorkout()
        val editedWorkout = workout.copy(
            id = newWorkout.id, exercises = mutableListOf(),
            date = newWorkout.date, status = newWorkout.status
        )

        saveWorkout(editedWorkout)

        val originExercises = workout.exercises
        originExercises.forEach { exercise ->
            val newExerciseId = saveExercise(exercise.copy(id = 0, workoutId = newWorkout.id))

            exercise.sets.forEach { set ->
                saveSet(set.copy(id = 0, exerciseId = newExerciseId, repeat = 0))
            }
        }
        loadAllWorkouts()
    }

    suspend fun deleteWorkout(workout: Workout) {

        _isLoading.postValue(true)

        workoutRepository.delete(workout)

        loadAllWorkouts()
        _isLoading.postValue(false)
    }

    private suspend fun addNewTimersToWorkout(workoutId: Long): List<WorkoutTimer> {
        val newTimer1 = createTimer(workoutId)
        val newTimer2 = createTimer(workoutId)

        timerRepository.save(newTimer1)
        timerRepository.save(newTimer2)

        return listOf(newTimer1, newTimer2)
    }

    private fun createTimer(workoutId: Long): WorkoutTimer {
        return WorkoutTimer(
            id = 0L,
            workoutId = workoutId,
            minutes = 0,
            seconds = 0
        )
    }

    suspend fun saveTimer(timer: WorkoutTimer) {
        timerRepository.save(timer)
        updateWorkoutValue(timer.workoutId)
    }

    private suspend fun loadAllExercises(): List<Exercise?> {
        allExercise = exerciseRepository.getAll()
        return allExercise
    }

    suspend fun getUniqueExercises() {
        val exercises = loadAllExercises()
        val uniqueExercises = WorkoutDataService().getUniqueExercisesWithMaxMass(exercises)
        _uniqueExercises.value = uniqueExercises
    }

    private fun createNewExercise(workoutId: Long): Exercise {
        return Exercise(
            id = 0L,
            workoutId = workoutId,
            name = null,
            notes = null,
            group = null
        )
    }

    suspend fun addNewExerciseToWorkout(workoutId: Long) {

        val newExercise = createNewExercise(workoutId)

        val savedExercise = exerciseRepository.save(newExercise)
        for (i in 1..4) {
            setRepository.save(createNewSet(savedExercise.id))
        }
        updateWorkoutValue(workoutId)
    }

    suspend fun deleteExercise(exerciseId: Long) {
        _isLoading.postValue(true)

        val exerciseForDelete = exerciseRepository.getById(exerciseId) ?: return
        exerciseRepository.delete(exerciseForDelete)

        updateWorkoutValue(exerciseForDelete.workoutId)

        _isLoading.postValue(false)
    }

    suspend fun getExerciseWithDateByNameAndGroup(exerciseName: String?, exerciseGroup: String?){
        val resultList = mutableListOf<ExerciseWithDate>()
        val allWorkouts = workoutRepository.getAll()

        allWorkouts.forEach { workout ->
            workout.exercises.forEach { exercise ->
                if (exercise.name.equals(exerciseName) && exercise.group.equals(exerciseGroup)) {
                    val resultExercise = ExerciseWithDate(
                        exercise.id,
                        exercise.workoutId,
                        workout.date,
                        exercise.name,
                        exercise.notes,
                        exercise.group,
                        exercise.sets
                    )
                    resultList.add(resultExercise)
                }
            }
        }
        _exercisesWithDates.value = resultList
    }

    private fun createNewSet(exerciseId: Long): Set {
        return Set(id = 0L, exerciseId = exerciseId, mass = 0.0, repeat = 0)
    }

    suspend fun saveExercise(exercise: Exercise): Long {
        return exerciseRepository.save(exercise).id
    }

//    suspend fun loadExercisesDataForGraphic(id: Long) {
//        val exercise = exerciseRepository.getById(id) ?: return
//        if (exercise.name == null) return
//        val exerciseList = if (exercise.group != null) {
//            exerciseRepository.getExerciseByName(exercise.name, exercise.group)
//        } else {
//            exerciseRepository.getExerciseByName(exercise.name, "")
//        }
//        val allWorkouts = workoutRepository.getAll()
//
//        _exercisesGraphData.value =
//            WorkoutDataService().getExercisesInfoForGraphic(exerciseList, allWorkouts)
//    }

    suspend fun saveSet(set: Set) {
        setRepository.save(set)
    }

    suspend fun deleteSet(set: Set) {
        setRepository.delete(set.id)
        val editedExercise = exerciseRepository.getById(set.exerciseId)
        val editedWorkout = editedExercise?.workoutId?.let { workoutRepository.getById(it) }
        editedWorkout?.id?.let { updateWorkoutValue(it) }
    }

    suspend fun addSetToExercise(exerciseId: Long) {
        _isLoading.postValue(true)
        setRepository.save(createNewSet(exerciseId))
        val updatedExercise = exerciseRepository.getById(exerciseId) ?: return
        updateWorkoutValue(updatedExercise.workoutId)
        _isLoading.postValue(false)
    }

    suspend fun copyWorkoutToBuffer(workoutId: Long, context: Context) {
        val findWorkout = workoutRepository.getById(workoutId) ?: return
        val outputString = WorkoutDataService().workoutAsString(findWorkout)

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Workout", outputString)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Workout copied to buffer", Toast.LENGTH_SHORT).show()
    }

    suspend fun loadAllExercisesGroup(context: Context) {
        val exercises = exerciseRepository.getAll()
        _groups.value = WorkoutDataService().getGroupList(context, exercises)
    }

}