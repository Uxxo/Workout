package com.example.personalworkoutnotebook.repository

import com.example.personalworkoutnotebook.dao.ExerciseDao
import com.example.personalworkoutnotebook.model.Exercise
import com.example.personalworkoutnotebook.model.toModel
import com.example.personalworkoutnotebook.model.toRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExerciseRepository @Inject constructor(

    val exerciseDao: ExerciseDao,
    val approachRepository: ApproachRepository
) {

    suspend fun getById(id : Long): Exercise?{
        return withContext(Dispatchers.IO){
            exerciseDao.getOneById(id)?.toModel()
        }
    }

    suspend fun save(exercise: Exercise, workoutId: Long): Exercise{
        return withContext(Dispatchers.IO){
            if(exerciseDao.isExist(exercise.id)){
                exerciseDao.update(exercise.toRoom(workoutId))
                exercise.approaches.forEach{approach -> approachRepository.save(approach, exercise.id)}
                exerciseDao.getOneById(exercise.id)!!.toModel()
            } else {
                val id = exerciseDao.insert(exercise.toRoom(workoutId))
                exercise.approaches.forEach { approach -> approachRepository.save(approach, id) }
                exerciseDao.getOneById(id)!!.toModel()
            }
        }
    }

    suspend fun delete(id: Long): Boolean{
        return withContext(Dispatchers.IO){
            val exercise = exerciseDao.getOneById(id)?.exercise
            if (exercise !=null){
                exerciseDao.delete(exercise)
            }
            !exerciseDao.isExist(id)
        }
    }
}