package com.example.personalworkoutnotebook.dao

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun provideWorkoutDao(): WorkoutDao = db.workoutDao()

    @Provides
    @Singleton
    fun provideExerciseDao(): ExerciseDao = db.exerciseDao()

    @Provides
    @Singleton
    fun provideSetDao(): SetDao = db.setDao()

    @Provides
    @Singleton
    fun provideTimerDao(): TimerDao = db.timerDao()

    @Provides
    @Singleton
    fun provideBioParameterDao(): BioParameterDao = db.bioParameterDao()

    @Provides
    @Singleton
    fun provideBioParameterValueDao(): BioParameterValueDao = db.bioParameterValueDao()

    private lateinit var db: AppDatabase
    fun initDb(context: Context) {
        if (::db.isInitialized) throw IllegalStateException("Room instance already initialized!")
        db = Room.databaseBuilder(context, AppDatabase::class.java, "workouts-db").build()
    }
}