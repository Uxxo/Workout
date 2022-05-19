package com.example.personalworkoutnotebook.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personalworkoutnotebook.databinding.ActivityMainBinding
import com.example.personalworkoutnotebook.ui.ViewEvent
import com.example.personalworkoutnotebook.ui.adapter.WorkoutPresentationAdapter
import com.example.personalworkoutnotebook.ui.viewModel.WorkoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val workoutViewModel : WorkoutViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val workoutAdapter = WorkoutPresentationAdapter{ event ->
            when(event){
                is ViewEvent.DeleteWorkout -> lifecycleScope.launch { workoutViewModel.deleteWorkout(event.workout) }
                is ViewEvent.DuplicateWorkout -> lifecycleScope.launch { workoutViewModel.duplicateWorkout(event.workout) }
                is ViewEvent.CopyWorkoutsFields -> lifecycleScope.launch { workoutViewModel.copyWorkoutToBuffer(event.workout.id,this@MainActivity) }
            }
        }

        binding.workoutRecycler.adapter = workoutAdapter

        workoutViewModel.isLoading.observe(this) { isVisible ->
            binding.progressLayout.visibility = if (isVisible) View.VISIBLE else View.GONE
        }


        workoutViewModel.workouts.observe(this) { workouts ->
            workoutAdapter.setData(workouts)
        }

        binding.startNewWorkout.setOnClickListener {
            lifecycleScope.launch {

                val workout = workoutViewModel.createWorkout()
                val intent = CreateNewWorkoutActivity.getIntent(this@MainActivity,workout.id)
                startActivityForResult(intent, REQUEST_CODE_DELETE_WORKOUT)
            }
        }

        binding.menuButtonsLayout.myWorkouts.drawable.setTint(Color.BLUE)
        binding.menuButtonsLayout.myExercises.setOnClickListener {
            startActivity(Intent(this, ExercisesActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
        }

        binding.menuButtonsLayout.myBio.setOnClickListener {
            startActivity(Intent(this, BioParametersActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_DELETE_WORKOUT){
           val id = intent?.getLongExtra(EXTRA_ID,-1) ?: -1
            if (id == -1L) return
            val adapter = (binding.workoutRecycler.adapter as WorkoutPresentationAdapter)
            lifecycleScope.launch {
                workoutViewModel.loadAllWorkouts()
                adapter.notifyDataSetChanged()
            }

        }
    }

    override fun onStart() {
//
//        GlobalScope.launch {
//            workoutViewModel.createTestDB()
//        }
        super.onStart()
        lifecycleScope.launch {
            workoutViewModel.loadAllWorkouts()
        }
    }

    companion object {
        private const val REQUEST_CODE_DELETE_WORKOUT = 1001
        const val EXTRA_ID = "ID_EXTRA"
    }
}