package com.example.personalworkoutnotebook.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personalworkoutnotebook.databinding.ActivityWorkoutBinding
import com.example.personalworkoutnotebook.extension.afterTextChanged
import com.example.personalworkoutnotebook.extension.isVisible
import com.example.personalworkoutnotebook.extension.toText
import com.example.personalworkoutnotebook.ui.ViewEvent
import com.example.personalworkoutnotebook.ui.adapter.ExerciseAdapter
import com.example.personalworkoutnotebook.ui.viewModel.WorkoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateNewWorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutBinding
    private val workoutViewModel: WorkoutViewModel by viewModels()
//    private val exerciseViewModel: ExerciseViewModel by viewModels()
//    private val approachViewModel: ApproachViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.workoutDate.requestFocus()

        workoutViewModel.workout.observe(this){ workout ->

            binding.workoutDate.text = workout.date.toText()
            binding.workoutTitle.editText?.setText(workout.name)
            binding.workoutTitle.editText?.afterTextChanged {
                val newWorkout = workout.copy(name = it)
                lifecycleScope.launch {
                    workoutViewModel.updateWorkout(newWorkout)
                }
            }

            val adapter = ExerciseAdapter(
                workout.exercises.toMutableList())
            { event ->
                when(event) {
                    is ViewEvent.SaveApproach -> lifecycleScope.launch { workoutViewModel.saveApproach(event.approach) }
                    is ViewEvent.DeleteApproach -> lifecycleScope.launch { workoutViewModel.deleteApproach(event.approach) }
                    is ViewEvent.SaveExercise -> lifecycleScope.launch { workoutViewModel.saveExercise(event.exercise) }
                    is ViewEvent.DeleteExercise -> lifecycleScope.launch { workoutViewModel.deleteExercise(event.exercise) }
//                    is ViewEvent.UpdateWorkoutData -> lifecycleScope.launch { workoutViewModel.updateViewModel(event.id) }
                    is ViewEvent.AddApproachToExercise -> lifecycleScope.launch { workoutViewModel.addApproachToExercise(event.exercise) }
                }

            }
            binding.exerciseRecycler.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        workoutViewModel.isLoading.observe(this){ isLoading ->
            binding.workoutProgressLayout.isVisible(isLoading)
        }

        binding.exerciseRecycler.adapter = ExerciseAdapter(
            mutableListOf())
        { event ->
            when(event) {
                is ViewEvent.SaveApproach -> lifecycleScope.launch { workoutViewModel.saveApproach(event.approach) }
                is ViewEvent.DeleteApproach -> lifecycleScope.launch { workoutViewModel.deleteApproach(event.approach) }
                is ViewEvent.SaveExercise -> lifecycleScope.launch { workoutViewModel.saveExercise(event.exercise) }
                is ViewEvent.UpdateWorkoutData ->lifecycleScope.launch { workoutViewModel.updateWorkoutData() }
            }
        }

        binding.startNewExercise.setOnClickListener {
            lifecycleScope.launch{
                workoutViewModel.addNewExerciseToWorkout(this@CreateNewWorkoutActivity,workoutViewModel.workout.value!!)

            }
        }
    }

    override fun onStart() {
        super.onStart()
        val id = intent.getLongExtra(ID_EXTRA, -1)
        lifecycleScope.launch {
            workoutViewModel.loadWorkoutById(this@CreateNewWorkoutActivity, id)

        }

    }

    companion object {
        private const val ID_EXTRA ="EXTRA_WORKOUT_ID"
        fun getIntent(context: Context, id: Long): Intent =
            Intent(context, CreateNewWorkoutActivity::class.java).apply {
                putExtra(ID_EXTRA, id)
            }
    }
}