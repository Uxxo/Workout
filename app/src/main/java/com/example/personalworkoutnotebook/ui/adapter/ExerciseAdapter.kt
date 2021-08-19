package com.example.personalworkoutnotebook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.databinding.ItemExerciseBinding
import com.example.personalworkoutnotebook.extension.afterTextChanged
import com.example.personalworkoutnotebook.model.Approach
import com.example.personalworkoutnotebook.model.Exercise
import com.google.android.material.textfield.TextInputLayout

class ExerciseAdapter(
    private val exercises: MutableList<Exercise>,
    val exerciseCallback: (exercise: Exercise) -> Unit,
    val approachCallback: (approach: Approach) -> Unit
) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ExerciseHolder(
            ItemExerciseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: ExerciseHolder, position: Int) {
        holder.bind(exercises[position])
    }


    override fun getItemCount(): Int {
        return exercises.size
    }

    inner class ExerciseHolder(private val exerciseBinding: ItemExerciseBinding) :
        RecyclerView.ViewHolder(exerciseBinding.root) {

        fun bind(exercise: Exercise) {
            exerciseBinding.exerciseName.editText?.setText(exercise.name)
            exerciseBinding.exerciseName.editText?.afterTextChanged {
                val index = exercises.indexOf(exercise)
                val editedExercise = exercise.copy(name = it)
                exercises.removeAt(index)
                exercises.add(index, editedExercise)
                exerciseCallback.invoke(editedExercise)
            }
            exerciseBinding.exerciseNote.editText?.setText(exercise.notes)
            exerciseBinding.exerciseNote.editText?.afterTextChanged {
                val index = exercises.indexOf(exercise)
                val editedExercise = exercise.copy(notes = it)
                exercises.removeAt(index)
                exercises.add(index, editedExercise)
                exerciseCallback.invoke(editedExercise)
            }
            val layouts = listOf(
                Pair(exerciseBinding.approachRepeats1, exerciseBinding.approachMass1),
                Pair(exerciseBinding.approachRepeats2, exerciseBinding.approachMass2),
                Pair(exerciseBinding.approachRepeats3, exerciseBinding.approachMass3),
                Pair(exerciseBinding.approachRepeats4, exerciseBinding.approachMass4),
                Pair(exerciseBinding.approachRepeats5, exerciseBinding.approachMass5),
                Pair(exerciseBinding.approachRepeats6, exerciseBinding.approachMass6),
                Pair(exerciseBinding.approachRepeats7, exerciseBinding.approachMass7),
                Pair(exerciseBinding.approachRepeats8, exerciseBinding.approachMass8)
            )
            exercise.approaches.forEachIndexed { index, approach ->
                saveChangesInApproach(layouts[index], approach, exercise)
            }

        }

        private fun saveChangesInApproach(
            layouts: Pair<TextInputLayout, TextInputLayout>,
            approach: Approach,
            exercise: Exercise
        ) {
            var editedApproach: Approach?
            layouts.first.editText?.setText(approach.repeat)
            layouts.first.editText?.afterTextChanged {
                editedApproach = approach.copy(repeat = it.toInt())
                saveData(editedApproach, exercise, approach)
            }

            layouts.second.editText?.setText(approach.mass.toString())
            layouts.second.editText?.afterTextChanged {
                editedApproach = approach.copy(mass = it.toDouble())
                saveData(editedApproach, exercise, approach)
            }

        }

        private fun saveData(
            editedApproach: Approach?, exercise: Exercise, approach: Approach) {
            val approachIndex = exercise.approaches.indexOf(approach)
            exercise.approaches.removeAt(approachIndex)
            exercise.approaches.add(approachIndex, approach)
            approachCallback.invoke(editedApproach!!)
        }
    }

}