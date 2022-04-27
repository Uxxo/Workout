package com.example.personalworkoutnotebook.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.databinding.ItemExercisePresentationBinding
import com.example.personalworkoutnotebook.model.Set
import com.example.personalworkoutnotebook.model.Exercise

class ExercisePresentationAdapter(
    private var exerciseList: MutableList<Exercise>,

) :
    RecyclerView.Adapter<ExercisePresentationAdapter.ExercisePresentationHolder>() {

    private var onBind = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ExercisePresentationHolder(
            ItemExercisePresentationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: ExercisePresentationHolder, position: Int) {
        onBind = true
        holder.bind(exerciseList[position])
        onBind = false
    }


    override fun getItemCount(): Int {
        return exerciseList.size
    }


    inner class ExercisePresentationHolder(private val exerciseBinding: ItemExercisePresentationBinding) :
        RecyclerView.ViewHolder(exerciseBinding.root) {


        fun bind(exercise: Exercise) {
            exerciseBinding.exerciseTitle.text = exercise.name

            val setFields: String = setsAsString(exercise.sets)
            exerciseBinding.setsInf.text = setFields

            if(exercise.notes != null){
                if (exercise.notes.isNotEmpty()){
                    exerciseBinding.notes.text = exercise.notes
                    exerciseBinding.notes.visibility = View.VISIBLE
                }
            }
        }

    }
}

private fun setsAsString(sets: List<Set>): String{
    var resultString = ""

    sets.forEach {
        val index = sets.indexOf(it)
        var mass = ""
        if (index == 0) mass = wholeOrNot(it.mass) + ":"
        if(index > 0 && it.mass != sets[index-1].mass && it.mass !=0.0) {
            mass = "\n" + wholeOrNot(it.mass) + ":"
        }
        val repeat = if(it.repeat !=0) {" /${it.repeat}"}
        else{""}
        resultString += mass + repeat
    }
    return resultString
}

private fun wholeOrNot(value: Double):String{
    return if(value - value.toInt() ==0.0){value.toInt().toString()}
    else value.toString()
}
