package com.example.sleeptracking

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sleeptracking.ui.theme.SleepTrackingTheme
import java.util.*

class TrackActivity : ComponentActivity() {

    private lateinit var databaseHelper: TimeLogDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseHelper = TimeLogDatabaseHelper(this)
        setContent {
            SleepTrackingTheme{
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //ListListScopeSample(timeLogs)

                    val data=databaseHelper.getTimeLogs();
                    Log.d("Sandeep" ,data.toString())
                    val timeLogs = databaseHelper.getTimeLogs()
                    ListListScopeSample(timeLogs)
                }
            }
        }
    }
}


@Composable
fun ListListScopeSample(timeLogs: List<TimeLogDatabaseHelper.TimeLog>) {
    val imageModifier = Modifier
    Image(
        painterResource(id = R.drawable.last),
        contentScale = ContentScale.FillBounds,
        contentDescription = "",
        modifier = imageModifier.alpha(0.3F)
    )

    Text(
        text = "Sleep Tracking",
        modifier = Modifier
            .padding(top = 16.dp, start = 106.dp),
        color = Color.Black,
        fontSize = 24.sp
    )

    // Spacer to separate the "Sleep Tracking" text and the table header
    Spacer(modifier = Modifier.height(40.dp))

    // Table Header
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 100.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Start Time", modifier = Modifier
            .weight(1f)
            .padding(top = 100.dp, start = 70.dp), color = Color.Black, fontSize = 16.sp)
        Text("End Time", modifier = Modifier
            .weight(1f)
            .padding(top = 100.dp, start = 70.dp), color = Color.Black, fontSize = 16.sp)
    }

    // Spacer between the header and rows for additional clarity
    Row {
        Spacer(modifier = Modifier.height(90.dp))

        // Table Rows
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp , top = 200.dp) ) {

            items(timeLogs) { timeLog ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDateTime(timeLog.startTime),
                        modifier = Modifier.weight(1f) .padding(horizontal = 16.dp),
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Text(
                        text = timeLog.endTime?.let { formatDateTime(it) } ?: "N/A",
                        modifier = Modifier.weight(1f) .padding(horizontal = 16.dp),
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

private fun formatDateTime(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}