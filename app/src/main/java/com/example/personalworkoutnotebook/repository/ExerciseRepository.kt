package com.example.personalworkoutnotebook.repository

import com.example.personalworkoutnotebook.dao.ExerciseDao
import com.example.personalworkoutnotebook.model.Exercise
import com.example.personalworkoutnotebook.model.toModel
import com.example.personalworkoutnotebook.model.toRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExerciseRepository @Inject constructor(

    private val exerciseDao: ExerciseDao,
    private val approachRepository: ApproachRepository
) {

    suspend fun getById(id : Long): Exercise?{
        return withContext(Dispatchers.IO){
            exerciseDao.getOneById(id)?.toModel()
        }
    }

    suspend fun getByWorkoutId(id: Long) : List<Exercise>{
        return withContext(Dispatchers.IO){
            val exerciseList : MutableList<Exercise> = mutableListOf()
            exerciseDao.getExercisesByWorkoutId(id)?.forEach { roomExerciseWithApproach ->
                exerciseList.add(roomExerciseWithApproach.toModel())
            }
            return@withContext exerciseList
        }
    }

    suspend fun save(exercise: Exercise): Exercise{
        return withContext(Dispatchers.IO){
            if(exerciseDao.isExist(exercise.id)){
                exerciseDao.update(exercise.toRoom())
                exercise.approaches.forEach{approach -> approachRepository.save(approach)}

                exerciseDao.getOneById(exercise.id)!!.toModel()

            } else {
                val id = exerciseDao.insert(exercise.toRoom())
                exercise.approaches.forEach { approach -> approachRepository.save(approach) }
                exerciseDao.getOneById(id)!!.toModel()
            }
        }
    }

    suspend fun delete(exercise: Exercise): Boolean{
        return withContext(Dispatchers.IO){
            approachRepository.delete(exercise.approaches)
            val exerciseForDelete = exerciseDao.getOneById(exercise.id)?.exercise
            if (exerciseForDelete !=null) exerciseDao.delete(exerciseForDelete)


            !exerciseDao.isExist(exercise.id)
        }
    }
}