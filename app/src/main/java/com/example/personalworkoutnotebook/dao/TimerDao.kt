package com.example.personalworkoutnotebook.dao

import androidx.room.*
import com.example.personalworkoutnotebook.model.RoomTimer

@Dao
interface TimerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timer : RoomTimer): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(timer : RoomTimer)

    @Delete
    suspend fun delete(timer : RoomTimer)

    @Transaction
    @Query("SELECT * FROM timers WHERE id = :id")
    suspend fun getOneById(id : Long): RoomTimer?

    @Query("SELECT EXISTS(SELECT * FROM timers WHERE id LIKE :id)")
    suspend fun isExist(id: Long): Boolean
}