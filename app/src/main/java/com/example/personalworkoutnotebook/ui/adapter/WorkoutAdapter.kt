package com.example.personalworkoutnotebook.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.databinding.ItemWorkoutBinding
import com.example.personalworkoutnotebook.model.Workout
import java.text.DateFormat

class WorkoutAdapter(private val workoutList: MutableList<Workout>) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WorkoutHolder(
            ItemWorkoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: WorkoutHolder, position: Int) =
        holder.bind(workoutList[position])


    override fun getItemCount(): Int {
        return workoutList.size
    }

    inner class WorkoutHolder(private val itemBinding: ItemWorkoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: Workout) {
            itemBinding.workoutName.text = String.format(item.name)
            itemBinding.workoutDate.text = DateFormat.getDateInstance(DateFormat.SHORT).format(item.date)
        }
    }

}
