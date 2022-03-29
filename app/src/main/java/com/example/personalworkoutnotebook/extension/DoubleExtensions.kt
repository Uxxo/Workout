package com.example.personalworkoutnotebook.extension

fun Double.toShowIt(): String{
    return if(this - this.toInt() == 0.0) this.toInt().toString()
           else this.toString()
}