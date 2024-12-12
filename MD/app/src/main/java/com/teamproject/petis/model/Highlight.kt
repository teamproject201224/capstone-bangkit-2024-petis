package com.teamproject.petis.model

data class Highlight(
    val title: String,
    val description: String,
    val icon: Int,
    val backgroundDrawable: Int? = null
)