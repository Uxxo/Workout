package com.example.personalworkoutnotebook.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.databinding.ActivityCreateWorkoutBinding
import com.example.personalworkoutnotebook.extension.*
import com.example.personalworkoutnotebook.model.*
import com.example.personalworkoutnotebook.service.CountDownService
import com.example.personalworkoutnotebook.ui.ViewEvent
import com.example.personalworkoutnotebook.ui.adapter.ExerciseAdapter
import com.example.personalworkoutnotebook.ui.adapter.HolderTypeConstants
import com.example.personalworkoutnotebook.ui.viewModel.WorkoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class CreateNewWorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateWorkoutBinding
    private val workoutViewModel: WorkoutViewModel by viewModels()


    private var isCountDownTimer1 = false
    private var isCountDownTimer2 = false

    private lateinit var workoutName: String

    private val timerValueReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val minutes = intent?.getIntExtra("minutes", -1)
            val seconds = intent?.getIntExtra("seconds", -1)
            val timersNumber = intent?.getIntExtra("timersNumber", -1)
            val isTimerStarted = intent?.getBooleanExtra("isTimerStarted", false)

            if (timersNumber == TIMER_1) {
                binding.timer1.text = toTimeForm(minutes!!, seconds!!)
            }
            if (timersNumber == TIMER_2) {
                binding.timer2.text = toTimeForm(minutes!!, seconds!!)
            }
            if (isTimerStarted != null) {
                if (timersNumber == TIMER_1) {
                    isCountDownTimer1 = isTimerStarted
                }
                if (timersNumber == TIMER_2) {
                    isCountDownTimer2 = isTimerStarted
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocalBroadcastManager.getInstance(this).registerReceiver(
            timerValueReceiver, IntentFilter("timerValue")
        )

        binding = ActivityCreateWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val exerciseAdapter = ExerciseAdapter(HolderTypeConstants.WORKOUT_EXERCISE_HOLDER){ event ->
            when (event) {
                is ViewEvent.SaveSet -> lifecycleScope.launch {
                    workoutViewModel.saveSet(event.set) }
                is ViewEvent.DeleteSet -> lifecycleScope.launch { workoutViewModel.deleteSet(event.set) }
                is ViewEvent.SaveExercise -> lifecycleScope.launch { workoutViewModel.saveExercise(event.exercise) }
                is ViewEvent.DeleteExercise -> lifecycleScope.launch { workoutViewModel.deleteExercise(event.exercise.id) }
                is ViewEvent.AddSetToExercise -> lifecycleScope.launch { workoutViewModel.addSetToExercise(event.exercise.id) }
            }
        }


        workoutViewModel.workout.observe(this) { workout ->

            workoutName = workout.name

            when(workout.status){
                Workout.CREATED->{
                    binding.startAndFinishButton.setImageResource(R.drawable.ic_start)
                    binding.workoutDate.visibility = View.INVISIBLE
                }

                Workout.IN_PROCESS -> {
                    binding.startAndFinishButton.setImageResource(R.drawable.ic_finish)
                    binding.workoutDate.visibility = View.INVISIBLE
                }
            }

            if( workout.name !="" && binding.workoutTitle.editText?.text.toString() != workout.name){
                println()
                binding.workoutTitle.editText?.setText(workout.name)
            }

            binding.workoutTitle.editText?.afterTextChanged {
                val editedWorkout = workout.copy(name = it)
                    lifecycleScope.launch {
                        workoutViewModel.saveWorkout(editedWorkout)
                        workoutName = it
                    }

            }

            binding.workoutCopy.setOnClickListener {
                lifecycleScope.launch {
                    workoutViewModel.copyWorkoutToBuffer(workout.id, this@CreateNewWorkoutActivity)
                }
            }

            binding.deleteWorkout.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Are you shore?")
                    .setMessage("This workout will be permanently deleted")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        lifecycleScope.launch {
                            workoutViewModel.deleteWorkout(workout)
                        }
                        setResult(Activity.RESULT_OK, Intent().apply {
                            putExtra(MainActivity.EXTRA_ID, workout.id)
                        })
                        finish()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }

            binding.exerciseRecycler.adapter = exerciseAdapter
            exerciseAdapter.setExercises(workout.exercises)
            exerciseAdapter.setWorkoutStatus(workout.status)


            binding.startAndFinishButton.setOnClickListener {
                lifecycleScope.launch { workoutViewModel.updateWorkoutValue(workout.id) }
                when(workout.status){
                    Workout.CREATED ->{
                        val actualDate = Calendar.getInstance()
                        val editedWorkout = workout.copy(date = actualDate, status = Workout.IN_PROCESS, name = workoutName)
                        lifecycleScope.launch {
                            workoutViewModel.updateWorkout(editedWorkout)
                        }
                    }

                    Workout.IN_PROCESS ->{
                        val editedWorkout = workout.copy(status = Workout.FINISHED, name = workoutName)
                        lifecycleScope.launch {
                            workoutViewModel.saveWorkout(editedWorkout)
                            workoutViewModel.loadAllWorkouts()
                        }
                        finish()
                    }
                }
            }

            if (workout.exercises.isNotEmpty()) binding.exerciseRecycler.smoothScrollToPosition(
                workout.exercises.size - 1
            )

            if (!workout.timers[TIMER_1].isTimeEmpty()) {
                binding.timer1.text = workout.timers[TIMER_1].toText()
            }
            if (!workout.timers[TIMER_2].isTimeEmpty()) {
                binding.timer2.text = workout.timers[TIMER_2].toText()
            }

            binding.timer1.setOnClickListener {
                val timer = workout.timers[TIMER_1]
                if (timer.isTimeEmpty()) {
                    showTimePickerDialog(workout, TIMER_1)
                } else {

                    if (!isCountDownTimer1 && !isCountDownTimer2) {

                        startService(
                            CountDownService.getIntent(
                                this,
                                timer.minutes,
                                timer.seconds,
                                TIMER_1
                            )
                        )
                    } else {
                        if (isCountDownTimer1) {
                            stopService(
                                CountDownService.getIntent(
                                    this,
                                    timer.minutes,
                                    timer.seconds,
                                    TIMER_1
                                )
                            )
                            binding.timer1.text = timer.toText()
                            isCountDownTimer1 = false
                        }
                    }
                }
            }

            binding.timer1.setOnLongClickListener {
                val timer = workout.timers[TIMER_1]
                if (isCountDownTimer1) {
                    stopService(CountDownService.getIntent(this, -1, -1, TIMER_1))
                    binding.timer1.text = timer.toText()
                    isCountDownTimer1 = false
                }
                showTimePickerDialog(workout, TIMER_1)
                return@setOnLongClickListener true
            }

            binding.timer2.setOnClickListener {
                val timer = workout.timers[TIMER_2]
                if (timer.isTimeEmpty()) {
                    showTimePickerDialog(workout, TIMER_2)
                } else {
                    if (!isCountDownTimer2 && !isCountDownTimer1) {
                        startService(
                            CountDownService.getIntent(
                                this,
                                timer.minutes,
                                timer.seconds,
                                TIMER_2
                            )
                        )
                    } else {
                        if (isCountDownTimer2) {
                            stopService(
                                CountDownService.getIntent(
                                    this,
                                    timer.minutes,
                                    timer.seconds,
                                    TIMER_2
                                )
                            )
                            binding.timer2.text = timer.toText()
                            isCountDownTimer2 = false
                        }
                    }
                }
            }

            binding.timer2.setOnLongClickListener {
                val timer = workout.timers[TIMER_2]
                if (isCountDownTimer2) {
                    stopService(CountDownService.getIntent(this, -1, -1, TIMER_2))
                    binding.timer2.text = timer.toText()
                    isCountDownTimer2 = false
                }
                showTimePickerDialog(workout, TIMER_2)
                return@setOnLongClickListener true
            }


            binding.startNewExercise.setOnClickListener {
                lifecycleScope.launch {
                    workoutViewModel.addNewExerciseToWorkout(workout.id)
                }
            }
        }

        workoutViewModel.uniqueExercises.observe(this) { uniqueExercises ->
            exerciseAdapter.setUniqueExercises(uniqueExercises)
        }

        workoutViewModel.isLoading.observe(this) { isLoading ->
            binding.workoutProgressLayout.isVisible(isLoading)
        }


    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            workoutViewModel.updateWorkoutValue(intent.getLongExtra("EXTRA_WORKOUT_ID", -1))
            workoutViewModel.getUniqueExercises()
        }
    }


    private fun showTimePickerDialog(workout: Workout, timer: Int) {
        val minutes = workout.timers[timer].minutes
        val seconds = workout.timers[timer].seconds

        TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
            { _, hourOfDay, minute ->
                updateTimer(workout, hourOfDay, minute, timer)
            }, minutes, seconds, true
        )
            .show()
    }

    private fun updateTimer(workout: Workout, minutes: Int, seconds: Int, timerNumber: Int) {
        if (timerNumber == TIMER_1) {
            val updatedTimer = workout.timers[TIMER_1].copy(minutes = minutes, seconds = seconds)

            val updatedTimersList = mutableListOf(updatedTimer, workout.timers[TIMER_2])

            val editedWorkout = workout.copy(timers = updatedTimersList)
            binding.timer1.text = editedWorkout.timers[TIMER_1].toText()

            lifecycleScope.launch {
                workoutViewModel.saveTimer(updatedTimer)
            }

        }

        if (timerNumber == TIMER_2) {
            val updatedTimer = workout.timers[TIMER_2].copy(minutes = minutes, seconds = seconds)

            val updatedTimerList = mutableListOf(workout.timers[TIMER_1], updatedTimer)
            val editedWorkout = workout.copy(timers = updatedTimerList)
            binding.timer2.text = editedWorkout.timers[TIMER_2].toText()

            lifecycleScope.launch {
                workoutViewModel.saveTimer(updatedTimer)

            }

        }
    }


    private fun toTimeForm(minutes: Int, seconds: Int): String {
        return if (seconds < 10) "$minutes:0$seconds"
        else "$minutes:$seconds"
    }


    companion object {
        const val TIMER_1 = 0
        const val TIMER_2 = 1
        private const val ID_EXTRA = "EXTRA_WORKOUT_ID"
        fun getIntent(context: Context, id: Long): Intent =
            Intent(context, CreateNewWorkoutActivity::class.java).apply {
                putExtra(ID_EXTRA, id)
            }
    }

}



