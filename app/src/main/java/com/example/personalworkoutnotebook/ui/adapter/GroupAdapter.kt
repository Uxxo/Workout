package com.example.personalworkoutnotebook.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.databinding.ItemExerciseBinding
import com.example.personalworkoutnotebook.databinding.ItemGroupBinding
import com.example.personalworkoutnotebook.model.Exercise
import com.example.personalworkoutnotebook.model.Group
import com.example.personalworkoutnotebook.model.Workout
import com.example.personalworkoutnotebook.ui.ViewEvent

class GroupAdapter(
    private val context: Context,
    private var groupList: MutableList<Group>,
    private val callback: (event: ViewEvent) -> Unit
) : RecyclerView.Adapter<GroupAdapter.GroupHolder>() {

    private var visibilityList = createVisibilityList(groupList)
    private var onBind = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GroupHolder(
            ItemGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: GroupHolder, position: Int) {
        onBind = true
        holder.bind(groupList[position])
        onBind = false
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    inner class GroupHolder(private val groupBinding: ItemGroupBinding) :
        RecyclerView.ViewHolder(groupBinding.root) {

        fun bind(group: Group) {
            val index = groupList.indexOf(group)
            groupBinding.groupTitle.text = group.name
            if(!visibilityList[index]){
                groupBinding.exerciseRecycler.visibility = View.GONE
            }else{
                val exerciseAdapter = ExerciseAdapter(context,HolderTypeConstants.GROUP_EXERCISE_HOLDER,Workout.DEFAULT ,group.exerciseList.toMutableList(),
                    mutableListOf(), callback)
                groupBinding.exerciseRecycler.adapter = exerciseAdapter
            }

            groupBinding.workoutCustomCardView.setOnClickListener {
                changeVisibility(index)
                notifyItemChanged(adapterPosition)
            }
        }
    }

    private fun createVisibilityList(groupList: MutableList<Group>):MutableList<Boolean>{
        val visibilityList = mutableListOf<Boolean>()
        for (i in 0 until groupList.size){
            visibilityList.add(false)
        }
        return visibilityList
    }

    private fun changeVisibility(index: Int){
        val visibility = visibilityList[index]
        visibilityList[index] = !visibility
    }
}
