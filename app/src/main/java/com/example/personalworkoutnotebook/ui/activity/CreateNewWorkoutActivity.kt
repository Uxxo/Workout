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

    private lateinit var actualWorkout: Workout
    private lateinit var uniqueExercises: List<Exercise>


    private val timerValueReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val minutes = intent?.getIntExtra("minutes", -1)
            val seconds = intent?.getIntExtra("seconds", -1)
            val timersNumber = intent?.getIntExtra("timersNumber", -1)
            val isTimerStarted = intent?.getBooleanExtra("isTimerStarted", false)

            if(timersNumber == TIMER_1){binding.timer1.text = toTimeForm(minutes!!,seconds!!)}
            if(timersNumber == TIMER_2){binding.timer2.text = toTimeForm(minutes!!,seconds!!)}
            if (isTimerStarted != null){
                if (timersNumber == TIMER_1){isCountDownTimer1 = isTimerStarted}
                if (timersNumber == TIMER_2){isCountDownTimer2 = isTimerStarted}
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocalBroadcastManager.getInstance(this).registerReceiver(
            timerValueReceiver, IntentFilter("timerValue")
        )

        binding = ActivityCreateWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.workoutDate.requestFocus()

        workoutViewModel.workout.observe(this){ workout ->

            actualWorkout = workout

            if(actualWorkout.status == Workout.CREATED){
                binding.startAndFinishButton.setImageResource(R.drawable.ic_start)
                binding.workoutDate.visibility = View.INVISIBLE
            }

            if(actualWorkout.status == Workout.IN_PROCESS){
                binding.startAndFinishButton.setImageResource(R.drawable.ic_finish)
                binding.workoutDate.visibility = View.INVISIBLE
            }

            binding.workoutTitle.editText?.setText(actualWorkout.name)
            binding.workoutTitle.editText?.afterTextChanged {
                actualWorkout = actualWorkout.copy(name = it)
                lifecycleScope.launch {
                    workoutViewModel.saveWorkout(actualWorkout)
                }
            }

            binding.workoutCopy.setOnClickListener {
                lifecycleScope.launch {
                    workoutViewModel.copyWorkoutToBuffer(actualWorkout.id, this@CreateNewWorkoutActivity)
                }
            }

            binding.deleteWorkout.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Are you shore?")
                    .setMessage("This workout will be permanently deleted")
                    .setPositiveButton(android.R.string.ok){_,_ ->
                        lifecycleScope.launch {
                            workoutViewModel.deleteWorkout(actualWorkout)
                        }
                        setResult(Activity.RESULT_OK, Intent().apply {
                            putExtra(MainActivity.EXTRA_ID, actualWorkout.id)
                        })
                        finish()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }

            workoutViewModel.uniqueExercises.observe(this){ exercises ->
                uniqueExercises = exercises

                val adapter = getExerciseAdapter()

                binding.exerciseRecycler.adapter = adapter
                adapter.notifyDataSetChanged()

                binding.startAndFinishButton.setOnClickListener {
                    if(actualWorkout.status == Workout.IN_PROCESS){
                        val editedWorkout = actualWorkout.copy(status = Workout.FINISHED)
                        lifecycleScope.launch {
                            workoutViewModel.saveWorkout(editedWorkout)
                        }
                        finish()
                    }
                    if(actualWorkout.status == Workout.CREATED){
                        val actualDate = Calendar.getInstance()
                        val editedWorkout = actualWorkout.copy(date = actualDate, status = Workout.IN_PROCESS)
                        lifecycleScope.launch {
                            workoutViewModel.saveWorkout(editedWorkout)
                            workoutViewModel.loadWorkoutById(editedWorkout.id)
                        }

                        binding.startAndFinishButton.setImageResource(R.drawable.ic_finish)
                        val editedAdapter = getExerciseAdapter()
                        binding.exerciseRecycler.adapter = editedAdapter
                        editedAdapter.notifyDataSetChanged()
                    }
                }

            }

            if(actualWorkout.exercises.isNotEmpty()) binding.exerciseRecycler.smoothScrollToPosition(actualWorkout.exercises.size -1)

            if(!actualWorkout.timers[TIMER_1].isTimeEmpty()){binding.timer1.text = actualWorkout.timers[TIMER_1].toText()}
            if(!actualWorkout.timers[TIMER_2].isTimeEmpty()){binding.timer2.text = actualWorkout.timers[TIMER_2].toText()}

            binding.timer1.setOnClickListener {
                val timer = actualWorkout.timers[TIMER_1]
                if (timer.isTimeEmpty()) {
                    showTimePickerDialog(TIMER_1)
                } else {

                    if (!isCountDownTimer1 && !isCountDownTimer2) {

                        startService(CountDownService.getIntent(this, timer.minutes, timer.seconds, TIMER_1))
                    } else {
                        if (isCountDownTimer1) {
                            stopService(
                                CountDownService.getIntent(this, timer.minutes, timer.seconds, TIMER_1)
                            )
                            binding.timer1.text = timer.toText()
                            isCountDownTimer1 = false
                        }
                    }
                }
            }

                binding.timer1.setOnLongClickListener {
                    val timer = actualWorkout.timers[TIMER_1]
                    if(isCountDownTimer1){
                        stopService(CountDownService.getIntent(this,-1,-1, TIMER_1))
                        binding.timer1.text = timer.toText()
                        isCountDownTimer1 = false
                    }
                    showTimePickerDialog(TIMER_1)
                    return@setOnLongClickListener true
                }

                binding.timer2.setOnClickListener {
                    val timer = actualWorkout.timers[TIMER_2]
                    if (timer.isTimeEmpty()) {
                        showTimePickerDialog(TIMER_2)
                    } else {
                        if(!isCountDownTimer2 && !isCountDownTimer1){
                            startService(CountDownService.getIntent(this, timer.minutes, timer.seconds, TIMER_2))
                        } else{
                            if(isCountDownTimer2){
                                stopService(CountDownService.getIntent(this, timer.minutes, timer.seconds, TIMER_2))
                                binding.timer2.text = timer.toText()
                                isCountDownTimer2 = false
                            }
                        }
                    }
                }


            binding.timer2.setOnLongClickListener {
                val timer = actualWorkout.timers[TIMER_2]
                if(isCountDownTimer2){
                    stopService(CountDownService.getIntent(this,-1, -1, TIMER_2))
                    binding.timer2.text = timer.toText()
                    isCountDownTimer2 = false
                }
                showTimePickerDialog(TIMER_2)
                return@setOnLongClickListener true
            }

        }

        workoutViewModel.isLoading.observe(this){ isLoading ->
            binding.workoutProgressLayout.isVisible(isLoading)
        }


        binding.startNewExercise.setOnClickListener {
            lifecycleScope.launch{
                workoutViewModel.addNewExerciseToWorkout(workoutViewModel.workout.value!!.id)
            }
        }


    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            workoutViewModel.loadWorkoutById(intent.getLongExtra("EXTRA_WORKOUT_ID", -1))
            workoutViewModel.loadUniqueExercises()
        }
    }

    private fun getExerciseAdapter(): ExerciseAdapter{
        return ExerciseAdapter(
            this,HolderTypeConstants.WORKOUT_EXERCISE_HOLDER, actualWorkout.status ,actualWorkout.exercises.toMutableList(),uniqueExercises
        )
        { event ->
            when(event) {
                is ViewEvent.SaveSet -> lifecycleScope.launch { workoutViewModel.saveSet(event.set) }
                is ViewEvent.DeleteSet -> lifecycleScope.launch { workoutViewModel.deleteSet(event.set) }
                is ViewEvent.SaveExercise -> lifecycleScope.launch { workoutViewModel.saveExercise(event.exercise) }
                is ViewEvent.DeleteExercise -> lifecycleScope.launch { workoutViewModel.deleteExercise(event.exercise.id) }
                is ViewEvent.AddSetToExercise -> lifecycleScope.launch { workoutViewModel.addSetToExercise(event.exercise.id) }
            }

        }
    }

    private fun showTimePickerDialog(timer: Int) {
        val minutes = actualWorkout.timers[timer].minutes
        val seconds = actualWorkout.timers[timer].seconds

        TimePickerDialog(this,AlertDialog.THEME_HOLO_LIGHT,
            { _, hourOfDay, minute ->
                    updateTimer(hourOfDay,minute, timer)
            }, minutes, seconds, true)
            .show()

    }

    private fun updateTimer(minutes: Int, seconds: Int, timerNumber: Int){
        if(timerNumber == TIMER_1){
            val updatedTimer = actualWorkout.timers[TIMER_1].copy(minutes = minutes, seconds = seconds)

            val updatedTimersList = mutableListOf(updatedTimer, actualWorkout.timers[TIMER_2])

            actualWorkout = actualWorkout.copy(timers = updatedTimersList)
            binding.timer1.text = actualWorkout.timers[0].toText()

            lifecycleScope.launch {
                workoutViewModel.saveTimer(updatedTimer)
           }

        }

        if(timerNumber == TIMER_2){
            val updatedTimer = actualWorkout.timers[TIMER_2].copy(minutes = minutes, seconds = seconds)

            val updatedTimerList = mutableListOf(actualWorkout.timers[TIMER_1], updatedTimer)
            actualWorkout = actualWorkout.copy(timers = updatedTimerList)
            binding.timer2.text = actualWorkout.timers[TIMER_2].toText()

            lifecycleScope.launch {workoutViewModel.saveTimer(updatedTimer)
                workoutViewModel.saveTimer(updatedTimer)
            }

        }
    }


    private fun toTimeForm(minutes: Int, seconds: Int) : String{
        return if(seconds <10) "$minutes:0$seconds"
        else "$minutes:$seconds"
    }


    companion object {
        const val TIMER_1 = 0
        const val TIMER_2 = 1
        private const val ID_EXTRA ="EXTRA_WORKOUT_ID"
        fun getIntent(context: Context, id: Long): Intent =
            Intent(context, CreateNewWorkoutActivity::class.java).apply {
                putExtra(ID_EXTRA, id)
            }
    }

}



