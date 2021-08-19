package com.example.personalworkoutnotebook.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personalworkoutnotebook.databinding.ActivityMainBinding
import com.example.personalworkoutnotebook.databinding.ActivityWorkoutBinding
import com.example.personalworkoutnotebook.model.Approach
import com.example.personalworkoutnotebook.model.Exercise
import com.example.personalworkoutnotebook.ui.adapter.ExerciseAdapter
import com.example.personalworkoutnotebook.ui.viewModel.ApproachViewModel
import com.example.personalworkoutnotebook.ui.viewModel.ExerciseViewModel
import com.example.personalworkoutnotebook.ui.viewModel.WorkoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutBinding
    private val workoutViewModel: WorkoutViewModel by viewModels()
    private val exerciseViewModel: ExerciseViewModel by viewModels()
    private val approachViewModel: ApproachViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.exerciseRecycler.adapter = ExerciseAdapter(
            mutableListOf(),
            { exercise ->
                lifecycleScope.launch { exerciseViewModel.saveExercise(exercise) }
            },
            { approach ->
                lifecycleScope.launch { approachViewModel.saveApproach(approach) }
            }
        )

        workoutViewModel.workout.observe(this){ workout ->
            val adapter = ExerciseAdapter(
                workout.exercises.toMutableList(),
                { exercise ->
                    lifecycleScope.launch { exerciseViewModel.saveExercise(exercise) }
                },
                { approach ->
                    lifecycleScope.launch { approachViewModel.saveApproach(approach) }
                }
            )
            binding.exerciseRecycler.adapter = adapter
            adapter.notifyDataSetChanged()
        }

    }

    override fun onStart() {
        super.onStart()
        val id = intent.getLongExtra(ID_EXTRA, -1)
        GlobalScope.launch {
            workoutViewModel.loadWorkoutById(id)
        }
    }

    companion object {
        private const val ID_EXTRA ="EXTRA_WORKOUT_ID"
        fun getIntent(context: Context, id: Long): Intent =
            Intent(context, WorkoutActivity::class.java).apply {
                putExtra(ID_EXTRA, id)
            }
    }
}