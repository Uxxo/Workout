package com.example.personalworkoutnotebook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.databinding.ItemSetBinding
import com.example.personalworkoutnotebook.extension.afterTextChanged
import com.example.personalworkoutnotebook.extension.isValidDouble
import com.example.personalworkoutnotebook.extension.toValidInt
import com.example.personalworkoutnotebook.model.Set
import com.example.personalworkoutnotebook.ui.ViewEvent

class SetAdapter(
    private val setList: MutableList<Set>,
    private val callback: (event: ViewEvent) -> Unit,
) :
    RecyclerView.Adapter<SetAdapter.SetHolder>() {

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
            itemBinding.setMassLayout.editText?.setText(
                if (set.mass != 0.0) set.mass.toString()
                else ""
            )

            itemBinding.setMassLayout.editText?.afterTextChanged { text ->

                val index = itemBinding.root.tag as Int
                val editedSet =
                    if (text.isValidDouble() && text !="-") setList[index].copy(mass = text.toDouble())
                    else set.copy(mass = 0.0)

                callback.invoke(ViewEvent.SaveSet(editedSet))
                setList.removeAt(index)
                setList.add(index, editedSet)

            }


            itemBinding.setRepeatsLayout.editText?.setText(
                if (set.repeat != 0) set.repeat.toString()
                else ""
            )
            itemBinding.setRepeatsLayout.editText?.afterTextChanged { text ->

                val index = itemBinding.root.tag as Int
                val editedSet =
                    if (text.isBlank())  set.copy(repeat = 0)
                    else setList[index].copy(repeat = text.toValidInt())

                callback.invoke(ViewEvent.SaveSet(editedSet))
                setList.removeAt(index)
                setList.add(index, editedSet)

            }


        }
    }
}

//private fun String.isValidDouble(): Boolean {
//    if (this.isBlank()) return false
//    val symbols = this.toCharArray()
//    if (symbols[0] == '.') return false
//    var counter = 0
//    symbols.forEach {
//        if (it == '.') {
//            counter++
//        }
//        if (counter > 1) {
//            return false
//        }
//    }
//    return true
//}