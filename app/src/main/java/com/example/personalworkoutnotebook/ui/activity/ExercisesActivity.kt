package com.example.personalworkoutnotebook.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personalworkoutnotebook.databinding.ActivityExercisesBinding
import com.example.personalworkoutnotebook.model.Group
import com.example.personalworkoutnotebook.ui.ViewEvent
import com.example.personalworkoutnotebook.ui.adapter.GroupAdapter
import com.example.personalworkoutnotebook.ui.viewModel.WorkoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExercisesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExercisesBinding
    private val workoutViewModel : WorkoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExercisesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val groupAdapter = GroupAdapter(this)
        { event ->
            when(event){
                is ViewEvent.StartExerciseInfoActivity -> startActivity(event.intent)
            }
        }

        binding.groupRecycler.adapter = groupAdapter

        workoutViewModel.groups.observe(this){ groups ->
            groupAdapter.setGroupList(groups)
        }

        binding.menuButtonsLayout.myExercises.drawable.setTint(Color.BLUE)
        binding.menuButtonsLayout.myWorkouts.setOnClickListener {
            finish()
        }

        binding.menuButtonsLayout.myBio.setOnClickListener {
            startActivity(Intent(this, BioParametersActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
        }
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            workoutViewModel.loadAllExercisesGroup(this@ExercisesActivity)
        }

    }


}