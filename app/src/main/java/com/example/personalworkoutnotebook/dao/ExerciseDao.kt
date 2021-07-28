package com.example.personalworkoutnotebook.dao

import androidx.room.*
import com.example.personalworkoutnotebook.model.RoomExercise
import com.example.personalworkoutnotebook.model.RoomExerciseWithApproach

@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: RoomExercise): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(exercise: RoomExercise)

    @Delete
    suspend fun delete(exercise: RoomExercise): Int

    @Transaction
    @Query("SELECT * FROM exercises")
    suspend fun getExercisesWithApproaches(): List<RoomExerciseWithApproach>?

    @Transaction
    @Query("SELECT * FROM exercises WHERE exercise_id = :id")
    suspend fun getOneById(id: Long):RoomExerciseWithApproach?

    @Query("SELECT EXISTS(SELECT * FROM exercises WHERE exercise_id LIKE :id)")
    suspend fun isExist(id: Long): Boolean

}