package com.example.personalworkoutnotebook.ui.adapter

import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.databinding.ItemSetBinding
import com.example.personalworkoutnotebook.extension.afterTextChanged
import com.example.personalworkoutnotebook.extension.isValidDouble
import com.example.personalworkoutnotebook.extension.toShowIt
import com.example.personalworkoutnotebook.extension.toValidInt
import com.example.personalworkoutnotebook.model.Set
import com.example.personalworkoutnotebook.ui.ViewEvent

class SetAdapter(
    private val callback: (event: ViewEvent) -> Unit,
) :
    RecyclerView.Adapter<SetAdapter.SetHolder>() {

    private var setList = mutableListOf<Set>()

    fun setSetList(newSetList: List<Set>) {
        setList = newSetList as MutableList<Set>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SetHolder(
            ItemSetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: SetHolder, position: Int) =
        holder.bind(setList[position])

    override fun getItemCount(): Int {
        return setList.size
    }

    inner class SetHolder(private val itemBinding: ItemSetBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(set: Set) {
            itemBinding.root.tag = setList.indexOf(set)


            if (set.mass != 0.0 && itemBinding.setMassLayout.editText?.text.toString() != set.mass.toShowIt()) {
                itemBinding.setMassLayout.editText?.setText(set.mass.toShowIt())
            }


            itemBinding.setMassLayout.editText?.afterTextChanged { it ->
                val text = it.replace(',', '.', false)
                println()
                val index = itemBinding.root.tag as Int
                    val editedSet =
                        if (text.isValidDouble() && text != "-") setList[index].copy(mass = text.toDouble())
                        else set.copy(mass = 0.0)

                    callback.invoke(ViewEvent.SaveSet(editedSet))
                    setList.removeAt(index)
                    setList.add(index, editedSet)
            }

            if(set.repeat !=0 && itemBinding.setRepeatsLayout.editText?.toString() != set.repeat.toString()){
                itemBinding.setRepeatsLayout.editText?.setText(set.repeat.toString())
            }


            itemBinding.setRepeatsLayout.editText?.afterTextChanged { text ->
                    val index = itemBinding.root.tag as Int
                    val editedSet =
                        if (text.isBlank()) set.copy(repeat = 0)
                        else setList[index].copy(repeat = text.toValidInt())

                    callback.invoke(ViewEvent.SaveSet(editedSet))
                    setList.removeAt(index)
                    setList.add(index, editedSet)
            }
        }
    }
}

