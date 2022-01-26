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
import com.example.personalworkoutnotebook.model.Workout
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

        // находим ресайклер и помещаем в него адаптер с пустым списком
        binding.workoutRecycler.adapter = WorkoutPresentationAdapter(mutableListOf()){ event ->
            when(event){
                is ViewEvent.DeleteWorkout -> lifecycleScope.launch { workoutViewModel.deleteWorkout(event.workout) }
            }
        }

        workoutViewModel.isLoading.observe(this) { isVisible ->
            binding.progressLayout.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

            // на liveData-объект workouts вешаем "слушатель", который будет возбуждаться при каждом изменении
            // объекта workouts
        workoutViewModel.workouts.observe(this) { workouts ->
            //помещаем список workouts в новый объект типа WorkoutAdapter и помещаем этот объект
            //в переменную адаптер которая уже назначена ресайклеру

            val adapter = WorkoutPresentationAdapter(
              if(workouts.isNotEmpty())  { workouts.sortedByDescending { it.date } as MutableList<Workout> }
              else mutableListOf()
            ){ event ->
                when(event){
                    is ViewEvent.DeleteWorkout -> lifecycleScope.launch { workoutViewModel.deleteWorkout(event.workout) }
                    is ViewEvent.CopyWorkoutsFields -> lifecycleScope.launch { workoutViewModel.copyWorkoutToBuffer(event.workout.id,this@MainActivity) }
                    is ViewEvent.DuplicateWorkout -> lifecycleScope.launch { workoutViewModel.duplicateWorkout(event.workout)}
                }
            }
            binding.workoutRecycler.adapter  = adapter
            //Сообщаем адаптеру, что данные в нем изменились и нужно перерисоваться
            adapter.notifyDataSetChanged()
        }

        binding.startNewWorkout.setOnClickListener {
            lifecycleScope.launch {

                val workout = workoutViewModel.createWorkout()
                val intent = CreateNewWorkoutActivity.getIntent(this@MainActivity,workout.id)
                startActivityForResult(intent, REQUEST_CODE_DELETE_WORKOUT)
            }
        }

        binding.myWorkouts.drawable.setTint(Color.BLUE)
        binding.myExercises.setOnClickListener {
            startActivity(Intent(this, ExercisesActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
        }

        binding.myBio.setOnClickListener {
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
                workoutViewModel.loadData()
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
            // запускаем процесс загрузки данных
            workoutViewModel.loadData()
        }
    }

    companion object {
        private const val REQUEST_CODE_DELETE_WORKOUT = 1001
        const val EXTRA_ID = "ID_EXTRA"
    }
}