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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*

class BioParameterAdapter(
    private val bioParametersList: MutableList<BioParameter>,
    private val context: Context,
    private val callback: (event: ViewEvent) -> Unit
) :
    RecyclerView.Adapter<BioParameterAdapter.BioParametersHolder>() {

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

        fun bind(bioParameter: BioParameter) {

            val valueAdapter = BioValueAdapter(bioParameter.values as MutableList<BioParameterValue>,callback)

            itemBinding.root.tag = bioParametersList.indexOf(bioParameter)

            itemBinding.bioParameterTitle.text = bioParameter.name
            if (bioParameter.values.isNotEmpty()) {
                val actualValue = bioParameter.values[bioParameter.values.size - 1]
                itemBinding.bioParameterValue.text = actualValue.value.toString()
                itemBinding.bioParameterDate.text = actualValue.date.toText()
            }

//            itemBinding.addValue.setOnClickListener {
//                val index = itemBinding.root.tag as Int
//                createSetValueDialog(context,bioParameter,index)
//            }

            itemBinding.addValueButton.setOnClickListener {
                val index = itemBinding.root.tag as Int
                createSetValueDialog(context,bioParameter,index)
            }

            itemBinding.root.setOnClickListener {
                if (bioParameter.values.size > 1) {

                    val chart = itemBinding.bioChart
                    var listEntry = mutableListOf<Entry>()

                    bioParameter.values.forEach { value ->
                        val x = value.date.toFloat()
                        val y = value.value.toFloat()
                        val entry = Entry(x,y)
                        listEntry.add(entry)
                    }

                    val dataSet = LineDataSet(listEntry,"")
                    dataSet.setDrawValues(true)
                    val lineData = LineData(dataSet)
                    chart.data = lineData
                    chart.setGridBackgroundColor(R.color.white)
                    chart.invalidate()

                    if (itemBinding.bioChart.visibility == View.GONE) itemBinding.bioChart.visibility = View.VISIBLE
                    else itemBinding.bioChart.visibility = View.GONE
                }
                if (itemBinding.valuesRecycler.visibility == View.GONE){

                    itemBinding.valuesRecycler.adapter = valueAdapter

                    itemBinding.valuesRecycler.visibility = View.VISIBLE
                    itemBinding.deleteBioParameter.visibility = View.VISIBLE
                } else {
                    itemBinding.valuesRecycler.visibility = View.GONE
                    itemBinding.deleteBioParameter.visibility = View.GONE
                }
            }

            itemBinding.deleteBioParameter.setOnClickListener {
                with(itemView.context){
                    AlertDialog.Builder(this)
                        .setTitle(R.string.are_you_shore)
                        .setMessage(R.string.this_bio_parameter_will_be_permanently_deleted)
                        .setPositiveButton(android.R.string.ok){_, _ ->
                            val index = itemBinding.root.tag as Int
                            callback.invoke(ViewEvent.DeleteBioParameter(bioParameter))
                            bioParametersList.removeAt(index)
                            notifyItemRemoved(index)
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }
            }
        }
    }

    private fun createSetValueDialog(context: Context, bioParameter: BioParameter, index: Int){
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(R.layout.item_set_new_bio_value_dialog,null)
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setView(dialogView)
        val edittext = dialogView.findViewById<EditText>(R.id.input_text)

        dialogBuilder.setPositiveButton(android.R.string.ok){_, _ ->
            val newValuesText = edittext.text.toString()
            if(newValuesText.isValidDouble()){
                val newBioValue = addNewValue(newValuesText, bioParameter)
                updateBioParameterInAdapter(bioParameter, newBioValue, index)
            } else{
                Toast.makeText(context, R.string.not_valid_value, Toast.LENGTH_SHORT).show()
            }
        }
        dialogBuilder.setNegativeButton(android.R.string.cancel,null)
        dialogBuilder.show()
    }

    private fun addNewValue(value: String, bioParameter: BioParameter): BioParameterValue
    {
        val newValue = BioParameterValue(0L, bioParameter.id, Calendar.getInstance(),value.toDouble())
        callback.invoke(ViewEvent.SaveBioParameterValue(newValue))
        return newValue
    }

    private fun updateBioParameterInAdapter(bioParameter: BioParameter, value: BioParameterValue, index: Int){
        val values = bioParameter.values
        val editedValues:MutableList<BioParameterValue> = values.toMutableList()
        editedValues.add(value)
        val updatedBioParameter = bioParameter.copy(values = editedValues)

        bioParametersList.removeAt(index)
        bioParametersList.add(index, updatedBioParameter)
        notifyItemChanged(index)
    }
}