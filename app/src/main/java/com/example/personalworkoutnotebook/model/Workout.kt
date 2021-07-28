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
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "workout_id") val id: Long,
    @ColumnInfo(name = "date") val date: Calendar,
    @ColumnInfo(name = "name") val name:String
)

data class RoomWorkoutWithExercises(
    @Embedded val workout: RoomWorkout,
    @Relation(parentColumn = "workout_Id", entityColumn = "workout_id")
    val exercises : List<RoomExercise>
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

fun RoomWorkoutWithExercises.toModel(): Workout = Workout(
    id = this.workout.id,
    date = this.workout.date,
    name = this.workout.name,
    exercises = listOf() //this.exercises.map{it.toModel()}
)