package com.example.personalworkoutnotebook.ui

import android.content.Context
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.extension.toText
import com.example.personalworkoutnotebook.model.*
import com.example.personalworkoutnotebook.model.Set
import java.util.*

class WorkoutDataService {

    fun getGroupList(context: Context, exerciseList: List<Exercise?>): List<Group> {

        val groupMap: MutableMap<String, MutableMap<String, Exercise>> = mutableMapOf()

        exerciseList.forEach { exercise ->
            if (exercise?.name != null) {
                val incomingExercise =
                    if (exercise.group == null) exercise.copy(group = context.getString(R.string.group_other))
                    else exercise
                if (groupMap.containsKey(incomingExercise.group?.trim())) {
                    val exerciseMap = groupMap[incomingExercise.group?.trim()]
                    if (exerciseMap != null) {
                        if (exerciseMap.containsKey(incomingExercise.name!!.trim())) {
                            val checkingExercise = exerciseMap[incomingExercise.name.trim()]
                            if (checkingExercise != null) {
                                if (incomingExercise.getMaxMass() > checkingExercise.getMaxMass()) {
                                    exerciseMap += incomingExercise.name.trim() to incomingExercise
                                    groupMap += incomingExercise.group!!.trim() to exerciseMap
                                }
                                if (incomingExercise.getMaxMass() == checkingExercise.getMaxMass() &&
                                    incomingExercise.getMaxRepeat(incomingExercise.getMaxMass()) > checkingExercise.getMaxRepeat(incomingExercise.getMaxMass())
                                ) {
                                    exerciseMap += incomingExercise.name.trim() to incomingExercise
                                    groupMap += incomingExercise.group!!.trim() to exerciseMap
                                }
                            }
                        } else exerciseMap[incomingExercise.name.trim()] = incomingExercise

                    }
                } else {
                    val exerciseMap: MutableMap<String, Exercise> =
                        mutableMapOf(incomingExercise.name!!.trim() to incomingExercise)
                    groupMap[incomingExercise.group!!.trim()] = exerciseMap
                }
            }
        }

        val groupList: MutableList<Group> = mutableListOf()
        groupMap.forEach { it ->
            val exercisePresentationList = mutableListOf<Exercise>()

            it.value.values.forEach { exercisePresentation ->
                exercisePresentationList.add(exercisePresentation)
            }
            val group = Group(it.key, exercisePresentationList)
            groupList.add(group)
        }


        return groupList
    }

    fun workoutAsString(workout: Workout): String {
        var resultString = ""
        val date = workout.date.toText()
        val title = workout.name
        val exercisesAsString = exerciseListAsString(workout.exercises)
        resultString += date + "\n$title" + exercisesAsString

        return resultString
    }

    private fun exerciseListAsString(exercises: List<Exercise>): String {
        var resultString = ""

        exercises.forEach {
            val title = "${it.name}:"
            val setsAsString = setsAsString(it.sets)
            val notes = if (it.notes != null) {
                "\n${it.notes}"
            } else {
                ""
            }

            resultString += "\n$title" + setsAsString + notes
        }
        return resultString
    }

    private fun setsAsString(sets: List<Set>): String {
        var resultString = ""

        sets.forEach {
            val index = sets.indexOf(it)
            var mass = ""
            if (index == 0) mass = "\n" + wholeOrNot(it.mass) + ":"
            if (index > 0 && it.mass != sets[index - 1].mass && it.mass != 0.0) {
                mass = "\n" + wholeOrNot(it.mass) + ":"
            }
            val repeat = if (it.repeat != 0) {
                " /${it.repeat}"
            } else {
                ""
            }
            resultString += mass + repeat
        }
        return resultString
    }

    private fun wholeOrNot(value: Double): String {
        return if (value - value.toInt() == 0.0) {
            value.toInt().toString()
        } else value.toString()
    }

    fun getUniqueExercisesWithMaxMass(exerciseList: List<Exercise?>): List<Exercise> {
        val uniqueExercises = mutableListOf<Exercise>()
        exerciseList.forEach { exercise ->
            if (exercise != null && exercise.name !=null) {
                if (uniqueExercises.any { it.name == exercise.name && it.group == exercise.group }) {
                    val index =
                        uniqueExercises.indexOfFirst { it.name == exercise.name && it.group == exercise.group }
                    if (uniqueExercises[index].getMaxMass() < exercise.getMaxMass()) {
                        uniqueExercises[index] = exercise
                    }
                } else {
                    uniqueExercises.add(exercise)
                }
            }
        }
        return uniqueExercises
    }

    fun getExercisesInfoForGraphic(exerciseList: List<Exercise>, workoutList: List<Workout>): Map<Calendar, Double> {
        val resultMap = mutableMapOf<Calendar, Double>()
        exerciseList.forEach { exercise ->
            workoutList.forEach { workout ->
                if(workout.exercises.contains(exercise)){
                    resultMap[workout.date] = exercise.getMaxMass()
                }
            }

        }
        return resultMap
    }

    private fun getMaxSetMass(sets: List<Set>): Double {
        var maxMass: Double = NOT_VALID_MASS
        sets.forEach { set ->
            if (set.mass > maxMass && set.repeat > 0) maxMass = set.mass
        }
        return maxMass

    }

    companion object {
        private const val NOT_VALID_MASS = -1000.0
    }
}