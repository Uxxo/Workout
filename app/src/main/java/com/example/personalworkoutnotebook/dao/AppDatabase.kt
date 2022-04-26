package com.example.personalworkoutnotebook.dao

import androidx.room.*
import com.example.personalworkoutnotebook.model.*

@Database(
    version = 1,
    entities = [
        RoomWorkout::class,
        RoomExercise::class,
        RoomSet::class,
        RoomTimer::class,
        RoomBioParameter::class,
        RoomBioParameterValue::class],
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun setDao(): SetDao
    abstract fun timerDao(): TimerDao
    abstract fun bioParameterDao(): BioParameterDao
    abstract fun bioParameterValueDao(): BioParameterValueDao

}