package com.example.personalworkoutnotebook.extension

import java.util.*

fun Calendar.toText() = "${this.get(Calendar.DAY_OF_MONTH)}.${this.get(Calendar.MONTH) + 1}.${this.get(Calendar.YEAR)}"
fun Calendar.toFloat() = "${this.get(Calendar.DAY_OF_MONTH)}.${this.get(Calendar.MONTH) +1}".toFloat()