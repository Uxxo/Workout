package com.example.personalworkoutnotebook.extension

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

fun View.isVisible(isVisible : Boolean){
    this.visibility = if (isVisible) VISIBLE else GONE
}
