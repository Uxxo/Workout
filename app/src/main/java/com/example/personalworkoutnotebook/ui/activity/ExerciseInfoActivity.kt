package com.example.personalworkoutnotebook.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personalworkoutnotebook.databinding.ActivityExerciseInfoBinding
import com.example.personalworkoutnotebook.extension.toText
import com.example.personalworkoutnotebook.model.ExerciseWithDate
import com.example.personalworkoutnotebook.model.Set
import com.example.personalworkoutnotebook.ui.viewModel.WorkoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExerciseInfoActivity : AppCompatActivity(){

    private lateinit var binding: ActivityExerciseInfoBinding
    private val workoutViewModel : WorkoutViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExerciseInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workoutViewModel.exercise.observe(this){ exercise ->
            lifecycleScope.launch {
                workoutViewModel.getExerciseWithDateByNameAndGroup(exercise.name, exercise.group)
            }

            binding.exerciseTitle.text = exercise.name
        }

        workoutViewModel.exerciseWithDate.observe(this){exercisesList ->
            binding.dateAndSetsValue.text = setsAndDatesToString(exercisesList.sortedByDescending { it.date })
        }

    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            workoutViewModel.loadExerciseById(intent.getLongExtra("EXTRA_EXERCISE_ID", -1))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, ExercisesActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
    }

    private fun setsAndDatesToString(exercises: List<ExerciseWithDate>): String{
        var resultString = ""
        exercises.forEach {exercise ->
            val exerciseAsString = "\n" + exercise.date.toText() + "\n"+ setsAsString(exercise.sets) +"\n"
            resultString +=exerciseAsString
        }
        return resultString
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

    companion object{
        private const val ID_EXERCISE_EXTRA = "EXTRA_EXERCISE_ID"

        fun getIntent(context: Context, id: Long): Intent =
            Intent(context, ExerciseInfoActivity::class.java).apply {
                putExtra(ID_EXERCISE_EXTRA, id)
            }
    }
}