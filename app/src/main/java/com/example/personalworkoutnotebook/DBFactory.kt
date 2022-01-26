//package com.example.personalworkoutnotebook
//
//import com.example.personalworkoutnotebook.model.Approach
//import com.example.personalworkoutnotebook.model.Exercise
//import com.example.personalworkoutnotebook.model.Workout
//import com.example.personalworkoutnotebook.repository.ApproachRepository
//import com.example.personalworkoutnotebook.repository.ExerciseRepository
//import com.example.personalworkoutnotebook.repository.WorkoutRepository
//import java.util.*
//import javax.inject.Inject
//
//class DBFactory @Inject constructor(
//    val workoutRepository: WorkoutRepository,
//    val exerciseRepository: ExerciseRepository,
//    val approachRepository: ApproachRepository
//) {
//
//
//
//    fun createWorkout(): Workout{
//        val year = 2020
//        val month = (1..12).random()
//        val day = (1..31).random()
//        val date: Calendar = GregorianCalendar(year,month,day)
//        val nameList = Arrays.asList("Руки","Плечи","Ноги","Грудь","Спина")
//        val name = nameList[(0..4).random()]
//        val workout: Workout = Workout(0,date,name)
//        return workout
//    }
//
//    fun createExercise(workoutId:Long):Exercise{
//        val nameList = Arrays.asList("Жим","Присед","Тяга","Сгибание","Разгибание")
//        val name = nameList[(0..4).random()]
//        val exercise:Exercise = Exercise(0,workoutId,name,null,null)
//        return exercise
//    }
//
//    fun createApproach(exerciseId:Long): Approach{
//        val mass:Double = (15..50).random().toDouble()
//        val repeat = (8..15).random()
//        val approach: Approach = Approach(0,exerciseId,mass,repeat)
//        return approach
//    }
//
//    suspend fun testCreateDB(){
//
//        for(a in 0..20){
//            val newWorkout = createWorkout()
//            val savedWorkout = workoutRepository.save(newWorkout)
//            val exerciseList :MutableList<Exercise> = mutableListOf()
//
//            for (b in 0..5){
//                val savedExercise = exerciseRepository.save(createExercise(savedWorkout.id))
//                val approachList :MutableList<Approach> = mutableListOf()
//
//                for(c in 0..4) {
//                    val sawedApproach = approachRepository.save(createApproach(savedExercise.id))
//                    approachList.add(sawedApproach)
//                }
//                val updatedExercise = savedExercise.copy(approaches = approachList)
//                exerciseRepository.save(updatedExercise)
//                exerciseList.add(updatedExercise)
//            }
//
//            val updatedWorkout = Workout(savedWorkout.id,savedWorkout.date,savedWorkout.name,exerciseList)
//            workoutRepository.save(updatedWorkout)
//        }
//    }
//}