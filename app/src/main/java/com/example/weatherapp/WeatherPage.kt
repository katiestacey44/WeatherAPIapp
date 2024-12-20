package com.example.weatherapp

import android.widget.Toast
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
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.WeatherModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherPage(viewModel: WeatherViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val uid = navController.currentBackStackEntry?.arguments?.getString("uid")
    // val name = navController.currentBackStackEntry?.arguments?.getString("name")
    var city by remember { mutableStateOf("") }
    val weatherResult by viewModel.weatherResult.observeAsState()
    var showMenu by remember { mutableStateOf(false) }
    val favoriteLocations by viewModel.favoriteLocations.observeAsState(emptyList())
    val isFavorite = favoriteLocations.contains(city)

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
                colors = TextFieldDefaults.colors(
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
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.3f)
                            )
                            Text(
                                text = "Notifications",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { navController.navigate("notifications-screen/$city") }
                                    .padding(vertical = 8.dp)
                            )
                            HorizontalDivider(
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
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.3f)
                            )
                            Text(
                                text = "My Favorites",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.getFavorites(uid.toString()); navController.navigate("favorites/${uid.toString()}") }
                                    .padding(vertical = 8.dp)
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.3f)
                            )
                            Text(
                                text = "Logout",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { navController.navigate(route = "login") }
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
                .padding(vertical = 10.dp)
                .clickable {
                    if (city.isNotEmpty()) {
                        if (!isFavorite) {
                            viewModel.addFavorite(city, uid.toString())
                            Toast.makeText(context, "$city added to favorites", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.removeFavorite(city, uid.toString())
                            Toast.makeText(context, "$city removed from favorites", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Toggle Favorite Location",
                tint = if (isFavorite) Color.Red else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isFavorite) "Remove from favorites" else "Add to favorites",
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
                        WeatherKeyVal("Humidity", "${data.current.humidity}%")
                        WeatherKeyVal("Wind Speed", "${data.current.wind_mph} mph")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherKeyVal("Sunrise", data.forecast.forecastday[0].astro.sunrise)
                        WeatherKeyVal("Sunset", data.forecast.forecastday[0].astro.sunset)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherKeyVal("Wind Chill", "${data.current.windchill_f}°F")
                        WeatherKeyVal("Feels like", "${data.forecast.forecastday[0].hour[0].pressure_in}°F")
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
fun WeatherKeyVal(key: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Blue)
        Text(text = key, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
    }
}
