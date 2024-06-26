package com.trainingtimer.utils

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

const val ACTION_DISMISS = "dismiss_action"

const val DATABASE_NAME = "training-database"

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

fun <T> Flow<T>.launchWhenStarted(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launchWhenStarted {
        this@launchWhenStarted.collect()
    }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
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