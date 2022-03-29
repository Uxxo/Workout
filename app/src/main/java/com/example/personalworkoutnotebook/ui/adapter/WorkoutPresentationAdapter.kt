package com.example.personalworkoutnotebook.ui.adapter

import android.app.AlertDialog
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.databinding.WorkoutCustomViewBinding
import com.example.personalworkoutnotebook.extension.toText
import com.example.personalworkoutnotebook.model.Exercise
import com.example.personalworkoutnotebook.model.Workout
import com.example.personalworkoutnotebook.ui.ViewEvent
import com.example.personalworkoutnotebook.ui.activity.CreateNewWorkoutActivity

class WorkoutPresentationAdapter(

    private val callback: (event: ViewEvent) -> Unit
) :
    RecyclerView.Adapter<WorkoutPresentationAdapter.WorkoutHolder>() {

    private var workoutList = mutableListOf<Workout>()

    fun setData(incomingWorkoutList: List<Workout>){
        workoutList = incomingWorkoutList as MutableList<Workout>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WorkoutHolder(
            WorkoutCustomViewBinding.inflate(
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

    inner class WorkoutHolder(private val itemBinding: WorkoutCustomViewBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private lateinit var exerciseRecycler: RecyclerView

        fun bind(workout: Workout) {

            val index = workoutList.indexOf(workout)
            itemBinding.root.tag = index


            if(workout.status == Workout.FINISHED){
                itemBinding.picture.setImageResource(R.drawable.icon_done)
            }
            if(workout.status == Workout.IN_PROCESS){
                itemBinding.picture.setImageResource(R.drawable.in_process)
            }
            if(workout.status == Workout.CREATED){
                itemBinding.picture.setImageResource(R.drawable.icon_plained)
            }


            itemBinding.title.text = workout.name
            itemBinding.date.text = workout.date.toText()
            itemBinding.exercisePresentationRecycler.visibility = View.GONE
            itemBinding.deleteWorkout.visibility = View.GONE
            itemBinding.workoutCopy.visibility = View.GONE
            itemBinding.duplicateWorkout.visibility = View.GONE


            val exerciseAdapter =
                ExercisePresentationAdapter(workout.exercises as MutableList<Exercise>)
            exerciseRecycler = itemBinding.exercisePresentationRecycler
            exerciseRecycler.adapter = exerciseAdapter

            itemBinding.workoutCustomCardView.setOnClickListener {
                if(workout.status != Workout.FINISHED){
                    val intent = CreateNewWorkoutActivity.getIntent(itemView.context, workout.id)
                    itemView.context.startActivity(intent)
                } else {
                    if (itemBinding.exercisePresentationRecycler.visibility == View.GONE){
                        itemBinding.exercisePresentationRecycler.visibility = View.VISIBLE
                        itemBinding.deleteWorkout.visibility = View.VISIBLE
                        itemBinding.workoutCopy.visibility = View.VISIBLE
                        itemBinding.duplicateWorkout.visibility = View.VISIBLE
                    } else if(itemBinding.exercisePresentationRecycler.visibility == View.VISIBLE){
                        itemBinding.exercisePresentationRecycler.visibility = View.GONE
                        itemBinding.deleteWorkout.visibility = View.GONE
                        itemBinding.workoutCopy.visibility = View.GONE
                        itemBinding.duplicateWorkout.visibility = View.GONE
                    }
                }
            }

            itemBinding.duplicateWorkout.setOnClickListener {
                with(itemView.context){
                    AlertDialog.Builder(this)
                        .setTitle("Duplicate workout")
                        .setMessage("Do you want to repeat this workout?")
                        .setPositiveButton(android.R.string.ok){_,_ ->
                            callback.invoke(ViewEvent.DuplicateWorkout(workout))
                            Toast.makeText(this, "Your workout created", Toast.LENGTH_SHORT).show()
                            notifyDataSetChanged()
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }
            }

            itemBinding.deleteWorkout.setOnClickListener {
                with(itemView.context){
                    AlertDialog.Builder(this)
                        .setTitle("Are you shore?")
                        .setMessage("This workout will be permanently deleted")
                        .setPositiveButton(android.R.string.ok){_,_ ->
                         callback.invoke(ViewEvent.DeleteWorkout(workout))
                            notifyDataSetChanged()
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }
            }

            itemBinding.workoutCopy.setOnClickListener{
                 callback.invoke(ViewEvent.CopyWorkoutsFields(workout))
            }

        }
    }

//    private fun createVisibilityList(): MutableList<Boolean>{
//        val visibilityList = mutableListOf<Boolean>()
//        for ( i in 0 until workoutList.size){
//            visibilityList.add(false)
//        }
//        return visibilityList
//    }

//    private fun changeVisibility(index : Int){
//        val visibility = visibilityList[index]
//        visibilityList[index] = !visibility
//    }

}
