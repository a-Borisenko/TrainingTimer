package com.trainingtimer.timerapp.views.details

import android.content.BroadcastReceiver
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainingtimer.foundation.data.TrainingRepositoryImpl
import com.trainingtimer.foundation.domain.AddTrainingUseCase
import com.trainingtimer.foundation.domain.EditTrainingUseCase
import com.trainingtimer.foundation.domain.GetTrainingListUseCase
import com.trainingtimer.foundation.domain.GetTrainingUseCase
import com.trainingtimer.foundation.domain.Training
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrainingViewModel : ViewModel() {

    private val repository = TrainingRepositoryImpl.get()
    private val getTrainingUseCase = GetTrainingUseCase(repository)
    private val addTrainingUseCase = AddTrainingUseCase(repository)
    private val editTrainingUseCase = EditTrainingUseCase(repository)
    private val getTrainingListUseCase = GetTrainingListUseCase(repository)

    var isCounting = false
    var progr = 100f
    lateinit var timeReceiver: BroadcastReceiver
    lateinit var trainingLD: LiveData<Training?>
    lateinit var trainNumber: LiveData<List<Training>>

    private val _errorInputTimes = MutableLiveData<Boolean>()
    val errorInputTimes: LiveData<Boolean>
        get() = _errorInputTimes

    private val _errorInputTitle = MutableLiveData<Boolean>()
    val errorInputTitle: LiveData<Boolean>
        get() = _errorInputTitle

    private val _errorInputSets = MutableLiveData<Boolean>()
    val errorInputSets: LiveData<Boolean>
        get() = _errorInputSets

    private val _training = MutableLiveData<Training>()
    val training: LiveData<Training>
        get() = _training

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _secRemain = MutableLiveData<Long>()
    val secRemain: LiveData<Long>
        get() = _secRemain

    private val _progress = MutableLiveData<Float>()
    val progress: LiveData<Float>
        get() = _progress

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    fun getTraining(trainingId: Int) {
        val item = getTrainingUseCase.getTraining(trainingId)
        trainingLD = item
    }

    fun getTrainingNumber() {
        trainNumber = getTrainingListUseCase.getTrainingList()
    }

    fun updateTime(sec: Long) {
        _secRemain.value = sec
        Log.d("viewModel", "sec remain = $sec")
    }

    /*fun getTime() {
        //get time from DataBase
    }

    fun getDialogTime() {
        //get time from dialogFragment
    }

    fun startTimer() {
        //start TimerService
    }

    fun stopTimer() {
        //stop TimerService
    }

    fun registerReceiver() {
        //receive counting down
        val intentFilter = IntentFilter()
        intentFilter.addAction("Counter")

        timeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val secIntent = intent.getLongExtra("TimeRemaining", 0)
                progr = intent.getFloatExtra("Progress", 100f)
                if (secIntent > 0) {
                    updateTime(secIntent)
                } else {
                    updateTime(0)
//                    isClickable(true)
                }
//                updateCountdownUI()
//                updateProgressBarUI()
            }
        }
//        requireActivity().registerReceiver(timeReceiver, intentFilter)
    }

    fun unregisterReceiver() {
        //stop receiving countdown
    }*/

    fun addTraining(
        inputSets: String?,
        inputTitle: String?,
        inputTimes: String?,
        inputRest: String?,
        trainingId: Int
    ) {
        val sets = parseSets(inputSets)
        val title = parseTitle(inputTitle)
        val times = parseTimes(inputTimes)
        val rest = parseRest(inputRest)
        val fieldValid = validateInput(sets, title, times)
        if (fieldValid) {
            viewModelScope.launch {
                startLoad()
                delay(3000)
                finishLoad()
                val training = Training(sets.toInt(), title, times, rest, trainingId)
                addTrainingUseCase.addTraining(training)
                finishWork()
            }
        }
    }

    fun editTraining(
        inputSets: String?,
        inputTitle: String?,
        inputTimes: String?,
        inputRest: String?,
        trainingId: Int
    ) {
        val sets = parseSets(inputSets)
        val title = parseTitle(inputTitle)
        val times = parseTimes(inputTimes)
        val rest = parseRest(inputRest)
        val fieldValid = validateInput(sets, title, times)
        if (fieldValid) {
            viewModelScope.launch {
                startLoad()
                delay(3000)
                finishLoad()
                val item = Training(sets.toInt(), title, times, rest, trainingId)
                editTrainingUseCase.editTraining(item)
                finishWork()
            }
        }
    }

    private fun parseSets(inputSets: String?) = inputSets?.trim() ?: ""

    private fun parseTitle(inputTitle: String?) = inputTitle?.trim() ?: ""

    private fun parseTimes(inputTimes: String?) = inputTimes?.trim() ?: ""

    private fun parseRest(inputRest: String?) = inputRest?.trim() ?: ""

    private fun validateInput(sets: String, title: String, times: String): Boolean {
        var res = true
        if (times.isBlank()) {
            _errorInputTimes.value = true
            res = false
        }
        if (title.isBlank()) {
            _errorInputTitle.value = true
            res = false
        }
        if (sets.isBlank()) {
            _errorInputSets.value = true
            res = false
        }
        return res
    }

    fun resetErrorInputTimes() {
        _errorInputTimes.value = false
    }

    fun resetErrorInputTitle() {
        _errorInputTitle.value = false
    }

    fun resetErrorInputSets() {
        _errorInputSets.value = false
    }

    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }

    private fun startLoad() {
        _loading.value = true
    }

    private fun finishLoad() {
        _loading.value = false
    }
}