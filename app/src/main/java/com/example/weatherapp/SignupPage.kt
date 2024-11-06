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
import androidx.navigation.NavController

@Composable
fun UserSignup(modifier: Modifier = Modifier, navController: NavController, vm: AuthViewModel = AuthViewModel(), onLoginSuccess: (String, Any?) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val authError = vm.authError.observeAsState()

    Column(
        modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    )
    {
        Text(
            "Sign Up",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Confirm Password") }
        )

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        if (authError.value != null) {
            Text(
                text = authError.value ?: "",
                color = Color.Red,
                fontSize = 14.sp
            )
        }

        Row {
            Button(onClick = {
                vm.signup(name, email, password, confirmPassword) { uid, name ->
                    onLoginSuccess(uid, name)
                }
            })
            {
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.padding(horizontal = 16.dp))

            Button(onClick = {
                navController.navigate(route = "login")
            })
            {
                Text("Login")
            }
        }

    }
}