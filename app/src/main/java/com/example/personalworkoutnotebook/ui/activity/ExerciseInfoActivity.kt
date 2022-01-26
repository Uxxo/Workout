package com.example.personalworkoutnotebook.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.databinding.ActivityExerciseInfoBinding
import com.example.personalworkoutnotebook.ui.viewModel.WorkoutViewModel
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExerciseInfoActivity : AppCompatActivity(){

    private lateinit var binding: ActivityExerciseInfoBinding
    private val workoutViewModel : WorkoutViewModel by viewModels()
    private var graphData = mutableListOf<Double>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExerciseInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workoutViewModel.exercise.observe(this){ exercise ->
            binding.exerciseTitle.text = exercise.name
        }
        workoutViewModel.exerciseGraphData.observe(this){graphData ->
            val chart = binding.chart
            var arrayEntry = mutableListOf<BarEntry>()

            graphData.forEach {
                val x = graphData.indexOf(it).toFloat()
                val y = it.toFloat()
                val entry = BarEntry(x,y)
                arrayEntry.add(entry)
            }

            val dataSet = BarDataSet(arrayEntry, "")
            dataSet.setDrawValues(true)
            val barData = BarData(dataSet)
            chart.data = barData
            chart.setGridBackgroundColor(R.color.white)
            chart.invalidate()


        }

    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            workoutViewModel.loadExerciseById(intent.getLongExtra("EXTRA_EXERCISE_ID", -1))
            workoutViewModel.loadExercisesDataForGraphic(intent.getLongExtra("EXTRA_EXERCISE_ID", -1))
        }
    }


    companion object{
        private const val ID_EXERCISE_EXTRA = "EXTRA_EXERCISE_ID"

        fun getIntent(context: Context, id: Long): Intent =
            Intent(context, ExerciseInfoActivity::class.java).apply {
                putExtra(ID_EXERCISE_EXTRA, id)
            }
    }
}