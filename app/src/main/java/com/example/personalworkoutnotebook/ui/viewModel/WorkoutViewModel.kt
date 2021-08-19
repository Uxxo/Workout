package com.example.personalworkoutnotebook.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.personalworkoutnotebook.DBFactory
import com.example.personalworkoutnotebook.model.Workout
import com.example.personalworkoutnotebook.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val dbFactory: DBFactory

) : ViewModel() {
    private var _loadingIndicator = MutableLiveData<Boolean>()
    val loadingIndicator: LiveData<Boolean>
        get() = _loadingIndicator

    // Скрытый объект LiveData, содержимое которого можно менять
    private var _workouts = MutableLiveData<List<Workout>>()
    // Объект-фасад, используемый снаружи (getter)
    val workouts: LiveData<List<Workout>>
        get() = _workouts

    private var _workout = MutableLiveData<Workout>()
    val workout: LiveData<Workout>
    get() = _workout


    suspend fun loadWorkoutById(id : Long){
        _workout.postValue(workoutRepository.getById(id))
    }

    suspend fun loadData() {
        _loadingIndicator.postValue(true)

        // загружаем данные и помещаем их в объект LiveData
        _workouts.postValue(workoutRepository.getAll().sortedByDescending { it.date.timeInMillis })

        _loadingIndicator.postValue(false)

    }

    suspend fun createTestDB(){
        dbFactory.testCreateDB()
    }
}