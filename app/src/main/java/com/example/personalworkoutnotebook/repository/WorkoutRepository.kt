package com.example.personalworkoutnotebook.repository

import com.example.personalworkoutnotebook.dao.WorkoutDao
import com.example.personalworkoutnotebook.model.Workout
import com.example.personalworkoutnotebook.model.toModel
import com.example.personalworkoutnotebook.model.toRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
    val workoutDao: WorkoutDao,
    val exerciseRepository: ExerciseRepository
) {

    suspend fun getAll():List<Workout>{
        return withContext(Dispatchers.IO) {
            val workoutList: MutableList<Workout> = mutableListOf()
            workoutDao.getWorkoutWithExercises()?.forEach { roomWorkoutWithExercises ->
                workoutList.add(roomWorkoutWithExercises.toModel())
            }
            workoutList
        }
    }

    suspend fun getById(id: Long): Workout? {
        return withContext(Dispatchers.IO) {
            workoutDao.getOneById(id)?.toModel()
        }
    }

    suspend fun save(workout: Workout) : Workout{
        return withContext(Dispatchers.IO) {
            if (workoutDao.isExist(workout.id)) {
                workoutDao.update(workout.toRoom())
                workout.exercises.forEach { exercise -> exerciseRepository.save(exercise, workout.id) }
                workoutDao.getOneById(workout.id)!!.toModel()
            } else {
                val id = workoutDao.insert(workout.toRoom())
                workout.exercises.forEach { exercise -> exerciseRepository.save(exercise,id) }
                workoutDao.getOneById(id)!!.toModel()
            }
        }
    }

    suspend fun delete(id: Long): Boolean{
        return withContext(Dispatchers.IO){
            val workout = workoutDao.getOneById(id)?.workout
            if (workout !=null){
                workoutDao.delete(workout)
            }
            !workoutDao.isExist(id)
        }
    }
}