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
    private val setRepository: SetRepository
) {

    suspend fun getAll(): List<Exercise?> {
        return withContext(Dispatchers.IO) {
            val exerciseList: MutableList<Exercise> = mutableListOf()
            exerciseDao.getExercisesWithSets()?.forEach { roomExerciseWithSet ->
                exerciseList.add(roomExerciseWithSet.toModel())
            }
            exerciseList
        }
    }

    suspend fun getExerciseByName(name: String, group: String): List<Exercise>{
        return withContext(Dispatchers.IO){
            val exerciseList: MutableList<Exercise> = mutableListOf()
            exerciseDao.getExercisesWithSetsByExerciseName(name)?.forEach { roomExerciseWithSet ->
                if(roomExerciseWithSet.exercise.group == group) {
                    exerciseList.add(roomExerciseWithSet.toModel())
                }
            }
            exerciseList
        }
    }

    suspend fun getById(id: Long): Exercise? {
        return withContext(Dispatchers.IO) {
            exerciseDao.getOneById(id)?.toModel()
        }
    }

    suspend fun getByWorkoutId(id: Long): List<Exercise> {
        return withContext(Dispatchers.IO) {
            val exerciseList: MutableList<Exercise> = mutableListOf()
            exerciseDao.getExercisesByWorkoutId(id)?.forEach { roomExerciseWithSet ->
                exerciseList.add(roomExerciseWithSet.toModel())
            }
            return@withContext exerciseList
        }
    }

    suspend fun save(exercise: Exercise): Exercise {
        println()
        return withContext(Dispatchers.IO) {
            if (exerciseDao.getOneById(exercise.id) != null) {
                exerciseDao.update(exercise.toRoom())
                exerciseDao.getOneById(exercise.id)!!.toModel()

            } else {
                val id = exerciseDao.insert(exercise.toRoom())
                exercise.sets.forEach { set -> setRepository.save(set) }
                exerciseDao.getOneById(id)!!.toModel()

            }
        }
    }

    suspend fun delete(exercise: Exercise): Boolean {
        if (exerciseDao.isExist(exercise.id)) {
            exerciseDao.delete(exercise.toRoom())
        }

        return exerciseDao.getOneById(exercise.id) == null
    }

    suspend fun delete(exercises: List<Exercise>) {
        exercises.forEach {
            if (exerciseDao.isExist(it.id)){
                exerciseDao.delete(it.toRoom())
            }
        }
    }
}