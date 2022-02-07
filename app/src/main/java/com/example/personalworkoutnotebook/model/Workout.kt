package com.example.personalworkoutnotebook.model

import androidx.room.*
import com.example.personalworkoutnotebook.extension.toFirstUpperCase
import java.io.Serializable
import java.util.*


data class Workout (
    val id:Long = 0,
    val date: Calendar,
    val name: String,
    val exercises: List<Exercise> = mutableListOf(),
    val timers: List<WorkoutTimer> = mutableListOf(),
    val status: Int

) : Serializable {
    companion object {
        const val CREATED = 1
        const val IN_PROCESS = 2
        const val FINISHED = 3

        const val DEFAULT = -1
    }
}


@Entity(tableName = "workouts")
@TypeConverters(DateConverter::class)
data class RoomWorkout(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "date") val date: Calendar,
    @ColumnInfo(name = "name") val name:String,
    @ColumnInfo(name = "status") val status: Int
)


data class RoomWorkoutWithTimersExercisesAndSets(
    @Embedded val roomWorkout: RoomWorkout,
    @Relation(parentColumn = "id", entityColumn = "workout_id", entity = RoomExercise::class)
    val exercises : List<RoomExerciseWithSet>,

    @Relation(parentColumn = "id", entityColumn = "workout_id", entity = RoomTimer::class)
    val timers: List<RoomTimer>
)

@Suppress("unused")
object DateConverter {
    @TypeConverter fun fromTimestamp(value: Long): Calendar = Calendar.getInstance().apply { timeInMillis = value }
    @TypeConverter fun dateToTimestamp(calendar: Calendar): Long = calendar.timeInMillis
}

fun Workout.toRoom() = RoomWorkout(
    id = this.id,
    date = this.date,
    name = this.name.toFirstUpperCase(),
    status = this.status
)

fun RoomWorkoutWithTimersExercisesAndSets.toModel() = Workout(
    id = this.roomWorkout.id,
    date = this.roomWorkout.date,
    name = this.roomWorkout.name,
    status = this.roomWorkout.status,
    timers = this.timers.map { it.toModel() },
    exercises = this.exercises.map { it.toModel() }
)

