package com.trainingtimer.timerapp.views.details

import kotlinx.coroutines.flow.Flow

interface TimeService {

    fun listenCurrentTime(): Flow<Long>
}