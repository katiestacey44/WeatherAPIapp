package com.example.weatherapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun UserLogin(modifier: Modifier = Modifier, vm: AuthViewModel, navController: NavHostController, onLoginSuccess: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authSuccess = vm.authSuccess.observeAsState()
    val authError = vm.authError.observeAsState()

    Column(
        modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    )
    {
        Text("Login", fontSize = 20.sp)

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        OutlinedTextField(email,
            isError = authSuccess.value == false,
            onValueChange = { email = it },
            label = { Text("Email") })

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        OutlinedTextField(
            password, isError = authSuccess.value == false, onValueChange = { password = it },
            label = { Text("Password") }, visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        if (authError.value != null) {
            Text(
                text = authError.value ?: "",
                color = Color.Red,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        Row {
            Button(onClick = {
                vm.login(email, password) { uid, name ->
                    onLoginSuccess(uid.toString(), name.toString())
                }
            })
            {
                Text("Login")
            }

            Spacer(modifier = Modifier.padding(horizontal = 16.dp))

            Button(onClick = {
                navController.navigate(route = "signup")
            })
            {
                Text("Sign Up")
            }
        }
    }
}