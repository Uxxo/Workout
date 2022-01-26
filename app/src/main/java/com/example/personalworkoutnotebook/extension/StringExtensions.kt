package com.example.personalworkoutnotebook.extension

import java.util.*

fun String.toFirstUpperCase() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
fun String.isValidDouble(): Boolean {
    if (this.isBlank()) return false
    if (this.contains(' ')) return false
    if (this.contains('-') && this.indexOf('-') != 0) return false
    val symbols = this.toCharArray()
    if (symbols[0] == '.') return false
    var counter = 0
    symbols.forEach {
        if (it == '.') {
            counter++
        }
        if (counter > 1) {
            return false
        }
    }
    return true
}

fun String.toValidInt(): Int{

    var outputString = ""
    this.forEach {
        if(it.isDigit()) outputString +=it
    }

    return if(outputString.isNotEmpty()) outputString.toInt()
            else 0
}

