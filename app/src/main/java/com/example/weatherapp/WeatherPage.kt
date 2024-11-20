package com.example.weatherapp

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.WeatherModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherPage(viewModel: WeatherViewModel) {
    val context = LocalContext.current
    var city by remember { mutableStateOf("") }
    val weatherResult by viewModel.weatherResult.observeAsState()
    var showMenu by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Search Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = city,
                onValueChange = { city = it },
                label = { Text("Search Location") },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Blue,
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color.Blue
                )
            )
            IconButton(onClick = { viewModel.getData(city) }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Location",
                    tint = Color.Blue
                )
            }
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = Color.Blue
                )
            }
        }

        // Menu Options
        if (showMenu) {
            Dialog(
                onDismissRequest = { showMenu = false },
                content = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Menu",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.3f)
                            )
                            Text(
                                text = "Logout",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { /* Handle logout */ }
                                    .padding(vertical = 8.dp)
                            )
                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.3f)
                            )
                            Text(
                                text = "Notifications",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showMenu = false
                                        showNotificationsDialog = true
                                    }
                                    .padding(vertical = 8.dp)
                            )
                            if (showNotificationsDialog) {
                                NotificationsSettingsDialog(
                                    currentWeatherData = (weatherResult as? NetworkResponse.Success)?.data,
                                    onDismiss = { showNotificationsDialog = false },
                                    onSaveNotification = { time, enabled ->
                                    // Implement notification scheduling logic here
                                    // You'll need to use WorkManager or AlarmManager to schedule notifications
                                    // The frequency parameter can be used to determine repeat interval
                                }
                            )
                        }
                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.3f)
                            )
                            Text(
                                text = "View Current Location",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { (context as? MainActivity)?.requestLocationPermission() }
                                    .padding(vertical = 8.dp)

                            )
                        }
                    }
                }
            )
        }


        // Add Favorite Location
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Add to Favorite Location",
                tint = Color.Red,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add favorite location",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }

        // Display Weather Data
        when (val result = weatherResult) {
            is NetworkResponse.Error -> {
                Text(
                    text = result.message,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
            NetworkResponse.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            is NetworkResponse.Success -> {
                WeatherDetails(data = result.data)
            }
            null -> {}
        }
    }
}


@Composable
fun WeatherDetails(data: WeatherModel) {
    val dateFormatter = SimpleDateFormat("EEE", Locale.getDefault()) // "EEE" gives the abbreviated weekday name (Mon, Tue, etc.)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFe0f7fa))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location Icon",
                    tint = Color.Blue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Row {
                    Text(
                        text = data.location.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = data.location.country,
                    fontSize = 15.sp,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Temperature and Icon
            Text(
                text = "${data.current.temp_f}°F",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Blue
            )
            AsyncImage(
                modifier = Modifier.size(50.dp),
                model = "https:${data.current.condition.icon}".replace("64x64", "128x128"),
                contentDescription = "Condition Icon"
            )
            Text(
                text = data.current.condition.text,
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Additional Weather Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFe3f2fd))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weatherKeyVal("Humidity", "${data.current.humidity}%")
                        weatherKeyVal("Wind Speed", "${data.current.wind_mph} mph")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weatherKeyVal("Sunrise", data.forecast.forecastday[0].astro.sunrise)
                        weatherKeyVal("Sunset", data.forecast.forecastday[0].astro.sunset)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weatherKeyVal("Wind Chill", "${data.current.windchill_f}°F")
                        weatherKeyVal("Feels like", "${data.forecast.forecastday[0].hour[0].pressure_in}°F")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 5-Day Forecast
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween // Use SpaceBetween to distribute items evenly
            ) {
                // Iterate over the forecast days (1 through 4)
                data.forecast.forecastday.drop(1).take(5).forEach { forecastDay ->
                    Column(
                        modifier = Modifier.weight(1f), // Each column gets equal width
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Convert the date to a weekday name (Mon, Tue, etc.)
                        val weekdayName = try {
                            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(forecastDay.date)
                            date?.let { dateFormatter.format(it) } ?: ""
                        } catch (e: Exception) {
                            ""
                        }

                        Text(
                            text = weekdayName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${forecastDay.day.avgtemp_f}°F",
                            fontSize = 16.sp,
                            color = Color.Blue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}




@Composable
fun weatherKeyVal(key: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Blue)
        Text(text = key, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSettingsDialog(
    currentWeatherData: WeatherModel?,
    onDismiss: () -> Unit,
    onSaveNotification: (String, Boolean) -> Unit
) {
    var selectedHour by remember { mutableStateOf(12) }
    var selectedMinute by remember { mutableStateOf(0) }
    var isAm by remember { mutableStateOf(true) }
    var enableNotifications by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val scheduler = remember { AndriodAlarmScheduler(context) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Notification Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Notifications Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Enable Notifications", fontWeight = FontWeight.Medium)
                    Switch(
                        checked = enableNotifications,
                        onCheckedChange = { enableNotifications = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Time Selection (conditionally enabled)
                if (enableNotifications) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Hour Selector
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                text = "%02d".format(selectedHour),
                                modifier = Modifier
                                    .clickable { selectedHour = (selectedHour % 12) + 1 }
                                    .padding(8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        Text(" : ", fontSize = 20.sp)
                        // Minute Selector
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                text = "%02d".format(selectedMinute),
                                modifier = Modifier
                                    .clickable { selectedMinute = (selectedMinute + 15) % 60 }
                                    .padding(8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        // AM/PM Selector
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                .clickable { isAm = !isAm }
                        ) {
                            Text(
                                text = if (isAm) "AM" else "PM",
                                modifier = Modifier.padding(8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Button(
                        onClick = {
                            if (enableNotifications && currentWeatherData != null) {
                                val hour = if (isAm) selectedHour else selectedHour + 12
                                val alarmTime = LocalDateTime.now()
                                    .withHour(hour)
                                    .withMinute(selectedMinute)
                                    .withSecond(0)
                                    .withNano(0)
                                    // Ensure future time
                                    .let {
                                        if (it.isBefore(LocalDateTime.now()))
                                            it.plusDays(1)
                                        else it
                                    }

                                val message = "Current Weather: ${currentWeatherData.current.temp_f}°F, " +
                                        "${currentWeatherData.current.condition.text} " +
                                        "in ${currentWeatherData.location.name}"

                                val alarmItem = AlarmItem(
                                    time = alarmTime,
                                    message = message
                                )
                                scheduler.schedule(alarmItem)
                            } else {
                                // Create a dummy item to cancel
                                val dummyItem = AlarmItem(
                                    time = LocalDateTime.now(),
                                    message = "Weather Notification"
                                )
                                scheduler.cancel(dummyItem)
                            }

                            val formattedTime = if (enableNotifications) {
                                "%02d:%02d %s".format(selectedHour, selectedMinute, if (isAm) "AM" else "PM")
                            } else {
                                ""
                            }
                            onSaveNotification(formattedTime, enableNotifications)
                            onDismiss()
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

