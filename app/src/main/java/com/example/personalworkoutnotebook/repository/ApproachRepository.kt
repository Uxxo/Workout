package com.example.personalworkoutnotebook.repository

import com.example.personalworkoutnotebook.App
import com.example.personalworkoutnotebook.dao.ApproachDao
import com.example.personalworkoutnotebook.model.Approach
import com.example.personalworkoutnotebook.model.RoomApproach
import com.example.personalworkoutnotebook.model.toModel
import com.example.personalworkoutnotebook.model.toRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ApproachRepository @Inject constructor(
    private val approachDao: ApproachDao
) {

    suspend fun getById(id : Long): Approach?{
        return withContext(Dispatchers.IO){
            approachDao.getOneById(id)?.toModel()
        }
    }

    suspend fun getAllByExerciseId(exerciseId: Long):List<Approach>{
        return withContext(Dispatchers.IO) {
            val approachesList : MutableList<Approach> = mutableListOf()
            approachDao.getAllByExerciseId(exerciseId).forEach { roomApproach ->
                approachesList.add(roomApproach.toModel())
            }
            return@withContext approachesList
        }
    }

    suspend fun save(approach: Approach) : Approach{
        return withContext(Dispatchers.IO){
            if(approachDao.getOneById(approach.id) != null){
                approachDao.update(approach.toRoom())
                approachDao.getOneById(approach.id)!!.toModel()
            } else{
                val  id = approachDao.insert(approach.toRoom())
                approachDao.getOneById(id)!!.toModel()
            }
        }
    }

    suspend fun delete(id: Long):Boolean {
        return withContext(Dispatchers.IO){
            val approach: RoomApproach? = approachDao.getOneById(id)
            if (approach != null) {
                approachDao.delete(approach)
            }
            !approachDao.isExist(id)
        }
    }

    suspend fun delete(approaches : List<Approach>){
        approaches.forEach {
            if(approachDao.isExist(it.id)){
            approachDao.delete(it.toRoom())}
        }
    }

}