package com.example.personalworkoutnotebook.ui.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.databinding.ItemWorkoutPresentationBinding
import com.example.personalworkoutnotebook.extension.toText
import com.example.personalworkoutnotebook.model.Workout
import com.example.personalworkoutnotebook.ui.ViewEvent
import com.example.personalworkoutnotebook.ui.activity.CreateNewWorkoutActivity

class WorkoutAdapter(
    private val workoutList: MutableList<Workout>,
    private val callback: (event : ViewEvent) -> Unit
) :
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
                val intent = CreateNewWorkoutActivity.getIntent(itemView.context, workout.id)
                itemView.context.startActivity(intent)
            }

            itemBinding.deleteWorkout.setOnClickListener {
                with(itemView.context){
                    AlertDialog.Builder(this)
                        .setTitle("Are you shore?")
                        .setMessage("This workout will be permanently deleted")
                        .setPositiveButton(android.R.string.ok){_,_ ->
                         callback.invoke(ViewEvent.DeleteWorkout(workout))
                            notifyItemRemoved(adapterPosition)
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }
            }

        }
    }

}
