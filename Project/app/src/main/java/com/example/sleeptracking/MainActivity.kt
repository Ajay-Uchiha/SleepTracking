package com.example.sleeptracking

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.sleeptracking.ui.theme.SleepTrackingTheme
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var databaseHelper: TimeLogDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = TimeLogDatabaseHelper(this)
        databaseHelper.deleteAllData()
        setContent {
            SleepTrackingTheme{
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyScreen(this,databaseHelper)
                }
            }
        }
    }
}
@Composable
fun MyScreen(context: Context, databaseHelper: TimeLogDatabaseHelper) {
    var startTime by remember { mutableStateOf(0L) }
    var elapsedTime by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var showGoodSleepMessage1 by remember { mutableStateOf(false) }
    var showGoodSleepMessage2 by remember { mutableStateOf(false) }
    var showGoodSleepMessage3 by remember { mutableStateOf(false) }


    // LaunchedEffect to keep updating the elapsed time while the timer is running
    LaunchedEffect(isRunning) {
        if (isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime // Preserve elapsed time when resuming
            while (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime
                kotlinx.coroutines.delay(1000) // Update every second
            }
        }
    }
    LaunchedEffect(showGoodSleepMessage1) {
        if (showGoodSleepMessage1) {
            kotlinx.coroutines.delay(2000) // Show for 2 seconds
            showGoodSleepMessage1 = false // Hide after delay
        }
    }
    LaunchedEffect(showGoodSleepMessage2) {
        if (showGoodSleepMessage2) {
            kotlinx.coroutines.delay(2000) // Show for 2 seconds
            showGoodSleepMessage2 = false // Hide after delay
        }
    }
    LaunchedEffect(showGoodSleepMessage3) {
        if (showGoodSleepMessage3) {
            kotlinx.coroutines.delay(2000) // Show for 2 seconds
            showGoodSleepMessage3 = false // Hide after delay
        }
    }
    Image(
        painterResource(id = R.drawable.summa),
        contentScale = ContentScale.FillHeight,
        contentDescription = "",
        modifier = Modifier.alpha(0.3F),
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isRunning) {
            Button(onClick = {
                startTime = System.currentTimeMillis() + elapsedTime // Reset or resume from last elapsed time
                isRunning = true
            }) {
                Text("Start")
            }
        }  else {
            Button(onClick = {
                val endTime = System.currentTimeMillis()
                isRunning = false
                databaseHelper.addTimeLog(startTime, endTime) // Save the time log

                // Check if the sleep duration is between 7 and 8 hours
                val duration = endTime - startTime


                if (duration >= (7 * 60 * 60 * 1000) && duration <= (8 * 60 * 60 * 1000)) {
                    // Show "Good Sleep" message and image for 7-8 hours
                    showGoodSleepMessage2 = true
                } else if (duration < (7 * 60 * 60 * 1000)) {
                    // Show "Less than 7 hours" message and image
                    showGoodSleepMessage1 = true
                } else {
                    // Code for other cases (e.g., more than 8 hours)
                    showGoodSleepMessage3 = true
                }

            }) {
                Text("Stop")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Elapsed Time: ${formatTime(elapsedTime)}")

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            context.startActivity(
                Intent(context, TrackActivity::class.java)
            )
        }) {
            Text(text = "Track Sleep")
        }

        if (showGoodSleepMessage1) {
            Image(
                painter = painterResource(id = R.drawable.lesssleep),
                contentDescription = "bad Sleep",
                modifier = Modifier
                    .height(150.dp)
                    .width(150.dp)
            )
            Text(text = "bad Sleep!", style = MaterialTheme.typography.headlineSmall)
        }
        if (showGoodSleepMessage2) {
            Image(
                painter = painterResource(id = R.drawable.goodsleep),
                contentDescription = "Good Sleep",
                modifier = Modifier
                    .height(150.dp)
                    .width(150.dp)
            )
            Text(text = "Good Sleep!", style = MaterialTheme.typography.headlineSmall)
        }
        if (showGoodSleepMessage3) {
            Image(
                painter = painterResource(id = R.drawable.oversleep),
                contentDescription = "Over Sleep",
                modifier = Modifier
                    .height(150.dp)
                    .width(150.dp)
            )
            Text(text = "Over Sleep!", style = MaterialTheme.typography.headlineSmall)
        }


    }

}

private fun startTrackActivity(context: Context) {
    val intent = Intent(context, TrackActivity::class.java)
    ContextCompat.startActivity(context, intent, null)
}
fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd /n HH:mm:ss", Locale.getDefault())
    val currentTime = System.currentTimeMillis()
    return dateFormat.format(Date(currentTime))
}

fun formatTime(timeInMillis: Long): String {
    val hours = (timeInMillis / (1000 * 60 * 60)) % 24
    val minutes = (timeInMillis / (1000 * 60)) % 60
    val seconds = (timeInMillis / 1000) % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}