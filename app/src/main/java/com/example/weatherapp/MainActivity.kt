package com.example.weatherapp

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.weatherapp.ui.theme.WeatherAppTheme
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherViewModel: WeatherViewModel

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                getLastKnownLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                getLastKnownLocation()
            }
            else -> {
                // No location access granted.
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        setContent {
            WeatherAppTheme {
                val navController = rememberNavController()
                NavHostScreen(navController = navController, weatherVM = weatherViewModel)
            }
        }
    }

    private fun getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        weatherViewModel.getDataByCoordinates(location.latitude, location.longitude)
                    }
                }
        }
    }

    fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
@Composable
fun NavHostScreen(navController: NavHostController, modifier: Modifier = Modifier, weatherVM: WeatherViewModel) {
    val authVM: AuthViewModel = viewModel<AuthViewModel>()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        NavHost(navController, startDestination = "login") {
            composable("weather-page/{name}/{uid}") {
                WeatherPage(weatherVM)
            }
            composable("login") {
                UserLogin(modifier = modifier, authVM, navController) { uid, name ->
                    navController.navigate("weather-page/$name/$uid")
                }
            }
            composable("signup") {
                UserSignup(navController = navController) { uid, name ->
                    navController.navigate("weather-page/$name/$uid")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

}

/*API fields
https://www.weatherapi.com/my/fields.asp

WeatherAppAPI Login
Username: Katiestacey44@gmail.com
Password: 357_Project


 */
/*
task 1: connect to API [X] -- katie
task 2: ensure that API will update screen [X] -- katie
task 3: create search bar [x]-- katie
task 4: make a favorite location button [X]-- katie
task 5: make favorite button components with funcationality
task 6: ensure that search bar will search locations [X] --katie
Task 7: ensure that Search bar will update weather app screen --katie
task 8: connect to firebase [X] -- katie
task 9: add more info and 5 day forcast -- katie (2 days done)
task 10: create register and login activity [X] -- gabbi
task 11: make contents for register screen
task 12: make contents for login screen
task 13: ensure that firebase will save registered users [X] -- gabbi
task 14: create firestore database for locations saved
task 15: create a save location option on search screen
task 16: ensure that locations are being saved in the firestore database
task 17: ensure that the saved locations is displays on the screen
task 18: add way to view current location --katie
task 19: added notification option button -- katie
task 20: create notification messages based on current weather
task 21: create notification alerts
task 22: create navController to navigate between screens [X] -- gabbi
task 23: create logout option/button --katie
task 24: update presentation:
    1: project overview
    2: api info
    3: project features
    4: project demo
    5: update app architechture
task 25: draft project tutorial
    1: overview
    2. getting started
    3: step by step instructions
    4: further discussion
    5: references

additional tasks:
make it look pretty
--scrolling ability?


 */