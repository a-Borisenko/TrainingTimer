package com.trainingtimer.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun timeStringToLong(time: String): Long {
    val min = (time.split(":"))[0].toLong()
    val sec = (time.split(":"))[1].toLong()
    return (min * 60 + sec)
}

fun timeLongToString(time: Long): String {
    val min = time / 60
    val sec = time % 60
    return "${"%02d".format(min)}:${"%02d".format(sec)}"
}


fun areDatesEqual(dateFirst: Date?, dateSecond: Date?): Boolean {
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    if (dateFirst == null || dateSecond == null) return false
    return sdf.format(dateFirst) == sdf.format(dateSecond)
}


// extension function for Fragment that runs a Flow<T> collection in a viewLifecycleScope
fun <T> Flow<T>.collectInViewScope(fragment: Fragment, action: suspend (T) -> Unit) {
    fragment.viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        collectLatest(action)
    }
}


fun EditText.onChange(textChanged: ((String) -> Unit)) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            textChanged.invoke(s.toString())
        }
    })
}


fun View.visible() {
    this.isVisible = true
}

fun View.gone() {
    this.isVisible = false
}


fun Context.toast(message: String?) {
    message?.let {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}