package com.example.personalworkoutnotebook.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.databinding.ActivityExerciseInfoBinding
import com.example.personalworkoutnotebook.extension.toFloat
import com.example.personalworkoutnotebook.extension.toText
import com.example.personalworkoutnotebook.ui.viewModel.WorkoutViewModel
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

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

            val calendarSet = graphData.keys.sorted()

            val listEntry = mutableListOf<BarEntry>()
            val dateMap = mutableMapOf<Float,String>()

            calendarSet.forEach { calendar ->
                val index = (calendarSet.indexOf(calendar) +1).toFloat()
                dateMap[index] = calendar.toText()

                val x = index
                val y = graphData[calendar]!!.toFloat()
                val entry = BarEntry(x, y)
                listEntry.add(entry)
            }

            val chart = binding.chart

            val dataSet = BarDataSet(listEntry, "")
            dataSet.setDrawValues(true)
            val barData = BarData(dataSet)
            chart.data = barData

            val aXis = chart.xAxis
            aXis.valueFormatter = object : ValueFormatter(){
                override fun getFormattedValue(value: Float): String {
                    val result = dateMap[value]
                    return result ?: ""
                }
            }

            chart.setGridBackgroundColor(R.color.white)
            chart.animateXY(500,1000)
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

//    private fun setGraphData(exercisesMap: Map<Calendar, Double>): List<BarEntry>{
//        val calendarSet = exercisesMap.keys.sorted()
//        val xValues = mutableListOf<String>()
//        val barEntry = mutableListOf<BarEntry>()
//
//        calendarSet.forEach { calendar ->
//            xValues.add(calendar.toText())
//            val index = xValues.indexOf(calendar.toText())
//            val value = exercisesMap[calendar]
//            barEntry.add(BarEntry(value!!.toFloat(), index.toFloat()))
//            println()
//        }
//        return barEntry
//    }

    companion object{
        private const val ID_EXERCISE_EXTRA = "EXTRA_EXERCISE_ID"

        fun getIntent(context: Context, id: Long): Intent =
            Intent(context, ExerciseInfoActivity::class.java).apply {
                putExtra(ID_EXERCISE_EXTRA, id)
            }
    }
}