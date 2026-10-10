package com.example.personalworkoutnotebook.dao

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        db = Room.databaseBuilder(context, AppDatabase::class.java, "workouts-db"
        ).addMigrations(MIGRATION_1_2)
            .build()
    }

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE exercises ADD COLUMN is_included_in_statistics INTEGER NOT NULL DEFAULT 1"
            )
        }
    }


    suspend fun prepareDbForExport() = withContext(Dispatchers.IO) {
        if (!::db.isInitialized) return@withContext

        if (db.isOpen) {
            db.close()
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