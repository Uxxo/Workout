package com.example.personalworkoutnotebook.ui.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.databinding.ItemExerciseBinding
import com.example.personalworkoutnotebook.extension.afterTextChanged
import com.example.personalworkoutnotebook.model.Exercise
import com.example.personalworkoutnotebook.ui.ViewEvent

class ExerciseAdapter(
    private val exerciseList: MutableList<Exercise>,
    private val callback: (event: ViewEvent) -> Unit
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


    override fun onBindViewHolder(holder: ExerciseHolder, position: Int) =
        holder.bind(exerciseList[position])



    override fun getItemCount(): Int {
        return exerciseList.size
    }

    inner class ExerciseHolder(private val exerciseBinding: ItemExerciseBinding) :
        RecyclerView.ViewHolder(exerciseBinding.root) {

        private lateinit var approachRecycle: RecyclerView


        fun bind(exercise: Exercise) {

                exerciseBinding.deleteExercise.setOnClickListener {
                    with(itemView.context) {
                        AlertDialog.Builder(this)
                            .setTitle("Are you shore?")
                            .setMessage("This exercise will be permanently deleted")
                            .setPositiveButton(android.R.string.ok) { _, _ ->
                                callback.invoke(ViewEvent.DeleteExercise(exercise))

                                notifyItemRemoved(adapterPosition)
                            }
                            .setNegativeButton(android.R.string.cancel, null)
                            .show()
                    }
                }


                exerciseBinding.exerciseName.editText?.setText(exercise.name)
                exerciseBinding.exerciseName.editText?.afterTextChanged { text ->
                    val editedExercise = exercise.copy(name = text)
                    val index = exerciseList.indexOf(exerciseList.first{it.id == exercise.id })
                    exerciseList.removeAt(index)
                    exerciseList.add(index,editedExercise)
                    callback.invoke(ViewEvent.SaveExercise(editedExercise))
                }
                exerciseBinding.exerciseName.setOnFocusChangeListener { _, hasFocus ->
                    if(!hasFocus) callback.invoke(ViewEvent.UpdateWorkoutData(exercise))
                }

                exerciseBinding.approachRecycler.adapter = ApproachAdapter(mutableListOf(), callback)

                val approachAdapter = ApproachAdapter(exercise.approaches as MutableList, callback)
                approachRecycle = exerciseBinding.approachRecycler
                approachRecycle.adapter = approachAdapter

                exerciseBinding.exerciseNote.editText?.setText(exercise.notes)
                exerciseBinding.exerciseNote.editText?.afterTextChanged { text ->
                    val editedExercise = exercise.copy(notes = text)
                    val index = exerciseList.indexOf(exerciseList.first { it.id == exercise.id })
                    exerciseList.removeAt(index)
                    exerciseList.add(index,editedExercise)
                    callback.invoke(ViewEvent.SaveExercise(editedExercise))
                }

                exerciseBinding.exerciseNote.editText?.onFocusChangeListener = View.OnFocusChangeListener{_, hasFocus ->
                    if(!hasFocus){callback.invoke(ViewEvent.UpdateWorkoutData(exercise.workoutId))}
                }

                exerciseBinding.addApproach.setOnClickListener {
                    callback.invoke(ViewEvent.AddApproachToExercise(exercise))

                    approachAdapter.notifyDataSetChanged()
                }
        }
    }
}