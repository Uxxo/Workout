package com.example.personalworkoutnotebook.repository

import com.example.personalworkoutnotebook.dao.TimerDao
import com.example.personalworkoutnotebook.model.RoomTimer
import com.example.personalworkoutnotebook.model.WorkoutTimer
import com.example.personalworkoutnotebook.model.toModel
import com.example.personalworkoutnotebook.model.toRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WorkoutTimerRepository @Inject constructor(
    private val timerDao: TimerDao
) {

    suspend fun save(workoutTimer: WorkoutTimer): WorkoutTimer {
        return withContext(Dispatchers.IO){
            if(timerDao.getOneById(workoutTimer.id) != null){
                timerDao.update(workoutTimer.toRoom())
                timerDao.getOneById(workoutTimer.id)!!.toModel()
            } else {
                val id = timerDao.insert(workoutTimer.toRoom())
                timerDao.getOneById(id)!!.toModel()
            }
        }
    }

    suspend fun delete(id: Long): Boolean{
        return withContext(Dispatchers.IO){
            val timer: RoomTimer? = timerDao.getOneById(id)
            if(timer != null){
                timerDao.delete(timer)
            }
            timerDao.isExist(id)
        }
    }

    suspend fun delete(workoutTimers : List<WorkoutTimer>){
        workoutTimers.forEach {
            if(timerDao.isExist(it.id)){
                timerDao.delete(it.toRoom())
            }
        }
    }

}