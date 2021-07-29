package com.example.personalworkoutnotebook.model

import androidx.room.*

data class Exercise(
    val id: Long = 0L,
    val name: String,
    val notes: String?,
    val approaches: List<Approach> = listOf()
)

@Entity(tableName = "exercises")
data class RoomExercise(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "workout_id") val workoutId: Long
)

data class RoomExerciseWithApproach(
    @Embedded val exercise: RoomExercise,
    @Relation(parentColumn = "id", entityColumn = "exercise_id", entity = RoomApproach::class)
    val approaches: List<RoomApproach>
)

fun Exercise.toRoom(workoutId: Long) = RoomExercise(
    id = this.id,
    name = this.name,
    notes = this.notes,
    workoutId = workoutId
)

fun RoomExerciseWithApproach.toModel(): Exercise = Exercise(
    id = this.exercise.id,
    name = this.exercise.name,
    notes = this.exercise.notes,
    approaches =  this.approaches.map { it.toModel() }
)