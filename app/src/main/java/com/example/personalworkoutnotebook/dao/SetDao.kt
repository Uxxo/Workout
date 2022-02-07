package com.example.personalworkoutnotebook.dao

import androidx.room.*
import com.example.personalworkoutnotebook.model.RoomSet

@Dao
interface SetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: RoomSet): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(set: RoomSet)

    @Delete
    suspend fun delete(set: RoomSet)

    @Transaction
    @Query("SELECT * FROM `set`")
    suspend fun getSets(): List<RoomSet>?

    @Transaction
    @Query("SELECT * FROM `set` WHERE id = :id")
    suspend fun getOneById(id: Long): RoomSet?

    @Transaction
    @Query("SELECT * FROM `set` WHERE exercise_id = :exerciseId")
    suspend fun getAllByExerciseId(exerciseId: Long): List<RoomSet>

    @Query("SELECT EXISTS(SELECT * FROM `set` WHERE id LIKE :id)")
    suspend fun isExist(id: Long): Boolean
}