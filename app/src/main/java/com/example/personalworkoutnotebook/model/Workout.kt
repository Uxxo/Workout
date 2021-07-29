package com.example.personalworkoutnotebook.model

import androidx.room.*
import java.io.Serializable
import java.util.*


data class Workout (
    val id:Long = 0,
    val date: Calendar,
    val name: String,
    val exercises: List<Exercise> = listOf()
) : Serializable

@Entity(tableName = "workouts")
@TypeConverters(DateConverter::class)
data class RoomWorkout(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "date") val date: Calendar,
    @ColumnInfo(name = "name") val name:String
)


data class RoomWorkoutWithExercisesAndApproaches(
    @Embedded val roomWorkout: RoomWorkout,
    @Relation(parentColumn = "id", entityColumn = "workout_id", entity = RoomExercise::class)
    val exercises : List<RoomExerciseWithApproach>
)

@Suppress("unused")
object DateConverter {
    @TypeConverter fun fromTimestamp(value: Long): Calendar = Calendar.getInstance().apply { timeInMillis = value }
    @TypeConverter fun dateToTimestamp(calendar: Calendar): Long = calendar.timeInMillis
}

fun Workout.toRoom() = RoomWorkout(
    id = this.id,
    date = this.date,
    name = this.name
)

fun RoomWorkoutWithExercisesAndApproaches.toModel(): Workout = Workout(
    id = this.roomWorkout.id,
    date = this.roomWorkout.date,
    name = this.roomWorkout.name,
    exercises = this.exercises.map { it.toModel() }
)