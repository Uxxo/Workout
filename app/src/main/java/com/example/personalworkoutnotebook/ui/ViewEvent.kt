package com.example.personalworkoutnotebook.ui

import android.content.Context
import android.content.Intent
import com.example.personalworkoutnotebook.model.*
import com.example.personalworkoutnotebook.model.Set

sealed class ViewEvent {
    data class AddSetToExercise(val exerciseId: Long): ViewEvent()
    data class SaveSet(val set: Set) : ViewEvent()
    data class DeleteSet(val exerciseId: Long) : ViewEvent()

    data class SaveExercise(val exercise: Exercise) : ViewEvent()
    data class SaveExerciseWithActualWeight(val exercise: Exercise, val newName: String,
                                            val newGroup:String,val workoutId: Long) : ViewEvent()
    data class DeleteExercise(val exercise: Exercise) : ViewEvent()

    data class DeleteWorkout(val workout: Workout) : ViewEvent()
    data class CopyWorkoutsFields(val workout: Workout) : ViewEvent()
    data class DuplicateWorkout(val workout: Workout) : ViewEvent()
    data class UpdateWorkoutData(val workoutId: Long ): ViewEvent()

    data class StartExerciseInfoActivity(val intent: Intent) : ViewEvent()
    data class StartBioParameterInfoActivity(val intent: Intent) : ViewEvent()

    data class DeleteBioParameter(val bioParameter: BioParameter) : ViewEvent()
    data class SaveBioParameterValue(val value: BioParameterValue) : ViewEvent()
    data class DeleteBioParameterValue(val value: BioParameterValue) : ViewEvent()

    data class ChangeExerciseStatisticStatus(val exerciseId: Long) : ViewEvent()
}
