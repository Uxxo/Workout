package com.example.personalworkoutnotebook.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.databinding.ActivityBioParametersBinding
import com.example.personalworkoutnotebook.extension.isValidDouble
import com.example.personalworkoutnotebook.model.BioParameter
import com.example.personalworkoutnotebook.ui.ViewEvent
import com.example.personalworkoutnotebook.ui.adapter.BioParameterAdapter
import com.example.personalworkoutnotebook.ui.viewModel.BioParameterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BioParametersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBioParametersBinding
    private val bioViewModel: BioParameterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBioParametersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = BioParameterAdapter( this){ event ->
            when(event){
                is ViewEvent.DeleteBioParameter ->lifecycleScope.launch { bioViewModel.deleteBioParameter(event.bioParameter) }
                is ViewEvent.SaveBioParameterValue -> lifecycleScope.launch { bioViewModel.saveBioParametersValue(event.value) }
                is ViewEvent.DeleteBioParameterValue ->lifecycleScope.launch { bioViewModel.deleteBioValue(event.value) }
                is ViewEvent.StartBioParameterInfoActivity -> startActivity(event.intent)
            }
        }

        bioViewModel.allBioParameters.observe(this){ bioParameters ->
            println()
            binding.bioRecycler.adapter = adapter
            adapter.setBioParameters(bioParameters)
        }


        binding.addNewBioParameter.setOnClickListener {
            createBioParameterDialog()
        }

        binding.menuButtonsLayout.myBio.drawable.setTint(Color.BLUE)

        binding.menuButtonsLayout.myWorkouts.setOnClickListener {
            finish()
        }

        binding.menuButtonsLayout.myExercises.setOnClickListener {
            startActivity(Intent(this, ExercisesActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            bioViewModel.loadAllBioParameters()
        }
    }

    private fun createBioParameterDialog(){
        val layoutInflater = LayoutInflater.from(this)
        val dialogView = layoutInflater.inflate(R.layout.item_create_new_bio_parameter_dialog, null)
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(dialogView)

        dialogBuilder.setPositiveButton(android.R.string.ok){_,_ ->
            val editTitle:String = dialogView.findViewById<EditText>(R.id.input_bio_parameters_title).text.toString()
            val editValue:String = dialogView.findViewById<EditText>(R.id.input_value).text.toString()

            if(editTitle.isNotEmpty() && editValue.isValidDouble()){
                lifecycleScope.launch {
                    val bioParameterId = bioViewModel.createBioParameter(editTitle)
                    bioViewModel.createNewBioValue(editValue.toDouble(), bioParameterId)
                    bioViewModel.loadAllBioParameters()
                }
            } else{
                Toast.makeText(this, "Bio parameters is not valid", Toast.LENGTH_SHORT).show()
            }
        }
        dialogBuilder.setNegativeButton(android.R.string.cancel, null)
        dialogBuilder.show()

    }
}
