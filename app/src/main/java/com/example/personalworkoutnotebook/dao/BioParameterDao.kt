package com.example.personalworkoutnotebook.dao

import androidx.room.*
import com.example.personalworkoutnotebook.model.RoomBioParameter
import com.example.personalworkoutnotebook.model.RoomBioParameterWithValues

@Dao
interface BioParameterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bioParameter: RoomBioParameter): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(bioParameter: RoomBioParameter)

    @Delete
    suspend fun delete(bioParameter: RoomBioParameter)

    @Transaction
    @Query("SELECT * FROM bio_parameters")
    suspend fun getAllBioParameters(): List<RoomBioParameterWithValues>?

    @Transaction
    @Query("SELECT * FROM bio_parameters WHERE id = :id")
    suspend fun getOneById(id: Long): RoomBioParameterWithValues?

    @Transaction
    @Query("SELECT * FROM bio_parameters WHERE name = :name")
    suspend fun getAllByName(name: String): List<RoomBioParameterWithValues>?

    @Query("SELECT EXISTS(SELECT * FROM bio_parameters WHERE id LIKE :id)")
    suspend fun isExist(id: Long): Boolean
}