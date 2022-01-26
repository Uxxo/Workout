package com.example.personalworkoutnotebook.ui.viewModel

import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.personalworkoutnotebook.model.BioParameter
import com.example.personalworkoutnotebook.model.BioParameterValue
import com.example.personalworkoutnotebook.repository.BioParameterRepository
import com.example.personalworkoutnotebook.repository.BioParameterValueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BioParameterViewModel @Inject constructor(
    private val bioParameterRepository: BioParameterRepository,
    private val valueRepository: BioParameterValueRepository
): ViewModel() {


    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private var _allBioParameters = MutableLiveData<List<BioParameter>>()
    val allBioParameters: LiveData<List<BioParameter>>
        get() = _allBioParameters


    suspend fun getAllBioParameters(){
        _isLoading.value = true
        val parametersList = bioParameterRepository.getAll()
        _allBioParameters.value = parametersList
        _isLoading.value = false
    }

    suspend fun saveBioParameter(bioParameter: BioParameter){
        bioParameterRepository.save(bioParameter)
    }

    suspend fun saveBioParametersValue(value: BioParameterValue){
        valueRepository.save(value)
    }

    suspend fun createBioParameter(name: String): Long {
        val newBioParameter = BioParameter(0, name, mutableListOf())
        return bioParameterRepository.save(newBioParameter).id
    }

    suspend fun deleteBioParameter(bioParameter: BioParameter){
        bioParameterRepository.delete(bioParameter)
    }

    suspend fun createNewBioValue(value: Double, bioParamId: Long){
        val calendar = Calendar.getInstance()
        val newBioValue = BioParameterValue(0,bioParamId,calendar,value)
        valueRepository.save(newBioValue)
    }

    suspend fun deleteBioValue(value: BioParameterValue){
        valueRepository.delete(value)
    }
}