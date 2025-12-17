package com.berkantagur.lifecyclelogger

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LifecycleLogViewModel : ViewModel() {
    val logs = mutableStateListOf<String>()

    private fun nowTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        return sdf.format(Date())
    }

    fun addEvent(event: String) {
        val line = "${nowTimestamp()} — $event"
        logs.add(0, line)
    }

    fun clear() = logs.clear()
}
