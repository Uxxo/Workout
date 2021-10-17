package com.example.personalworkoutnotebook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.databinding.ItemApproachBinding
import com.example.personalworkoutnotebook.extension.afterTextChanged
import com.example.personalworkoutnotebook.model.Approach
import com.example.personalworkoutnotebook.ui.ViewEvent

class ApproachAdapter(
    private val approachList: MutableList<Approach>,
    private val callback: (event: ViewEvent) -> Unit,
) :
    RecyclerView.Adapter<ApproachAdapter.ApproachHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ApproachHolder(
            ItemApproachBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: ApproachHolder, position: Int) =
        holder.bind(approachList[position])

    override fun getItemCount(): Int {
        return approachList.size
    }

    inner class ApproachHolder(private val itemBinding: ItemApproachBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(approach: Approach) {
            itemBinding.approachMassLayout.editText?.setText(
                if (approach.mass != 0.0) approach.mass.toString()
                else ""
            )

            itemBinding.approachMassLayout.editText?.afterTextChanged { text ->
                val index = approachList.indexOf(approachList.first { it.id == approach.id })

                val editedApproach =
                    if (text.isValidDouble()) approach.copy(mass = text.toDouble()) else approach.copy(
                        mass = 0.0
                    )

                approachList.removeAt(index)
                approachList.add(index, editedApproach)
                callback.invoke(ViewEvent.SaveApproach(editedApproach))
            }


            itemBinding.approachRepeatsLayout.editText?.setText(
                if (approach.repeat != 0) approach.repeat.toString()
                else ""
            )
            itemBinding.approachRepeatsLayout.editText?.afterTextChanged { text ->

                val index = approachList.indexOf(approachList.first { it.id == approach.id })
                val editedApproach =
                    if (text.isBlank()) approach.copy(repeat = 0) else approach.copy(repeat = text.toInt())

                approachList.removeAt(index)
                approachList.add(index, editedApproach)
                callback.invoke(ViewEvent.SaveApproach(editedApproach))
            }


        }
    }
}

private fun String.isValidDouble(): Boolean {
    if (this.isBlank()) return false
    val symbols = this.toCharArray()
    if (symbols[0] == '.') return false
    var counter = 0
    symbols.forEach {
        if (it == '.') {
            counter++
        }
        if (counter > 1) {
            return false
        }
    }
    return true
}