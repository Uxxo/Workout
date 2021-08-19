package com.example.personalworkoutnotebook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.databinding.ItemWorkoutPresentationBinding
import com.example.personalworkoutnotebook.extension.toText
import com.example.personalworkoutnotebook.model.Workout
import com.example.personalworkoutnotebook.ui.activity.WorkoutActivity

class WorkoutAdapter(private val workoutList: List<Workout>) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WorkoutHolder(
            ItemWorkoutPresentationBinding.inflate(
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

    inner class WorkoutHolder(private val itemBinding: ItemWorkoutPresentationBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(workout: Workout) {
            itemBinding.workoutName.text = String.format(workout.name)
            itemBinding.workoutDate.text = workout.date.toText()
            itemBinding.root.setOnClickListener {
                val intent = WorkoutActivity.getIntent(itemView.context, workout.id)
                itemView.context.startActivity(intent)

                // TODO: 29.07.2021 add click listener

            }
        }
    }

}
