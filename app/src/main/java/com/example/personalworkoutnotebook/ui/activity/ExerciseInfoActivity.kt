package com.example.personalworkoutnotebook.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.databinding.ActivityExerciseInfoBinding
import com.example.personalworkoutnotebook.extension.toText
import com.example.personalworkoutnotebook.model.Exercise
import com.example.personalworkoutnotebook.model.ExerciseWithDate
import com.example.personalworkoutnotebook.model.Set
import com.example.personalworkoutnotebook.ui.ViewEvent
import com.example.personalworkoutnotebook.ui.adapter.ExerciseInfoAdapter
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

        val adapter = ExerciseInfoAdapter()
        binding.fieldsRecycler.adapter = adapter

        workoutViewModel.exercise.observe(this){ exercise ->
            lifecycleScope.launch {
                workoutViewModel.getExerciseWithDateByNameAndGroup(exercise.name, exercise.group)
            }

            binding.exerciseTitle.text = exercise.name
            binding.renameExercise.setOnClickListener {
                createExerciseRenamingDialog(this,exercise)
            }
        }

        workoutViewModel.exerciseWithDate.observe(this){exercisesList ->
            adapter.setExercisesList(exercisesList)
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


    private fun createExerciseRenamingDialog(context: Context,exercise: Exercise){
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(R.layout.item_ceate_dialog_for_exercise_rename, null)
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setView(dialogView)

        dialogView.findViewById<EditText>(R.id.input_new_exercise_title).setText(exercise.name)
        dialogView.findViewById<EditText>(R.id.input_new_exercise_group).setText(exercise.group)

        dialogBuilder.setPositiveButton(android.R.string.ok){_,_ ->
            val newTitle = dialogView.findViewById<EditText>(R.id.input_new_exercise_title).text.toString()
            val newGroup = dialogView.findViewById<EditText>(R.id.input_new_exercise_group).text.toString()
           lifecycleScope.launch {
               workoutViewModel.renameExercises(exercise.id,newTitle, newGroup)
           }
        }

        dialogBuilder.setNegativeButton(android.R.string.cancel,null)
        dialogBuilder.show()
    }

    companion object{
        private const val ID_EXERCISE_EXTRA = "EXTRA_EXERCISE_ID"

        fun getIntent(context: Context, id: Long): Intent =
            Intent(context, ExerciseInfoActivity::class.java).apply {
                putExtra(ID_EXERCISE_EXTRA, id)
            }
    }
}