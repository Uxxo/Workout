package com.example.personalworkoutnotebook.model

import androidx.room.*
import com.example.personalworkoutnotebook.extension.toFirstUpperCase
import kotlin.math.max

data class Exercise(
    val id: Long = 0L,
    val workoutId: Long,
    val name: String?,
    val notes: String?,
    val group: String?,
    val sets: List<Set> = listOf()
)

@Entity(tableName = "exercises", foreignKeys = [
    ForeignKey(entity = RoomWorkout::class,
                parentColumns = ["id"],
                childColumns = ["workout_id"],
                onDelete = ForeignKey.CASCADE)])
data class RoomExercise(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "group") val group: String?,
    @ColumnInfo(name = "workout_id", index = true) val workoutId: Long
)

data class RoomExerciseWithSet(
    @Embedded val exercise: RoomExercise,
    @Relation(parentColumn = "id", entityColumn = "exercise_id", entity = RoomSet::class)
    val sets: List<RoomSet>
)


fun Exercise.toRoom() = RoomExercise(
    id = this.id,
    name = this.name?.toFirstUpperCase(),
    notes = this.notes,
    group = this.group?.toFirstUpperCase(),
    workoutId = this.workoutId
)

fun RoomExerciseWithSet.toModel(): Exercise = Exercise(
    id = this.exercise.id,
    workoutId = this.exercise.workoutId,
    name = this.exercise.name,
    notes = this.exercise.notes,
    group = this.exercise.group,
    sets = this.sets.map { it.toModel() }
)



fun Exercise.getMaxMass(): Double{
    var maxMass = if(sets.isNotEmpty() && sets[0].repeat !=0) sets[0].mass
                    else -1000.0

    sets.forEach { set ->
        if( set.mass > maxMass && set.repeat != 0) maxMass = set.mass
    }

    return if(maxMass == -1000.0) 0.0
            else maxMass
}

fun Exercise.getMaxRepeat(incomingMass:Double): Int{
    var maxRepeat = 0
    sets.forEach { set ->
        if(set.mass == incomingMass && set.repeat > maxRepeat) maxRepeat = set.repeat
    }
    return maxRepeat
}

