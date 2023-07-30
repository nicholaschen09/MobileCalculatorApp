package com.example.calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel :ViewModel(){
    var progressValue = ""

    var result = MutableLiveData<String>()
    init {
        result.value = ""
    }
    fun updateProgressValue(value:String)
    {
        progressValue = value
    }

    fun updateResult(value:String)
    {
        result.value = value
    }

}