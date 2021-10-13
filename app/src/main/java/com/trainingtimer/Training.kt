package com.trainingtimer

import java.util.*

data class Training(val trainingId: UUID = UUID.randomUUID(),
                    var title: String = "",
                    var sets: Int,
                    var times: Int)