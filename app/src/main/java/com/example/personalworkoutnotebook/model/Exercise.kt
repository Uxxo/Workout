package com.example.personalworkoutnotebook.model

import androidx.room.*
import com.example.personalworkoutnotebook.extension.toFirstUpperCase

data class Exercise(
    val id: Long = 0L,
    val workoutId: Long,
    val name: String,
    val notes: String?,
    val group: String?,
    val approaches: List<Approach> = listOf()
)

@Entity(tableName = "exercises", foreignKeys = [
    ForeignKey(entity = RoomWorkout::class,
                parentColumns = ["id"],
                childColumns = ["workout_id"],
                onDelete = ForeignKey.CASCADE)])
data class RoomExercise(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "group") val group: String?,
    @ColumnInfo(name = "workout_id", index = true) val workoutId: Long
)

data class RoomExerciseWithApproach(
    @Embedded val exercise: RoomExercise,
    @Relation(parentColumn = "id", entityColumn = "exercise_id", entity = RoomApproach::class)
    val approaches: List<RoomApproach>
)


fun Exercise.toRoom() = RoomExercise(
    id = this.id,
    name = this.name.toFirstUpperCase(),
    notes = this.notes,
    group = if (this.group != null) this.group.toFirstUpperCase()
            else null,
    workoutId = this.workoutId
)

fun RoomExerciseWithApproach.toModel(): Exercise = Exercise(
    id = this.exercise.id,
    workoutId = this.exercise.workoutId,
    name = this.exercise.name,
    notes = this.exercise.notes,
    group = this.exercise.group,
    approaches = this.approaches.map { it.toModel() }
)



fun Exercise.getMaxMass(): Double{
    var maxMass = if(approaches.isNotEmpty() && approaches[0].repeat !=0) approaches[0].mass
                    else -1000.0

    approaches.forEach { approach ->
        if( approach.mass > maxMass && approach.repeat != 0) maxMass = approach.mass
    }

    return if(maxMass == -1000.0) 0.0
            else maxMass
}

