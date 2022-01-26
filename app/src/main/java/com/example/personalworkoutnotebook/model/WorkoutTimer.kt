package com.example.personalworkoutnotebook.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

data class WorkoutTimer(
    val id: Long,
    val workoutId: Long,
    val minutes: Int,
    val seconds: Int
) : Serializable


@Entity(
    tableName = "timers", foreignKeys = [
        ForeignKey(
            entity = RoomWorkout::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE

        )
    ]
)
data class RoomTimer(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "workout_id", index = true) val workoutId: Long,
    @ColumnInfo(name = "minutes") val minutes: Int,
    @ColumnInfo(name = "seconds") val second: Int
)

fun WorkoutTimer.toRoom() = RoomTimer(
    id = this.id,
    workoutId = this.workoutId,
    minutes = this.minutes,
    second = this.seconds
)

fun RoomTimer.toModel() = WorkoutTimer(
    id = this.id,
    workoutId = this.workoutId,
    minutes = this.minutes,
    seconds = this.second
)

fun WorkoutTimer.isTimeEmpty(): Boolean {
    return (this.minutes == 0 && this.seconds == 0)
}

fun WorkoutTimer.toText(): String {
    return if (this.seconds < 10) {
        "${this.minutes}:0${this.seconds}"
    } else {
        "${this.minutes}:${this.seconds}"
    }
}
