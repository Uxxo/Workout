package com.example.personalworkoutnotebook.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.personalworkoutnotebook.model.Approach
import com.example.personalworkoutnotebook.repository.ApproachRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ApproachViewModel @Inject constructor(
    private var approachRepository: ApproachRepository
) : ViewModel() {

    private var _approach = MutableLiveData<Approach>()
    val approach: LiveData<Approach>
        get() = _approach

    private var _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    suspend fun getApproach(id :Long){
        _isLoading.postValue(true)
        _approach.postValue(approachRepository.getById(id))
        _isLoading.postValue(false)
    }

    fun updateApproach(approach: Approach){
        _approach.value = approach
    }

    suspend fun saveApproach(approach: Approach){
        approachRepository.save(approach)
    }

}