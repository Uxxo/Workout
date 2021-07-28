package com.example.personalworkoutnotebook.ui.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.model.Exercise
import com.google.android.material.textfield.TextInputLayout

class ExerciseAdapter(private val exercises:MutableList<Exercise>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder {
        return ExerciseHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ExerciseHolder, position: Int) {
        holder.bind(exercises[position])
    }


    override fun getItemCount(): Int {
        return exercises.size
    }

    inner class ExerciseHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(exercise: Exercise) {

            view.findViewById<TextInputLayout>(R.id.exerciseTitleLayout).apply {
                editText?.setOnFocusChangeListener { _, hasFocus ->
                    if(!hasFocus) {
                        // TODO: 21.07.2021 save changes to db

                    }
                }
            }



//            view.findViewById<Button>(R.id.month_button).apply {
//                text = month.name
//                setOnClickListener {
//                    val intent = MonthActivity.getIntent(view.context, month.id)
//                    view.context.startActivity(intent)
//                }
//            }
        }
    }

}