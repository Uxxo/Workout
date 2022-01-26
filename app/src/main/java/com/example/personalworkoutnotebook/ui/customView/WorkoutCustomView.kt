package com.example.personalworkoutnotebook.ui.customView

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.model.Exercise
import com.example.personalworkoutnotebook.ui.adapter.ExercisePresentationAdapter

class WorkoutCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) :  CardView(context, attrs, defStyleAttr) {

    private val imageField : ImageView
    private val workoutTitleField : TextView
    private val workoutDateField : TextView
    private val workoutDeleteButtonField : Button
    private val workoutCopyButtonField : Button
    private val exerciseRecyclerField = findViewById<RecyclerView>(R.id.exerciseRecycler)


        init {
            LayoutInflater.from(context).inflate(R.layout.workout_custom_view, this)
            imageField = findViewById(R.id.picture)
            workoutTitleField = findViewById(R.id.workoutTitle)
            workoutDateField = findViewById(R.id.workoutDate)
            workoutDeleteButtonField = findViewById(R.id.deleteWorkout)
            workoutCopyButtonField = findViewById(R.id.workoutCopy)

            with(context.obtainStyledAttributes(attrs, R.styleable.WorkoutCustomView, defStyleAttr, 0)){
                getInt(R.styleable.WorkoutCustomView_image, 0).also { if (it != 0) image = AppCompatResources.getDrawable(context,it)
                }
                getString(R.styleable.WorkoutCustomView_workoutTitle)?.let { workoutTitle = it }
                getString(R.styleable.WorkoutCustomView_workoutDate)?.let { workoutDate = it }

            }
        }

    var image : Drawable? = null
        set(value) {
           field = value
            imageField.setImageDrawable(value)
        }

    var workoutTitle : String? = null
        set(value) {
            field = value
            workoutTitleField.text = value
        }

    var workoutDate : String? = null
        set(value) {
            field = value
            workoutDateField.text = value
        }

    var exerciseItems : List<Exercise> = emptyList()
        set(value) {
            field = value
            exerciseRecyclerField.adapter = ExercisePresentationAdapter(value as MutableList<Exercise>)
        }

}