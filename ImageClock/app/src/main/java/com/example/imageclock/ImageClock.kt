package com.example.imageclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.imageclock.ui.theme.ImageClockTheme
import java.util.Calendar
import java.util.TimeZone

@Composable
fun ImageClock() {
    val calendar = remember { Calendar.getInstance() }
    val currentTime = remember { mutableStateOf(System.currentTimeMillis()) }

    val (hourTensResId, hourUnitsResId, minuteTensResId, minuteUnitsResId) =
        rememberTimeResourceIds(calendar, currentTime)
    val receiver = rememberBroadcastReceiver(calendar, currentTime)

    val context = LocalContext.current
    DisposableEffect(Unit) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        context.registerReceiver(receiver, filter)
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Column(modifier = Modifier.padding(20.dp)) {
        Row {
            Image(painter = painterResource(hourTensResId), contentDescription = "hourTensImg")
            Image(painter = painterResource(hourUnitsResId), contentDescription = "hourUnitsImg")
        }
        Row {
            Image(painter = painterResource(minuteTensResId), contentDescription = "minuteTensImg")
            Image(painter = painterResource(minuteUnitsResId), contentDescription = "minuteUnitsImg")
        }
    }
}

@Composable
private fun rememberBroadcastReceiver(
    calendar: Calendar,
    currentTime: MutableState<Long>
) = remember {
    object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.R)
        override fun onReceive(context: Context?, intent: Intent?) {
            if (Intent.ACTION_TIMEZONE_CHANGED == intent?.action) {
                val timeZone = TimeZone.getTimeZone(intent.getStringExtra(Intent.EXTRA_TIMEZONE))
                calendar.timeZone = timeZone
            }

            // update time
            currentTime.value = System.currentTimeMillis()
            calendar.timeInMillis = currentTime.value
        }
    }
}

fun getResourceId(num: Int) = when (num) {
    0 -> R.drawable.clock_num_0
    1 -> R.drawable.clock_num_1
    2 -> R.drawable.clock_num_2
    3 -> R.drawable.clock_num_3
    4 -> R.drawable.clock_num_4
    5 -> R.drawable.clock_num_5
    6 -> R.drawable.clock_num_6
    7 -> R.drawable.clock_num_7
    8 -> R.drawable.clock_num_8
    9 -> R.drawable.clock_num_9
    else -> R.drawable.clock_num_0
}

data class TimeResourceIds(
    val hourTensResId: Int,
    val hourUnitsResId: Int,
    val minuteTensResId: Int,
    val minuteUnitsResId: Int
)

@Composable
fun rememberTimeResourceIds(calendar: Calendar, currentTime: MutableState<Long>): TimeResourceIds {
    val hourTensResId by remember(currentTime.value) {
        derivedStateOf { getResourceId(calendar.get(Calendar.HOUR_OF_DAY) / 10) }
    }
    val hourUnitsResId by remember(currentTime.value) {
        derivedStateOf { getResourceId(calendar.get(Calendar.HOUR_OF_DAY) % 10) }
    }
    val minuteTensResId by remember(currentTime.value) {
        derivedStateOf { getResourceId(calendar.get(Calendar.MINUTE) / 10) }
    }
    val minuteUnitsResId by remember(currentTime.value) {
        derivedStateOf { getResourceId(calendar.get(Calendar.MINUTE) % 10) }
    }

    return TimeResourceIds(hourTensResId, hourUnitsResId, minuteTensResId, minuteUnitsResId)
}

@Preview
@Composable
fun ImageClockPreview() {
    ImageClockTheme {
        ImageClock()
    }
}