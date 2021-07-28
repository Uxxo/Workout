package com.example.personalworkoutnotebook.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class Approach(
    var id:Long = 0L,
    val mass:Double,
    val repeat:Int,
) : Serializable

@Entity(tableName = "approaches")
data class RoomApproach(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "approach_id") val id: Long,
    @ColumnInfo(name = "mass") val mass: Double,
    @ColumnInfo(name = "repeat") val repeat: Int,
    @ColumnInfo(name = "exercise_id") val exerciseId: Long
)

fun Approach.toRoom(exerciseId: Long) = RoomApproach(
    id = this.id,
    mass = this.mass,
    repeat = this.repeat,
    exerciseId = exerciseId
)

fun RoomApproach.toModel() = Approach(
    id = this.id,
    mass = this.mass,
    repeat = this.repeat,

)