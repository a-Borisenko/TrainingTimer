package com.trainingtimer.domain

data class Training(
    val sets: String,
    val title: String,
    val times: String,
    val rest: String,
    var id: Int = UNDEFINED_ID
) {
    companion object {

        const val UNDEFINED_ID = -1
    }
}