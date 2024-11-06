package com.example.weatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val _authSuccess = MutableLiveData<Boolean>()
    val authSuccess: LiveData<Boolean?> get() = _authSuccess

    data class User (
        val name: String,
        val email: String,
    )

    private val _authError = MutableLiveData<String?>()
    val authError: LiveData<String?> get() = _authError

    fun login(email:String, password:String, onAuthenticated: (Any?, Any?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _authError.postValue("Email and password cannot be empty.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _authSuccess.postValue(true)
                    _authError.postValue(null)
                    val uid = it.user!!.uid
                    val usrDoc = db.collection("users").document(uid)

                    usrDoc.get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                val name = document.getString("name") ?: "Unknown"
                                onAuthenticated(uid, name)
                            } else {
                                _authError.postValue("User not found in Firestore.")
                            }
                        }
                        .addOnFailureListener { e ->
                            _authError.postValue("Failed to get user data: ${e.message}")
                        }
                }
                .addOnFailureListener { exception ->
                    _authSuccess.postValue(false)
                    _authError.postValue(exception.message)
                }
        }
    }
    fun signup(name:String,email:String, password:String, confirmPassword:String, onAuthenticated: (String, String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _authError.postValue("Email and password cannot be empty.")
            return
        }
        if (password != confirmPassword) {
            _authError.postValue("Passwords do not match.")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _authSuccess.postValue(true)
                    createNewUser(it.user!!.uid, name, email)
                    onAuthenticated(it.user!!.uid, name)
                }
                .addOnFailureListener {
                    _authSuccess.postValue(false)
                }
        }
    }

    private fun createNewUser(uid: String, name: String, email: String){
        val user = User(name, email)
        val usrColl = db.collection("users")
        viewModelScope.launch(Dispatchers.IO){
            usrColl.document(uid).set(user)
        }
    }
}