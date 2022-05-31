package com.example.personalworkoutnotebook.repository

import com.example.personalworkoutnotebook.dao.BioParameterValueDao
import com.example.personalworkoutnotebook.model.BioParameterValue
import com.example.personalworkoutnotebook.model.toModel
import com.example.personalworkoutnotebook.model.toRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BioParameterValueRepository @Inject constructor(val bioParameterValueDao: BioParameterValueDao) {

    suspend fun getAll(): List<BioParameterValue>{
        return withContext(Dispatchers.IO){
            val bioParamValues: MutableList<BioParameterValue> = mutableListOf()
            bioParameterValueDao.getAllBioParametersValues()?.forEach { roomBioParameterValue ->
                bioParamValues.add(roomBioParameterValue.toModel())
            }
            bioParamValues
        }
    }

    suspend fun getById(id: Long): BioParameterValue?{
        return bioParameterValueDao.getOneById(id)?.toModel()
    }

    suspend fun getAllByBioParameterId(id: Long): List<BioParameterValue>{
        return withContext(Dispatchers.IO){
            val bioParamValues = mutableListOf<BioParameterValue>()
            bioParameterValueDao.getAllByBioParameterId(id)?.forEach {roomBioParameterValue ->
            bioParamValues.add(roomBioParameterValue.toModel())
            }
            bioParamValues
        }
    }

    suspend fun save(bioParameterValue: BioParameterValue): BioParameterValue{
        println()
        return if(bioParameterValueDao.getOneById(bioParameterValue.id) !=null){
            bioParameterValueDao.update(bioParameterValue.toRoom())
            bioParameterValueDao.getOneById(bioParameterValue.id)!!.toModel()
        }else {
            val id = bioParameterValueDao.insert(bioParameterValue.toRoom())
            bioParameterValueDao.getOneById(id)!!.toModel()
        }
    }

    suspend fun delete(bioParameterValue: BioParameterValue): Boolean{
        println()
        if (bioParameterValueDao.getOneById(bioParameterValue.id) != null){
            bioParameterValueDao.delete(bioParameterValue.toRoom())
        }
        return !bioParameterValueDao.isExist(bioParameterValue.id)
    }
}