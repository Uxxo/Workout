package com.example.personalworkoutnotebook.ui

import com.example.personalworkoutnotebook.model.Approach
import com.example.personalworkoutnotebook.model.Exercise
import com.example.personalworkoutnotebook.model.Workout

sealed class ViewEvent {
    data class AddApproachToExercise(val exercise: Exercise): ViewEvent()
    data class SaveApproach(val approach: Approach) : ViewEvent()
    data class DeleteApproach(val approach: Approach) : ViewEvent()

    data class SaveExercise(val exercise: Exercise) : ViewEvent()
    data class DeleteExercise(val exercise: Exercise) : ViewEvent()

    data class DeleteWorkout(val workout: Workout) : ViewEvent()
    data class UpdateWorkoutData(val any: Any) : ViewEvent()

}
