//package com.example.personalworkoutnotebook.ui.viewModel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.example.personalworkoutnotebook.model.Approach
//import com.example.personalworkoutnotebook.repository.ApproachRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//
//@HiltViewModel
//class ApproachViewModel @Inject constructor(
//    private var approachRepository: ApproachRepository
//) : ViewModel() {
//
//    private var _approaches = MutableLiveData<List<Approach>>()
//    val approaches : LiveData<List<Approach>>
//        get() = _approaches
//
//    private var _approach = MutableLiveData<Approach>()
//    val approach: LiveData<Approach>
//        get() = _approach
//
//    private var _isLoading = MutableLiveData(false)
//    val isLoading: LiveData<Boolean>
//        get() = _isLoading
//
//
//    suspend fun loadData(id : Long){
//        _isLoading.postValue(true)
//        _approaches.postValue(approachRepository.getAllByExerciseId(id).sortedBy { it.id })
//    }
//
//    suspend fun getApproach(id :Long){
//        _isLoading.postValue(true)
//        _approach.postValue(approachRepository.getById(id))
//        _isLoading.postValue(false)
//    }
//
//    suspend fun updateApproach(approach: Approach) : Approach{
//       return approachRepository.save(approach)
//    }
//
//    suspend fun saveApproach(approach: Approach) : Approach{
//       return approachRepository.save(approach)
//    }
//
//    suspend fun deleteApproach(approach: Approach){
//        approachRepository.delete(approach.id)
//    }
//
//    suspend fun createApproach(exerciseId : Long) : Approach{
//        val approach = Approach(id = 0L, exerciseId = exerciseId, mass = 0.0, repeat = 0)
//        val savedApproach = approachRepository.save(approach)
//        return savedApproach
//    }
//
//
//}