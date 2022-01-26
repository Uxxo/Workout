package com.example.personalworkoutnotebook.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

data class Approach(
    var id:Long = 0L,
    val exerciseId: Long,
    val mass:Double,
    val repeat:Int,
) : Serializable

@Entity(tableName = "approaches",foreignKeys = [
    ForeignKey(entity = RoomExercise::class,
    parentColumns = ["id"],
    childColumns = ["exercise_id"],
    onDelete = ForeignKey.CASCADE)
])
data class RoomApproach(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "mass") val mass: Double,
    @ColumnInfo(name = "repeat") val repeat: Int,
    @ColumnInfo(name = "exercise_id", index = true) val exerciseId: Long
)

fun Approach.toRoom() = RoomApproach(
    id = this.id,
    mass = this.mass,
    repeat = this.repeat,
    exerciseId = this.exerciseId
)

fun RoomApproach.toModel() = Approach(
    id = this.id,
    mass = this.mass,
    repeat = this.repeat,
    exerciseId = this.exerciseId
)



