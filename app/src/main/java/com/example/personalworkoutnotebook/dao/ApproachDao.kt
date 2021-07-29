package com.example.personalworkoutnotebook.dao

import androidx.room.*
import com.example.personalworkoutnotebook.model.RoomApproach

@Dao
interface ApproachDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(approach: RoomApproach): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(approach: RoomApproach)

    @Delete
    suspend fun delete(approach: RoomApproach)

    @Transaction
    @Query("SELECT * FROM approaches")
    suspend fun getApproaches(): List<RoomApproach>?

    @Transaction
    @Query("SELECT * FROM approaches WHERE id = :id")
    suspend fun getOneById(id: Long): RoomApproach?

    @Transaction
    @Query("SELECT * FROM approaches WHERE exercise_id = :exerciseId")
    suspend fun getAllByExerciseId(exerciseId: Long): List<RoomApproach>

    @Query("SELECT EXISTS(SELECT * FROM approaches WHERE id LIKE :id)")
    suspend fun isExist(id: Long): Boolean
}