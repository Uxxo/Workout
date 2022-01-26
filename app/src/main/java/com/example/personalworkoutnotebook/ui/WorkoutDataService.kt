package com.example.personalworkoutnotebook.ui

import com.example.personalworkoutnotebook.extension.toText
import com.example.personalworkoutnotebook.model.*
import kotlin.math.max

class WorkoutDataService {

    fun getGroupList(exerciseList: List<Exercise?>): List<Group> {


        val groupMap: MutableMap<String, MutableMap<String, Exercise>> = mutableMapOf()

        exerciseList.forEach { exercise ->
            if(exercise != null){
               val incomingExercise = if(exercise.group == null) exercise.copy(group = "Other")
                                    else exercise
                if (groupMap.containsKey(incomingExercise.group?.trim())) {
                    val exerciseMap = groupMap[incomingExercise.group?.trim()]
                    if (exerciseMap != null) {
                        if (exerciseMap.containsKey(incomingExercise.name.trim())) {
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
                        mutableMapOf(incomingExercise.name.trim() to incomingExercise)
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
            val approachesAsString = approachesAsString(it.approaches)
            val notes = if (it.notes != null) {
                "\n${it.notes}"
            } else {
                ""
            }

            resultString += "\n$title" + approachesAsString + notes
        }
        return resultString
    }

    private fun approachesAsString(approaches: List<Approach>): String {
        var resultString = ""

        approaches.forEach {
            val index = approaches.indexOf(it)
            var mass = ""
            if (index == 0) mass = "\n" + wholeOrNot(it.mass) + ":"
            if (index > 0 && it.mass != approaches[index - 1].mass && it.mass != 0.0) {
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

    fun getMaxApproachesInfoForGraphic(exerciseList: List<Exercise>): List<Double>{
        val resultList = mutableListOf<Double>()
        exerciseList.forEach { exercise ->
            val maxMass = getMaxApproachMass(exercise.approaches)
            if (maxMass != NOT_VALID_MASS) resultList.add(maxMass)
        }
        return resultList
    }
    private fun getMaxApproachMass(approaches: List<Approach>):Double{
        var maxMass: Double = NOT_VALID_MASS
        approaches.forEach { approach ->
            if(approach.mass > maxMass && approach.repeat > 0) maxMass = approach.mass
        }
        return  maxMass

    }

    companion object{
        private const val NOT_VALID_MASS = -1000.0
    }
}