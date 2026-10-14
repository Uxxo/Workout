package com.example.personalworkoutnotebook.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.databinding.ItemExerciseInfoBinding
import com.example.personalworkoutnotebook.extension.toText
import com.example.personalworkoutnotebook.model.ExerciseWithDate
import com.example.personalworkoutnotebook.model.setsToString
import com.example.personalworkoutnotebook.ui.ViewEvent

class ExerciseInfoAdapter(
    private val callback: (event: ViewEvent) -> Unit
): RecyclerView.Adapter<ExerciseInfoAdapter.ExerciseInfoHolder>() {

    private var exerciseList = mutableListOf<ExerciseWithDate>()

    fun setExercisesList(incomingExercises: List<ExerciseWithDate>){
        println()
        exerciseList = incomingExercises.sortedByDescending { it.date } as MutableList<ExerciseWithDate>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        ExerciseInfoHolder (
            ItemExerciseInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
                )



    override fun onBindViewHolder(holder: ExerciseInfoHolder, position: Int) {
        holder.bind(exerciseList[position])
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    inner class ExerciseInfoHolder(private val binding: ItemExerciseInfoBinding):
        RecyclerView.ViewHolder(binding.root){

            fun bind(exercise: ExerciseWithDate){
                println()
                binding.date.text = exercise.date.toText()
                binding.setsValue.text = exercise.setsToString()
                 if(exercise.notes != null){
                     binding.notes.text = exercise.notes
                     binding.notes.visibility = View.VISIBLE
                 }

                binding.btnToggleStatistics.isActivated = exercise.isIncludedInStatistics
                binding.btnToggleStatistics.setOnClickListener {
                    val newStatus = !exercise.isIncludedInStatistics
                    binding.btnToggleStatistics.isActivated = newStatus
                    callback.invoke(ViewEvent.ChangeExerciseStatisticStatus(exercise.id))
                }
            }
        }
}