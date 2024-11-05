package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.example.weatherapp.ui.theme.WeatherAppTheme
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                val navController = rememberNavController()
                NavHostScreen(navController = navController, weatherVM = weatherViewModel)
            }
        }
    }
}

@Composable
fun NavHostScreen(navController: NavHostController, modifier: Modifier = Modifier, weatherVM: WeatherViewModel) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        NavHost(navController, startDestination = "weather-page") {
            composable("weather-page") {
                WeatherPage(weatherVM)
            }
            composable("login") {
                UserLogin()
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
task 4: make a favorite location button [X]-- katie and components(needs doing)
task 5: ensure that search bar will search locations [X] --katie
Task 6: ensure that Search bar will update weather app screen --katie
task 7: connect to firebase [X] -- katie
task 8: create register and login activity
task 9: make contents for register screen
task 10: make contents for login screen
task 11: ensure that firebase will save registered users
task 12: create firestore database for locations saved
task 13: create a save location option on search screen
task 14: ensure that locations are being saved in the firestore database
task 15: ensure that the saved locations is displays on the screen
task 16: create notification messages based on current weather
task 17: create notification alerts



additional tasks:
make it look pretty


 */