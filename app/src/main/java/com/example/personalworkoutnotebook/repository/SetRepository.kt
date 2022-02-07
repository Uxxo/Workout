package com.example.personalworkoutnotebook.repository

import com.example.personalworkoutnotebook.dao.SetDao
import com.example.personalworkoutnotebook.model.Set
import com.example.personalworkoutnotebook.model.RoomSet
import com.example.personalworkoutnotebook.model.toModel
import com.example.personalworkoutnotebook.model.toRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SetRepository @Inject constructor(
    private val setDao: SetDao
) {

    suspend fun getById(id : Long): Set?{
        return withContext(Dispatchers.IO){
            setDao.getOneById(id)?.toModel()
        }
    }

    suspend fun getAllByExerciseId(exerciseId: Long):List<Set>{
        return withContext(Dispatchers.IO) {
            val setsList : MutableList<Set> = mutableListOf()
            setDao.getAllByExerciseId(exerciseId).forEach { roomSet ->
                setsList.add(roomSet.toModel())
            }
            return@withContext setsList
        }
    }

    suspend fun save(set: Set) : Set {
        return withContext(Dispatchers.IO){
            if(setDao.getOneById(set.id) != null){
                setDao.update(set.toRoom())
                setDao.getOneById(set.id)!!.toModel()
            } else{
                val  id = setDao.insert(set.toRoom())
                setDao.getOneById(id)!!.toModel()
            }
        }
    }

    suspend fun delete(id: Long):Boolean {
        return withContext(Dispatchers.IO){
            val set: RoomSet? = setDao.getOneById(id)
            if (set != null) {
                setDao.delete(set)
            }
            !setDao.isExist(id)
        }
    }

    suspend fun delete(sets : List<Set>){
        sets.forEach {
            if(setDao.isExist(it.id)){
            setDao.delete(it.toRoom())}
        }
    }

}