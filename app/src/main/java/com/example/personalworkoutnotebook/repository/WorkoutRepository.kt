package com.example.personalworkoutnotebook.repository

import com.example.personalworkoutnotebook.dao.WorkoutDao
import com.example.personalworkoutnotebook.model.Workout
import com.example.personalworkoutnotebook.model.toModel
import com.example.personalworkoutnotebook.model.toRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkoutDao
) {

    suspend fun getAll(): List<Workout> {
        return withContext(Dispatchers.IO) {
            val workoutList: MutableList<Workout> = mutableListOf()
            workoutDao.getWorkoutWithExercises()?.forEach { roomWorkoutWithExercises ->
                workoutList.add(roomWorkoutWithExercises.toModel())
            }
            workoutList
        }
    }

    suspend fun getById(id: Long): Workout? {
        return workoutDao.getOneById(id)?.toModel()

    }

    suspend fun save(workout: Workout): Workout {
        return if (workoutDao.getOneById(workout.id) != null) {
            workoutDao.update(workout.toRoom())
            workoutDao.getOneById(workout.id)!!.toModel()
        } else {
            val id = workoutDao.insert(workout.toRoom())
            workoutDao.getOneById(id)!!.toModel()
        }

    }

    suspend fun delete(workout: Workout): Boolean {
        if (workoutDao.getOneById(workout.id) != null) {
            workoutDao.delete(workout.toRoom())
        }
        return !workoutDao.isExist(workout.id)
    }
}