package com.example.personalworkoutnotebook.repository

import com.example.personalworkoutnotebook.dao.BioParameterDao
import com.example.personalworkoutnotebook.model.BioParameter
import com.example.personalworkoutnotebook.model.toModel
import com.example.personalworkoutnotebook.model.toRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BioParameterRepository @Inject constructor(
    private val bioParameterDao: BioParameterDao
){

    suspend fun getAll(): List<BioParameter>{
        return withContext(Dispatchers.IO){
            val bioParameterList: MutableList<BioParameter> = mutableListOf()
            bioParameterDao.getAllBioParameters()?.forEach { roomBioParameter ->
                bioParameterList.add(roomBioParameter.toModel())
            }

            bioParameterList
        }
    }

    suspend fun getById(id: Long): BioParameter?{
        return bioParameterDao.getOneById(id)?.toModel()
    }

    suspend fun getAllByName(name: String): List<BioParameter>{
        return withContext(Dispatchers.IO){
            val bioParameterList: MutableList<BioParameter> = mutableListOf()
            bioParameterDao.getAllByName(name)?.forEach { roomBioParameter ->
                bioParameterList.add(roomBioParameter.toModel())
            }
            bioParameterList
        }
    }

    suspend fun save(bioParameter: BioParameter): BioParameter {
        return if (bioParameterDao.getOneById(bioParameter.id) != null){
            bioParameterDao.update(bioParameter.toRoom())
            bioParameterDao.getOneById(bioParameter.id)!!.toModel()
        } else {
            val id = bioParameterDao.insert(bioParameter.toRoom())
            bioParameterDao.getOneById(id)!!.toModel()
        }
    }

    suspend fun delete(bioParameter: BioParameter):Boolean {
        if(bioParameterDao.getOneById(bioParameter.id) !=null){
            bioParameterDao.delete(bioParameter.toRoom())
        }
        return !bioParameterDao.isExist(bioParameter.id)
    }
}