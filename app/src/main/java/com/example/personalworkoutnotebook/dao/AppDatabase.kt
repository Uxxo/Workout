package com.example.personalworkoutnotebook.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.personalworkoutnotebook.model.RoomApproach
import com.example.personalworkoutnotebook.model.RoomExercise
import com.example.personalworkoutnotebook.model.RoomWorkout

@Database(entities = [RoomWorkout::class, RoomExercise::class, RoomApproach::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun approachDao(): ApproachDao

}