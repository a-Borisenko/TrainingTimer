package com.trainingtimer.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest


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


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}