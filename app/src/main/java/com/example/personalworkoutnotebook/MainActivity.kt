package com.example.personalworkoutnotebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.personalworkoutnotebook.databinding.ActivityMainBinding
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

        workoutViewModel.loadingIndicator.observe(this) { isVisible ->
            binding.progressLayout.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

        lifecycleScope.launch {
            workoutViewModel.loadData()
        }
    }
}