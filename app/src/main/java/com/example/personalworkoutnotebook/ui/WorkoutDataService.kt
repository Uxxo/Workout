package com.example.personalworkoutnotebook.ui

import com.example.personalworkoutnotebook.extension.toText
import com.example.personalworkoutnotebook.model.*
import com.example.personalworkoutnotebook.model.Set

class WorkoutDataService {

    fun getGroupList(exerciseList: List<Exercise?>): List<Group> {


        val groupMap: MutableMap<String, MutableMap<String, Exercise>> = mutableMapOf()

        exerciseList.forEach { exercise ->
            if(exercise?.name != null){
               val incomingExercise = if(exercise.group == null) exercise.copy(group = "Other")
                                    else exercise
                if (groupMap.containsKey(incomingExercise.group?.trim())) {
                    val exerciseMap = groupMap[incomingExercise.group?.trim()]
                    if (exerciseMap != null) {
                        if (exerciseMap.containsKey(incomingExercise.name!!.trim())) {
                            val checkingExercise = exerciseMap[incomingExercise.name.trim()]
                            if (checkingExercise != null) {
                                if (incomingExercise.getMaxMass() > checkingExercise.getMaxMass())
                                    exerciseMap += incomingExercise.name.trim() to incomingExercise
                                groupMap += incomingExercise.group!!.trim() to exerciseMap
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

    fun getUniqueExercisesWithMaxMass(exerciseList: List<Exercise?>): List<Exercise>{
        val uniqueExercises = mutableListOf<Exercise>()
        exerciseList.forEach { exercise ->
            if(exercise !=null){
                if(uniqueExercises.any { it.name == exercise.name && it.group == exercise.group }){
                        val index = uniqueExercises.indexOfFirst { it.name == exercise.name && it.group == exercise.group }
                    if(uniqueExercises[index].getMaxMass() < exercise.getMaxMass()){
                        uniqueExercises[index] = exercise
                    }
                } else {uniqueExercises.add(exercise)}
            }
        }
        return  uniqueExercises
    }

    fun getMaxSetsInfoForGraphic(exerciseList: List<Exercise>): List<Double>{
        val resultList = mutableListOf<Double>()
        exerciseList.forEach { exercise ->
            val maxMass = getMaxSetMass(exercise.sets)
            if (maxMass != NOT_VALID_MASS) resultList.add(maxMass)
        }
        return resultList
    }
    private fun getMaxSetMass(sets: List<Set>):Double{
        var maxMass: Double = NOT_VALID_MASS
        sets.forEach { set ->
            if(set.mass > maxMass && set.repeat > 0) maxMass = set.mass
        }
        return  maxMass

    }

    companion object{
        private const val NOT_VALID_MASS = -1000.0
    }
}