package com.example.personalworkoutnotebook.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.databinding.ItemGroupBinding
import com.example.personalworkoutnotebook.model.Group
import com.example.personalworkoutnotebook.model.Workout
import com.example.personalworkoutnotebook.ui.ViewEvent

class GroupAdapter(
    private val context: Context,
    private val callback: (event: ViewEvent) -> Unit
) : RecyclerView.Adapter<GroupAdapter.GroupHolder>() {

    private var groupList = mutableListOf<Group>()

    private var onBind = true

    fun setGroupList(newGroupList: List<Group>){
        groupList = newGroupList as MutableList<Group>
        notifyDataSetChanged()
    }

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

            val exerciseAdapter = ExerciseAdapter(context,HolderTypeConstants.GROUP_EXERCISE_HOLDER,callback)
            exerciseAdapter.setExercises(group.exerciseList)
            groupBinding.exerciseRecycler.adapter = exerciseAdapter
            groupBinding.exerciseRecycler.visibility = View.GONE

            groupBinding.groupTitle.text = group.name

            groupBinding.workoutCustomCardView.setOnClickListener {
                if(groupBinding.exerciseRecycler.visibility == View.GONE){
                    groupBinding.exerciseRecycler.visibility = View.VISIBLE
                } else if(groupBinding.exerciseRecycler.visibility == View.VISIBLE){
                    groupBinding.exerciseRecycler.visibility = View.GONE
                }
            }
        }
    }
}
