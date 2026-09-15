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

    fun prepareDbForExport() {
        if (::db.isInitialized && db.isOpen) {
            val sDb = db.openHelper.writableDatabase

            // На некоторых прошивках Huawei выполнение через execSQL генерирует ложное исключение с кодом SQLITE_OK.
            // Оборачиваем каждую команду в индивидуальный try-catch, игнорируя сообщения с текстом "SQLITE_OK".
            try {
                sDb.execSQL("PRAGMA journal_mode = DELETE;")
            } catch (e: Exception) {
                if (e.message?.contains("SQLITE_OK") == false) {
                    throw e // Пробрасываем наверх только НАСТОЯЩУЮ ошибку
                }
            }

            try {
                sDb.execSQL("PRAGMA journal_mode = WAL;")
            } catch (e: Exception) {
                if (e.message?.contains("SQLITE_OK") == false) {
                    throw e
                }
            }
        }
    }


    fun checkpointDb() {
        if (::db.isInitialized && db.isOpen) {
            val query = androidx.sqlite.db.SimpleSQLiteQuery("PRAGMA wal_checkpoint(FULL);")
            db.openHelper.writableDatabase.query(query).close()
        }
    }

    fun closeDb() {
        if (::db.isInitialized && db.isOpen) {
            db.close()
        }
    }
}