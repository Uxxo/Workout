package com.example.personalworkoutnotebook.dao

import androidx.room.*
import com.example.personalworkoutnotebook.model.RoomBioParameterValue

@Dao
interface BioParameterValueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: RoomBioParameterValue): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(value: RoomBioParameterValue)

    @Delete
    suspend fun delete(value: RoomBioParameterValue)

    @Transaction
    @Query("SELECT  * FROM bio_value")
    suspend fun getAllBioParametersValues(): List<RoomBioParameterValue>?

    @Transaction
    @Query("SELECT * FROM bio_value WHERE id = :id")
    suspend fun getOneById(id: Long): RoomBioParameterValue?

    @Transaction
    @Query("SELECT * FROM bio_value WHERE bioParameterId = :id")
    suspend fun getAllByBioParameterId(id: Long): List<RoomBioParameterValue>?

    @Query("SELECT EXISTS(SELECT * FROM bio_value WHERE id LIKE :id )")
    suspend fun isExist(id: Long): Boolean
}