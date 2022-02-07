package com.example.personalworkoutnotebook.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

data class Set(
    var id:Long = 0L,
    val exerciseId: Long,
    val mass:Double,
    val repeat:Int,
) : Serializable

@Entity(tableName = "set",foreignKeys = [
    ForeignKey(entity = RoomExercise::class,
    parentColumns = ["id"],
    childColumns = ["exercise_id"],
    onDelete = ForeignKey.CASCADE)
])
data class RoomSet(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "mass") val mass: Double,
    @ColumnInfo(name = "repeat") val repeat: Int,
    @ColumnInfo(name = "exercise_id", index = true) val exerciseId: Long
)

fun Set.toRoom() = RoomSet(
    id = this.id,
    mass = this.mass,
    repeat = this.repeat,
    exerciseId = this.exerciseId
)

fun RoomSet.toModel() = Set(
    id = id,
    mass = mass,
    repeat = repeat,
    exerciseId = exerciseId
)



