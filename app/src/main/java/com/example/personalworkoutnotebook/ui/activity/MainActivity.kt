package com.example.personalworkoutnotebook.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.personalworkoutnotebook.databinding.ActivityMainBinding
import com.example.personalworkoutnotebook.model.Workout
import com.example.personalworkoutnotebook.ui.ViewEvent
import com.example.personalworkoutnotebook.ui.adapter.WorkoutAdapter
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
        binding.workoutRecycler.adapter = WorkoutAdapter(mutableListOf()){event ->
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

            val adapter = WorkoutAdapter(
              if(workouts.isNotEmpty())  {workouts as MutableList<Workout>}
              else mutableListOf()
            ){ event ->
                when(event){
                    is ViewEvent.DeleteWorkout -> lifecycleScope.launch { workoutViewModel.deleteWorkout(event.workout) }
                }
            }
            binding.workoutRecycler.adapter  = adapter
            //Сообщаем адаптеру, что данные в нем изменились и нужно перерисоваться
            adapter.notifyDataSetChanged()
        }

        binding.startNewWorkout.setOnClickListener {
            startActivity(Intent(this, CreateNewWorkoutActivity::class.java))
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
}