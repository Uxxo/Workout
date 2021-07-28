package com.example.personalworkoutnotebook.dao

import androidx.room.*
import com.example.personalworkoutnotebook.model.RoomWorkout
import com.example.personalworkoutnotebook.model.RoomWorkoutWithExercises

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: RoomWorkout): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(workout: RoomWorkout)

    @Delete
    suspend fun delete(workout: RoomWorkout): Int

    @Transaction
    @Query("SELECT * FROM workouts")
    suspend fun getWorkoutWithExercises(): List<RoomWorkoutWithExercises>?

    @Transaction
    @Query("SELECT * FROM workouts WHERE workout_id = :id")
    suspend fun getOneById(id: Long): RoomWorkoutWithExercises?

    @Query("SELECT EXISTS(SELECT * FROM workouts WHERE workout_id LIKE :id)")
    suspend fun isExist(id: Long): Boolean
}