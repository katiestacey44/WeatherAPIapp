package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionStatus
import android.app.TimePickerDialog
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherapp.workers.NotificationWorker
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit
import androidx.compose.runtime.DisposableEffect

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationScreen(viewModel: WeatherViewModel, navController: NavHostController, city: String) {
    val context = LocalContext.current
    val locationHelper = LocationHelper(context)
    var currentCity by remember { mutableStateOf(city) }
    var notificationTime by remember { mutableStateOf(LocalTime.now()) }
    var isNotificationsEnabled by remember { mutableStateOf(viewModel.isNotificationEnabled(context)) }

    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // Fetch location and set city when permission is granted
    LaunchedEffect(Unit) {
        if (permissionState.status == PermissionStatus.Granted) {
            locationHelper.requestLocationUpdates { fetchedCity ->
                currentCity = fetchedCity ?: "Unknown"
                if (currentCity != "Unknown") {
                    viewModel.saveCity(currentCity)
                    viewModel.scheduleNotification(notificationTime, currentCity, context)
                }
            }
        } else {
            permissionState.launchPermissionRequest()
        }
    }

    DisposableEffect(context) {
        onDispose {
            locationHelper.stopLocationUpdates()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Notification Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Enable Notifications", fontSize = 18.sp)
            Switch(
                checked = isNotificationsEnabled,
                onCheckedChange = { isNotificationsEnabled = it }
            )
        }

        if (isNotificationsEnabled) {
            TimePicker(
                selectedTime = notificationTime,
                onTimeChange = { notificationTime = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.saveNotificationEnabled(context, isNotificationsEnabled)
            if (isNotificationsEnabled) {
                viewModel.scheduleNotification(notificationTime, currentCity, context)
            } else {
                viewModel.cancelNotification()
            }
            navController.popBackStack()
        }) {
            Text("Save Settings")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePicker(selectedTime: LocalTime, onTimeChange: (LocalTime) -> Unit) {
    val context = LocalContext.current
    val timePickerDialog = remember { mutableStateOf(false) }

    if (timePickerDialog.value) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                onTimeChange(LocalTime.of(hourOfDay, minute))
                timePickerDialog.value = false
            },
            selectedTime.hour,
            selectedTime.minute,
            false
        ).show()
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Select Time: ${selectedTime.format(DateTimeFormatter.ofPattern("hh:mm a"))}")
        IconButton(onClick = { timePickerDialog.value = true}) {
            Icon(imageVector = Icons.Filled.AccessTime, contentDescription = "Select Time")
        }
    }
}
