package com.example.weatherapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun FavoritesPage(viewModel: WeatherViewModel, navController: NavHostController) {
    val favoriteLocationsState = viewModel.favoriteLocations.observeAsState(emptyList())
    val favoriteLocations = favoriteLocationsState.value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
        Text(
            text = "Favorite Locations",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favoriteLocations.isEmpty()) {
            Text(text = "No favorites added yet", color = Color.Gray)
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(favoriteLocations) { city ->
                   Row(
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(8.dp)
                           .clickable {  }
                   ) {
                       Text(
                           text = city.toString(),
                           fontSize = 18.sp,
                           modifier = Modifier.weight(1f)
                       )
                       IconButton(onClick = { viewModel.removeFavorite(city.toString()) }) {
                           Icon(
                               imageVector = Icons.Filled.Delete,
                               contentDescription = "Remove Favorite",
                               tint = Color.Red
                           )
                       }
                   }
                }
            }
        }
    }
}