package com.example.personalworkoutnotebook.ui.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.databinding.ItemBioParameterBinding
import com.example.personalworkoutnotebook.extension.isValidDouble
import com.example.personalworkoutnotebook.extension.toFloat
import com.example.personalworkoutnotebook.extension.toText
import com.example.personalworkoutnotebook.model.BioParameter
import com.example.personalworkoutnotebook.model.BioParameterValue
import com.example.personalworkoutnotebook.ui.ViewEvent
import com.example.personalworkoutnotebook.ui.activity.BioParameterInfoActivity
import com.example.personalworkoutnotebook.ui.activity.BioParametersActivity
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.*
import kotlin.collections.ArrayList

class BioParameterAdapter(
    private val context: Context,
    private val callback: (event: ViewEvent) -> Unit
) :
    RecyclerView.Adapter<BioParameterAdapter.BioParametersHolder>() {

    private var bioParametersList = mutableListOf<BioParameter>()

    fun setBioParameters(incomingParametersList: List<BioParameter>) {
        bioParametersList = incomingParametersList as MutableList<BioParameter>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BioParametersHolder(
            ItemBioParameterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: BioParameterAdapter.BioParametersHolder, position: Int) {
        holder.bind(bioParametersList[position])
    }

    override fun getItemCount(): Int {
        return bioParametersList.size
    }

    inner class BioParametersHolder(private val itemBinding: ItemBioParameterBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private val valueAdapter = BioValueAdapter(callback)

        fun bind(bioParameter: BioParameter) {

            itemBinding.root.tag = bioParametersList.indexOf(bioParameter)

            itemBinding.bioParameterTitle.text = bioParameter.name

            if (bioParameter.values.isNotEmpty()) {
                val actualValue = bioParameter.values[bioParameter.values.size - 1]
                itemBinding.bioParameterValue.text = actualValue.value.toString()
                itemBinding.bioParameterDate.text = actualValue.date.toText()
            }

            itemBinding.addValueButton.setOnClickListener {
                val index = itemBinding.root.tag as Int
                createSetValueDialog(context, bioParameter, index)
            }

            itemBinding.root.setOnClickListener {

                val intent = BioParameterInfoActivity.getIntent(context, bioParameter.id)
                callback.invoke(ViewEvent.StartBioParameterInfoActivity(intent))

            }
        }

        private fun createSetValueDialog(context: Context, bioParameter: BioParameter, index: Int) {
            val layoutInflater = LayoutInflater.from(context)
            val dialogView = layoutInflater.inflate(R.layout.item_set_new_bio_value_dialog, null)
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setView(dialogView)
            val edittext = dialogView.findViewById<EditText>(R.id.input_text)

            dialogBuilder.setPositiveButton(android.R.string.ok) { _, _ ->
                val newValuesText = edittext.text.toString()
                if (newValuesText.isValidDouble()) {
                    addNewValue(newValuesText, bioParameter)
                } else {
                    Toast.makeText(context, R.string.not_valid_value, Toast.LENGTH_SHORT).show()
                }
            }
            dialogBuilder.setNegativeButton(android.R.string.cancel, null)
            dialogBuilder.show()
        }

        private fun addNewValue(value: String, bioParameter: BioParameter): BioParameterValue {
            val newValue =
                BioParameterValue(0L, bioParameter.id, Calendar.getInstance(), value.toDouble())
            callback.invoke(ViewEvent.SaveBioParameterValue(newValue))
            return newValue
        }
    }
}