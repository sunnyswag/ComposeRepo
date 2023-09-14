package com.example.imageclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import com.example.imageclock.ui.theme.ImageClockTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageClockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun ImageClock() {
    val context = LocalContext.current
    val currentTime = remember { Calendar.getInstance() }
    val currentHourTensResId by remember {
        derivedStateOf { getResourceId(currentTime.get(Calendar.HOUR_OF_DAY) / 10) }
    }
    val currentHourUnitsResId by remember {
        derivedStateOf { getResourceId(currentTime.get(Calendar.HOUR_OF_DAY) % 10) }
    }
    val currentMinuteTensResId by remember {
        derivedStateOf { getResourceId(currentTime.get(Calendar.MINUTE) / 10) }
    }
    val currentMinuteUnitsResId by remember {
        derivedStateOf { getResourceId(currentTime.get(Calendar.MINUTE) % 10) }
    }


    val intentReceiver = remember {
        object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                currentTime.timeInMillis = System.currentTimeMillis()
            }
        }
    }

    DisposableEffect(Unit) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        context.registerReceiver(intentReceiver, filter)
        onDispose {
            context.unregisterReceiver(intentReceiver)
        }
    }

    val hourTens = ImageBitmap.imageResource(id = currentHourTensResId)
    val hourUnits = ImageBitmap.imageResource(id = currentHourUnitsResId)
    val minuteTens = ImageBitmap.imageResource(id = currentMinuteTensResId)
    val minuteUnits = ImageBitmap.imageResource(id = currentMinuteUnitsResId)

    Canvas(modifier = Modifier) {
        drawImage(
            image = hourTens,
            dstOffset = IntOffset(0, 0)
        )
        drawImage(
            image = hourUnits,
            dstOffset = IntOffset(100, 0)
        )
        drawImage(
            image = minuteTens,
            dstOffset = IntOffset(200, 0)
        )
        drawImage(
            image = minuteUnits,
            dstOffset = IntOffset(300, 0)
        )
    }

}

fun getResourceId(num: Int): Int {
    return when (num) {
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
}
