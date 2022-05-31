package com.example.personalworkoutnotebook.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.databinding.ActivityBioParameterInfoBinding
import com.example.personalworkoutnotebook.extension.toText
import com.example.personalworkoutnotebook.ui.ViewEvent
import com.example.personalworkoutnotebook.ui.adapter.BioValueAdapter
import com.example.personalworkoutnotebook.ui.viewModel.BioParameterViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BioParameterInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBioParameterInfoBinding
    private val bioViewModel: BioParameterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBioParameterInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val valuesAdapter = BioValueAdapter { event ->
            when (event) {
                is ViewEvent.DeleteBioParameter -> lifecycleScope.launch {
                    bioViewModel.deleteBioParameter(
                        event.bioParameter
                    )
                }
                is ViewEvent.SaveBioParameterValue -> lifecycleScope.launch {
                    bioViewModel.saveBioParametersValue(
                        event.value
                    )
                }
                is ViewEvent.DeleteBioParameterValue -> lifecycleScope.launch {
                    bioViewModel.deleteBioValue(
                        event.value
                    )
                }
            }
        }


        bioViewModel.bioParameter.observe(this) { bioParameter ->
            println()
            binding.bioParameterTitle.text = bioParameter.name

            if (bioParameter.values.size > 1) {

                val chart = binding.bioChart
                val listEntry = mutableListOf<Entry>()
                val dateMap = mutableMapOf<Float, String>()


                bioParameter.values.forEach { value ->
                    val index = (bioParameter.values.indexOf(value) + 1).toFloat()
                    dateMap[index] = value.date.toText()

                    val x = index
                    val y = value.value.toFloat()
                    val entry = Entry(x, y)
                    listEntry.add(entry)

                }

                val dataSet = LineDataSet(listEntry, "")
                dataSet.setDrawValues(true)
                val lineData = LineData(dataSet)
                chart.data = lineData

                val xAxis = chart.xAxis
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val result = dateMap[value]
                        return result ?: ""
                    }

                }

                chart.setGridBackgroundColor(R.color.white)
                chart.animateXY(1000, 1000)
                chart.invalidate()

                binding.bioChart.visibility = View.VISIBLE
            }

            binding.valuesRecycler.adapter = valuesAdapter
            valuesAdapter.setValues(bioParameter.values)

            binding.deleteBioParameter.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle(R.string.are_you_shore)
                    .setMessage(R.string.this_bio_parameter_will_be_permanently_deleted)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        lifecycleScope.launch {
                            bioViewModel.deleteBioParameter(bioParameter)
                        }
                        startActivity(
                            Intent(this, BioParametersActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        )
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            bioViewModel.loadBioParameter(intent.getLongExtra("EXTRA_BIO_PARAMETER_ID", -1))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(
            Intent(this, BioParametersActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        )
    }

    companion object{
        private const val ID_BIO_PARAMETER_EXTRA = "EXTRA_BIO_PARAMETER_ID"

        fun getIntent(context: Context, id: Long): Intent =
            Intent(context, BioParameterInfoActivity::class.java).apply {
                putExtra(ID_BIO_PARAMETER_EXTRA, id)
            }
    }

}

