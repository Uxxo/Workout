package com.example.personalworkoutnotebook.ui.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.databinding.ItemExerciseBinding
import com.example.personalworkoutnotebook.databinding.ItemExercisePresentationBinding
import com.example.personalworkoutnotebook.extension.afterTextChanged
import com.example.personalworkoutnotebook.model.Set
import com.example.personalworkoutnotebook.model.Exercise
import com.example.personalworkoutnotebook.model.Workout
import com.example.personalworkoutnotebook.model.getMaxMass
import com.example.personalworkoutnotebook.ui.ViewEvent
import com.example.personalworkoutnotebook.ui.activity.ExerciseInfoActivity
import com.example.personalworkoutnotebook.ui.adapter.HolderTypeConstants.GROUP_EXERCISE_HOLDER
import com.example.personalworkoutnotebook.ui.adapter.HolderTypeConstants.WORKOUT_EXERCISE_HOLDER

class ExerciseAdapter(
    private val context: Context,
    private val holderType: Int,
    private val workoutStatus: Int,
    private var exerciseList: MutableList<Exercise>,
    private var uniqueExercisesList: List<Exercise>,
    private val callback: (event: ViewEvent) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var exercisesMap = mutableMapOf<String,Exercise>()
    private var onBind = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when(viewType){
            WORKOUT_EXERCISE_HOLDER -> ExerciseHolder(ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            GROUP_EXERCISE_HOLDER -> GroupExerciseHolder(ItemExercisePresentationBinding.inflate(LayoutInflater.from(parent.context), parent, false))

            else -> throw IllegalArgumentException("Unsupported Layout")
        }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBind = true
        when(holder){
            is ExerciseHolder -> holder.bind(exerciseList[position])
            is GroupExerciseHolder -> holder.bind(exerciseList[position])
        }
        onBind = false
    }


    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (holderType){
            1 -> WORKOUT_EXERCISE_HOLDER
            2 -> GROUP_EXERCISE_HOLDER

            else -> throw IllegalArgumentException("Unsupported Type")
        }
    }


    inner class ExerciseHolder(private val exerciseBinding: ItemExerciseBinding) :
        RecyclerView.ViewHolder(exerciseBinding.root) {

        private lateinit var setRecycler: RecyclerView

        fun bind(exercise: Exercise) {

            val setAdapter = SetAdapter(exercise.sets as MutableList, callback)

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
            val namesList = getExercisesTitleAndGroup(uniqueExercisesList)
            val uniqueExercisesAdapter = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item,namesList)

            autoCompleteTextView.setAdapter(uniqueExercisesAdapter)
            autoCompleteTextView.threshold = 2
            autoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->

                val selectedItem = parent.getItemAtPosition(position).toString()
                val foundExercise = exercisesMap[selectedItem]
                if(foundExercise !=null){
                    val newName = foundExercise.name
                    val newGroup = foundExercise.group
                    val maxMass = foundExercise.getMaxMass()

                    if(!onBind){

                        val editedExercise = updateExercise(exercise, newName, newGroup, maxMass)
                        callback.invoke(ViewEvent.SaveSet(editedExercise.sets[0]))
                        callback.invoke(ViewEvent.SaveExercise(editedExercise))

                        exerciseBinding.exerciseNameEditText.setText(editedExercise.name)
                        exerciseBinding.exerciseGroupEditText.setText(editedExercise.group)

                        if (exerciseBinding.setRecycler.visibility == View.VISIBLE){
                            val newSetAdapter = SetAdapter(editedExercise.sets.toMutableList(), callback)
                            setRecycler.adapter = newSetAdapter
                        }
                        exerciseBinding.exerciseGroup.visibility = View.VISIBLE
                    }
                }
            }

            autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus ) exerciseBinding.exerciseGroup.visibility = View.GONE
                else exerciseBinding.exerciseGroup.visibility = View.VISIBLE
            }

            exerciseBinding.exerciseName.editText?.setText(exercise.name)
            exerciseBinding.exerciseName.editText?.afterTextChanged { text ->
                if (!onBind) {
                    if(text.length > 10) exerciseBinding.exerciseGroup.visibility = View.VISIBLE
                   val index = exerciseBinding.root.tag as Int
                    val editedExercise = exerciseList[index].copy(name = text)
                    callback.invoke(ViewEvent.SaveExercise(editedExercise))

                    exerciseList.removeAt(index)
                    exerciseList.add(index, editedExercise)


                }
            }

            exerciseBinding.exerciseName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    if (!onBind) notifyDataSetChanged()
                }
            }

            exerciseBinding.exerciseGroup.editText?.setText(exercise.group)
            exerciseBinding.exerciseGroup.editText?.afterTextChanged { text ->
                if(!onBind){

                    val index = exerciseBinding.root.tag as Int
                    val editedExercise = exerciseList[index].copy(group = text)
                    callback.invoke(ViewEvent.SaveExercise(editedExercise))

                    exerciseList.removeAt(index)
                    exerciseList.add(index, editedExercise)
                }
            }

            exerciseBinding.exerciseGroup.setOnFocusChangeListener { _, hasFocus ->
                if(!hasFocus) notifyDataSetChanged()
            }

            if(workoutStatus == Workout.IN_PROCESS){
                exerciseBinding.setRecycler.adapter = SetAdapter(mutableListOf(), callback)

                setRecycler = exerciseBinding.setRecycler
                setRecycler.adapter = setAdapter

                exerciseBinding.addSet.setOnClickListener {
                    callback.invoke(ViewEvent.AddSetToExercise(exercise))
                }

                exerciseBinding.deleteSet.setOnClickListener {
                if(exercise.sets.size > 1){
                    val set = exercise.sets[exercise.sets.size -1]
                    callback.invoke(ViewEvent.DeleteSet(set))
                    val newSetsList = exercise.sets
                    newSetsList.remove(set)
                    val newAdapter = SetAdapter(newSetsList, callback)
                    setRecycler.adapter = newAdapter
                    newAdapter.notifyDataSetChanged()
                }
                }

            } else {
                exerciseBinding.setRecycler.visibility = View.GONE
                exerciseBinding.addSet.visibility = View.GONE
            }

            if(exercise.notes == null || exercise.notes.isEmpty()){
                exerciseBinding.addNotesButton.visibility = View.VISIBLE
                exerciseBinding.addNotesTextView.visibility = View.VISIBLE
                exerciseBinding.exerciseNote.visibility = View.GONE
            } else {
                exerciseBinding.exerciseNote.visibility = View.VISIBLE
                exerciseBinding.addNotesButton.visibility = View.GONE
                exerciseBinding.addNotesTextView.visibility = View.GONE
            }

            exerciseBinding.addNotesButton.setOnClickListener {
                exerciseBinding.exerciseNote.visibility = View.VISIBLE
                exerciseBinding.addNotesTextView.visibility = View.GONE
                it.visibility = View.GONE
            }

            exerciseBinding.addNotesTextView.setOnClickListener {
                exerciseBinding.exerciseNote.visibility = View.VISIBLE
                exerciseBinding.addNotesButton.visibility = View.GONE
                it.visibility = View.GONE
            }

            exerciseBinding.exerciseNote.editText?.setText(exercise.notes)
            exerciseBinding.exerciseNote.editText?.afterTextChanged { text ->
                if (!onBind) {
                    val index = exerciseBinding.root.tag as Int
                    val editedExercise = exerciseList[index].copy(notes = text)
                    callback.invoke(ViewEvent.SaveExercise(editedExercise))

                    exerciseList.removeAt(index)
                    exerciseList.add(index, editedExercise)
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
    RecyclerView.ViewHolder(exerciseBinding.root){

        fun bind(exercise:Exercise){
            exerciseBinding.exerciseTitle.text = exercise.name
            exerciseBinding.setsInf.text = getExerciseMaxMass(exercise)

            exerciseBinding.exerciseTitle.setOnClickListener{
                val intent = ExerciseInfoActivity.getIntent(context, exercise.id)
                callback.invoke(ViewEvent.StartExerciseInfoActivity(intent))
            }
        }

    }

    private fun getExerciseMaxMass(exercise: Exercise): String{
        if(exercise.sets.isEmpty()) return ""
        var returnedString = ""
        var maxMass = exercise.sets[0].mass
        var repeat = exercise.sets[0].repeat
        exercise.sets.forEach { set ->
            if(set.mass > maxMass && set.repeat !=0){
                maxMass = set.mass
                repeat = set.repeat
            }
        }

        if (repeat !=0){
            val resultMass =
            if(maxMass - maxMass.toInt() == 0.0)  maxMass.toInt()
            else maxMass
            returnedString = "$resultMass: /$repeat"
        }
        return returnedString
    }

    private fun getExercisesTitleAndGroup(exercises: List<Exercise>): List<String>{
        val resultList = mutableListOf<String>()
        exercises.forEach { exercise ->
            val outputString = "${exercise.name} (${exercise.group})"
            resultList.add(exercises.indexOf(exercise), outputString)
            exercisesMap[outputString] = exercise
        }
        return resultList
    }

    private fun updateExercise(exerciseForUpdate: Exercise, newName: String, newGroup: String?, maxMass: Double): Exercise {
        val setsList = exerciseForUpdate.sets
        val newSetsList = mutableListOf<Set>()

        setsList.forEach { set ->
            if (setsList.indexOf(set) == 0) {
                val editedSet = set.copy(mass = maxMass)
                newSetsList.add(0,editedSet)
            } else {newSetsList.add(set)}
        }

        return exerciseForUpdate.copy(name = newName, group = newGroup, sets = newSetsList)
    }

}

 object HolderTypeConstants {
    const val WORKOUT_EXERCISE_HOLDER = 1
    const val GROUP_EXERCISE_HOLDER = 2
}

