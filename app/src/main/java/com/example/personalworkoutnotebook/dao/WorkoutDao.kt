package com.example.personalworkoutnotebook.dao

import androidx.room.*
import com.example.personalworkoutnotebook.model.RoomWorkout
import com.example.personalworkoutnotebook.model.RoomWorkoutWithTimersExercisesAndSets

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: RoomWorkout): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(workout: RoomWorkout)

    @Delete
    suspend fun delete(workout: RoomWorkout)

    @Transaction
    @Query("SELECT * FROM workouts")
    suspend fun getWorkoutWithExercises(): List<RoomWorkoutWithTimersExercisesAndSets>?

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getOneById(id: Long): RoomWorkoutWithTimersExercisesAndSets?

    @Query("SELECT EXISTS(SELECT * FROM workouts WHERE id LIKE :id)")
    suspend fun isExist(id: Long): Boolean
}