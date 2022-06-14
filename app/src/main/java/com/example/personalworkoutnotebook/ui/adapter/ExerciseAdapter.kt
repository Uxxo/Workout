package com.example.personalworkoutnotebook.ui.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.databinding.ItemExerciseBinding
import com.example.personalworkoutnotebook.databinding.ItemExercisePresentationBinding
import com.example.personalworkoutnotebook.extension.afterTextChanged
import com.example.personalworkoutnotebook.extension.toShowIt
import com.example.personalworkoutnotebook.model.*
import com.example.personalworkoutnotebook.model.Set
import com.example.personalworkoutnotebook.ui.ViewEvent
import com.example.personalworkoutnotebook.ui.activity.ExerciseInfoActivity
import com.example.personalworkoutnotebook.ui.adapter.HolderTypeConstants.GROUP_EXERCISE_HOLDER
import com.example.personalworkoutnotebook.ui.adapter.HolderTypeConstants.WORKOUT_EXERCISE_HOLDER
import javax.xml.parsers.DocumentBuilder

class ExerciseAdapter(
    private val holderType: Int,
    private val callback: (event: ViewEvent) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var exerciseList = mutableListOf<Exercise>()
    private var uniqueExercisesList = mutableListOf<Exercise>()
    private var workoutStatus = Workout.DEFAULT

    private var exercisesMap = mutableMapOf<String, Exercise>()
    private var onBind = true

    fun setExercises(incomingExercises: List<Exercise>) {
        exerciseList = incomingExercises as MutableList<Exercise>
        println()
        notifyDataSetChanged()
    }

    fun setUniqueExercises(incomingExercises: List<Exercise>) {
        uniqueExercisesList = incomingExercises as MutableList<Exercise>
        println()
        notifyDataSetChanged()
    }

    fun setWorkoutStatus(incomingStatus: Int) {
        workoutStatus = incomingStatus
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            WORKOUT_EXERCISE_HOLDER -> ExerciseHolder(
                ItemExerciseBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            GROUP_EXERCISE_HOLDER -> GroupExerciseHolder(
                ItemExercisePresentationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Unsupported Layout")
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBind = true
        when (holder) {
            is ExerciseHolder -> holder.bind(exerciseList[position])
            is GroupExerciseHolder -> holder.bind(exerciseList[position])
        }
        onBind = false
    }


    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (holderType) {
            1 -> WORKOUT_EXERCISE_HOLDER
            2 -> GROUP_EXERCISE_HOLDER

            else -> throw IllegalArgumentException("Unsupported Type")
        }
    }


    inner class ExerciseHolder(private val exerciseBinding: ItemExerciseBinding) :
        RecyclerView.ViewHolder(exerciseBinding.root) {

        fun bind(exercise: Exercise) {
            val context = exerciseBinding.root.context
            val setAdapter = SetAdapter(callback)

            exerciseBinding.root.tag = exerciseList.indexOf(exercise)
            exerciseBinding.deleteExercise.setOnClickListener {

                with(itemView.context) {
                    AlertDialog.Builder(this)
                        .setTitle(R.string.are_you_shore)
                        .setMessage(R.string.this_exercise_will_be_permanently_deleted)
                        .setPositiveButton(android.R.string.ok) { _, _ ->

                            val index = exerciseBinding.root.tag as Int
                            callback.invoke(ViewEvent.DeleteExercise(exercise))
                            exerciseList.removeAt(index)
                            notifyItemRemoved(index)
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }
            }


            val autoCompleteTextView = exerciseBinding.exerciseNameEditText
            val namesList = getExercisesTitleAndGroup(context ,uniqueExercisesList)
            val uniqueExercisesAdapter =
                ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, namesList)
            autoCompleteTextView.setAdapter(uniqueExercisesAdapter)
            autoCompleteTextView.threshold = 2
            autoCompleteTextView.onItemClickListener =
                AdapterView.OnItemClickListener { parent, _, position, _ ->
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    val foundExercise = exercisesMap[selectedItem]
                    if (foundExercise != null) {
                        val newName = foundExercise.name
                        val newGroup = foundExercise.group
                        val maxMass = foundExercise.getMaxMass()

                        if (!onBind) {

                            val editedExercise =
                                updateExercise(exercise, newName, newGroup, maxMass)
                            callback.invoke(ViewEvent.SaveSet(editedExercise.sets[0]))
                            callback.invoke(ViewEvent.SaveExercise(editedExercise))

                            exerciseBinding.exerciseNameEditText.setText(editedExercise.name)
                            exerciseBinding.exerciseGroupEditText.setText(editedExercise.group)

                            if (exerciseBinding.setRecycler.visibility == View.VISIBLE) {

                                setAdapter.setSetList(editedExercise.sets)
                                exerciseBinding.setRecycler.smoothScrollToPosition(setAdapter.itemCount - 1)
                            }
                            exerciseBinding.exerciseGroup.visibility = View.VISIBLE
                        }
                    }
                }

            autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) exerciseBinding.exerciseGroup.visibility = View.GONE
                else exerciseBinding.exerciseGroup.visibility = View.VISIBLE
            }


            if (exercise.name != null) {
                exerciseBinding.exerciseName.editText?.setText(exercise.name)
            } else {
                exerciseBinding.exerciseName.editText?.setText("")
            }
            exerciseBinding.exerciseName.editText?.afterTextChanged { text ->
                if (exerciseBinding.exerciseName.editText?.text.toString() != exercise.name) {
                    if (!onBind) {
                        if (text.length > 10) exerciseBinding.exerciseGroup.visibility =
                            View.VISIBLE
                        val index = exerciseBinding.root.tag as Int
                        val editedExercise =
                            if (text.isEmpty()) exerciseList[index].copy(name = null)
                            else exerciseList[index].copy(name = text)
                        callback.invoke(ViewEvent.SaveExercise(editedExercise))

                        exerciseList.removeAt(index)
                        exerciseList.add(index, editedExercise)
                    }
                }
            }

            if (exercise.group != null) {
                exerciseBinding.exerciseGroup.editText?.setText(exercise.group)
            } else {
                exerciseBinding.exerciseGroup.editText?.setText("")
            }

            exerciseBinding.exerciseGroup.editText?.afterTextChanged { text ->
                if (exerciseBinding.exerciseGroup.editText?.text.toString() != exercise.group) {
                    if (!onBind) {
                        val index = exerciseBinding.root.tag as Int
                        val editedExercise = if (text != "") exerciseList[index].copy(group = text)
                        else exerciseList[index].copy(group = null)
                        callback.invoke(ViewEvent.SaveExercise(editedExercise))

                        exerciseList.removeAt(index)
                        exerciseList.add(index, editedExercise)
                    }
                }
            }

            if (workoutStatus == Workout.IN_PROCESS) {
                exerciseBinding.setRecycler.adapter = setAdapter
                setAdapter.setSetList(exercise.sets)
                exerciseBinding.setRecycler.smoothScrollToPosition(exercise.sets.size - 1)

                exerciseBinding.addSet.setOnClickListener {
                    callback.invoke(ViewEvent.AddSetToExercise(exercise))
                }

                exerciseBinding.deleteSet.setOnClickListener {
                    if (exercise.sets.size > 1) {
                        val set = exercise.sets[exercise.sets.size - 1]
                        callback.invoke(ViewEvent.DeleteSet(set))
                    }
                }

            } else {
                exerciseBinding.setRecycler.visibility = View.GONE
                exerciseBinding.addSet.visibility = View.GONE
                exerciseBinding.deleteSet.visibility = View.GONE
            }

            if (exercise.notes == null || exercise.notes.isEmpty()) {
                exerciseBinding.addNotesButton.visibility = View.VISIBLE
                exerciseBinding.exerciseNote.visibility = View.GONE
            } else {
                exerciseBinding.exerciseNote.visibility = View.VISIBLE
                exerciseBinding.addNotesButton.visibility = View.GONE
            }

            exerciseBinding.addNotesButton.setOnClickListener {
                exerciseBinding.exerciseNote.visibility = View.VISIBLE
                it.visibility = View.GONE
            }

            if (exercise.notes != exerciseBinding.exerciseNote.editText?.text.toString()) {
                exerciseBinding.exerciseNote.editText?.setText(exercise.notes)
            }
            exerciseBinding.exerciseNote.editText?.afterTextChanged { text ->
                if (text != exercise.notes) {
                    if (!onBind) {
                        val index = exerciseBinding.root.tag as Int
                        val editedExercise = exerciseList[index].copy(notes = text)
                        callback.invoke(ViewEvent.SaveExercise(editedExercise))

                        exerciseList.removeAt(index)
                        exerciseList.add(index, editedExercise)
                    }
                }
            }

            exerciseBinding.exerciseNote.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    if (!onBind) {
                        val index = exerciseBinding.root.tag as Int
                        notifyItemChanged(index)
                    }
                }
            }

        }

    }

    inner class GroupExerciseHolder(private val exerciseBinding: ItemExercisePresentationBinding) :
        RecyclerView.ViewHolder(exerciseBinding.root) {

        fun bind(exercise: Exercise) {
            val context = exerciseBinding.root.context
            exerciseBinding.exerciseTitle.text = exercise.name

            exerciseBinding.setsInf.text = exercise.getActualMassAsString()

            exerciseBinding.exerciseTitle.setOnClickListener {
                val intent = ExerciseInfoActivity.getIntent(context, exercise.id)
                callback.invoke(ViewEvent.StartExerciseInfoActivity(intent))
            }
        }

    }


    private fun getExercisesTitleAndGroup(context: Context, exercises: List<Exercise>): List<String> {
        val resultList = mutableListOf<String>()
        exercises.forEach { exercise ->
            if (exercise.name != null) {
                val outputString =
                    "${exercise.name} (${exercise.group ?: context.getString(R.string.group_other)})"
                resultList.add(exercises.indexOf(exercise), outputString)
                exercisesMap[outputString] = exercise
            }
        }
        return resultList
    }

    private fun updateExercise(
        exerciseForUpdate: Exercise, newName: String?, newGroup: String?,
        maxMass: Double
    ): Exercise {
        val setsList = exerciseForUpdate.sets
        val newSetsList = mutableListOf<Set>()

        setsList.forEach { set ->
            if (setsList.indexOf(set) == 0) {
                val editedSet = set.copy(mass = maxMass)
                newSetsList.add(0, editedSet)
            } else {
                newSetsList.add(set)
            }
        }

        return exerciseForUpdate.copy(
            name = newName,
            group = newGroup,
            sets = newSetsList
        )
    }
}

object HolderTypeConstants {
    const val WORKOUT_EXERCISE_HOLDER = 1
    const val GROUP_EXERCISE_HOLDER = 2
}

