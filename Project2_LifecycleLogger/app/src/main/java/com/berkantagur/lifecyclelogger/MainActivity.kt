package com.berkantagur.lifecyclelogger

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.berkantagur.lifecyclelogger.ui.theme.LifecycleLoggerTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "LifecycleLogger"
    }

    private val vm: LifecycleLogViewModel by viewModels()

    private fun addEvent(event: String) {
        vm.addEvent(event)
        Log.d(TAG, vm.logs.firstOrNull() ?: event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        addEvent("onCreate")

        setContent {
            LifecycleLoggerTheme {
                LifecycleLogScreen(
                    logs = vm.logs,
                    onClear = {
                        vm.clear()
                        addEvent("CLEAR (button)")
                    }
                )
            }
        }
    }

    override fun onStart() { super.onStart(); addEvent("onStart") }
    override fun onResume() { super.onResume(); addEvent("onResume") }
    override fun onPause() { super.onPause(); addEvent("onPause") }
    override fun onStop() { super.onStop(); addEvent("onStop") }
    override fun onRestart() { super.onRestart(); addEvent("onRestart") }

    override fun onDestroy() {
        addEvent("onDestroy")
        super.onDestroy()
    }
        // val reason = if (isFinishing) "finishing" else "config-change/process"
        // addEvent("onDestroy ($reason)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LifecycleLogScreen(
    logs: List<String>,
    onClear: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Lifecycle Logger") },
                actions = {
                    FilledTonalButton(
                        onClick = onClear,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(Icons.Outlined.DeleteSweep, contentDescription = "Clear")
                        Spacer(Modifier.width(8.dp))
                        Text("Clear")
                    }
                    Spacer(Modifier.width(12.dp))
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Logs (newest on top)",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            Divider()
            Spacer(Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(logs) { line ->
                    LogCard(line)
                }
            }
        }
    }
}

@Composable
private fun LogCard(line: String) {
    val (timestamp, event) = parseLine(line)
    val accent = eventColor(event)
    val icon = eventIcon(event)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(44.dp)
                    .background(accent)
            )

            Spacer(Modifier.width(12.dp))

            Icon(imageVector = icon, contentDescription = event, tint = accent)

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.ifBlank { "Event" },
                    style = MaterialTheme.typography.titleMedium,
                    color = accent
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

private fun parseLine(line: String): Pair<String, String> {
    val parts = line.split("—", limit = 2).map { it.trim() }
    val ts = parts.getOrNull(0) ?: line
    val ev = parts.getOrNull(1) ?: ""
    return ts to ev
}

private fun eventColor(event: String): Color = when (event) {
    "onCreate" -> Color(0xFF2E7D32)
    "onStart" -> Color(0xFF0277BD)
    "onResume" -> Color(0xFF6A1B9A)
    "onPause" -> Color(0xFFF9A825)
    "onStop" -> Color(0xFFD84315)
    "onRestart" -> Color(0xFF00897B)
    "onDestroy" -> Color(0xFFC62828)
    else -> Color(0xFF546E7A)
}

private fun eventIcon(event: String) = when (event) {
    "onCreate" -> Icons.Outlined.Info
    "onStart" -> Icons.Outlined.PlayArrow
    "onResume" -> Icons.Outlined.PlayArrow
    "onPause" -> Icons.Outlined.VisibilityOff
    "onStop" -> Icons.Outlined.StopCircle
    "onRestart" -> Icons.Outlined.Refresh
    "onDestroy" -> Icons.Outlined.StopCircle
    else -> Icons.Outlined.Info
}
