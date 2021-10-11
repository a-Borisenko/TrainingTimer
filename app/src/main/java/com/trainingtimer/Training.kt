package com.trainingtimer

import java.util.*

data class Training(val trainingId: UUID = UUID.randomUUID(),
                    val title: String = "",
                    val sets: Int,
                    val times: Int)