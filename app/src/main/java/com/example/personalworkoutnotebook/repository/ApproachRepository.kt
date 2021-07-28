package com.example.personalworkoutnotebook.repository

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

    suspend fun save(approach: Approach, exerciseId : Long) : Approach{
        return withContext(Dispatchers.IO){
            if(approachDao.isExist(approach.id)){
                approachDao.update(approach.toRoom(exerciseId))
                approachDao.getOneById(approach.id)!!.toModel()
            } else{
                val  id = approachDao.insert(approach.toRoom(exerciseId))
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
}