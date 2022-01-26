package com.example.personalworkoutnotebook.model

data class Group(
    val name: String,
    val exerciseList: List<Exercise> = listOf()
)
{
}