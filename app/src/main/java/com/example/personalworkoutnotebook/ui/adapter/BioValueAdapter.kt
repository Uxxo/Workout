package com.example.personalworkoutnotebook.ui.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.databinding.ItemBioValueBinding
import com.example.personalworkoutnotebook.extension.toText
import com.example.personalworkoutnotebook.model.BioParameterValue
import com.example.personalworkoutnotebook.ui.ViewEvent
import kotlinx.coroutines.NonDisposableHandle.parent

class BioValueAdapter(
    private val callback: (event: ViewEvent) -> Unit
) : RecyclerView.Adapter<BioValueAdapter.BioValueHolder>() {

    private var valueList = mutableListOf<BioParameterValue>()

    fun setValues(incomingValueList: List<BioParameterValue>){
        valueList = incomingValueList.sortedByDescending { it.date } as MutableList<BioParameterValue>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BioValueHolder(
            ItemBioValueBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: BioValueAdapter.BioValueHolder, position: Int) {
        holder.bind(valueList[position])
    }

    override fun getItemCount(): Int {
        return valueList.size
    }

    inner class BioValueHolder(private val itemBinding: ItemBioValueBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(bioValue: BioParameterValue) {

            itemBinding.root.tag = valueList.indexOf(bioValue)
            itemBinding.bioParameterValue.text = bioValue.value.toString()
            itemBinding.bioParameterDate.text = bioValue.date.toText()

            itemBinding.deleteBioParameter.setOnClickListener {
                with(itemView.context){
                    AlertDialog.Builder(this)
                        .setTitle(R.string.are_you_shore)
                        .setMessage(R.string.this_value_will_be_permanently_deleted)
                        .setPositiveButton(android.R.string.ok){_,_ ->
                            callback.invoke(ViewEvent.DeleteBioParameterValue(bioValue))
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }
            }
        }
    }
}