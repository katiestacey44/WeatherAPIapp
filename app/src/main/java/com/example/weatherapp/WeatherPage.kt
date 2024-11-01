package com.example.weatherapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.WeatherModel

@Composable
fun WeatherPage(viewModel: WeatherViewModel) {
    var city by remember { mutableStateOf("") }
    val weatherResult by viewModel.weatherResult.observeAsState() // Using observeAsState correctly

    Column(
        modifier = Modifier.fillMaxWidth().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = city,
                onValueChange = { city = it },
                label = { Text(text = "Search Location") }
            )
            IconButton(onClick = { viewModel.getData(city) }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Location"
                )
            }
        }

        when (val result = weatherResult) {
            is NetworkResponse.Error -> {
                // Handle the error case
                Text(text = result.message) // Display the error message
            }
            NetworkResponse.Loading -> {
                // Display a loading indicator
               CircularProgressIndicator()
            }
            is NetworkResponse.Success -> {
                // Handle the success case
                WeatherDetails(data = result.data)
                // Display weather data here
            }
            null -> {}
        }
    }
}

@Composable
fun WeatherDetails(data: WeatherModel){

}
