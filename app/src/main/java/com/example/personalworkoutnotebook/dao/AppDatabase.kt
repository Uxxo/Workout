package com.example.personalworkoutnotebook.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.personalworkoutnotebook.model.*

@Database(
    entities = [
        RoomWorkout::class,
        RoomExercise::class,
        RoomApproach::class,
        RoomTimer::class,
        RoomBioParameter::class,
        RoomBioParameterValue::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun approachDao(): ApproachDao
    abstract fun timerDao(): TimerDao
    abstract fun bioParameterDao(): BioParameterDao
    abstract fun bioParameterValueDao(): BioParameterValueDao

}