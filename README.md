# Android Weather App Tutorial 🌤️

A comprehensive guide to building a weather application using Android, Kotlin, and modern Android development practices.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Project Setup](#project-setup)
- [API Integration](#api-integration)
- [Implementation Steps](#implementation-steps)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Resources](#resources)

## Prerequisites

Before starting, ensure you have:
- Android Studio (latest stable version)
- Basic knowledge of Kotlin and Android development
- WeatherAPI.com account
- Android device or emulator for testing

## Project Setup

### Dependencies
Add these to your app-level `build.gradle`:

```gradle
dependencies {
    // Retrofit for network calls
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // Android Jetpack Components
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.6.1'
    
    // Google Play Services for Location
    implementation 'com.google.android.gms:play-services-location:21.0.1'
}
```

### Manifest Setup
Add these permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```

## API Integration

### 1. WeatherAPI Setup
1. Visit [WeatherAPI.com](https://www.weatherapi.com/)
2. Create an account and obtain API key
3. Test your key in the API Explorer
4. Save your base URL and API key

### 2. API Interface

```kotlin
interface WeatherAPI {
    @GET("/v1/forecast.json")
    suspend fun getWeather(
        @Query("key") apikey: String,
        @Query("q") city: String,
        @Query("days") days: String
    ): Response<WeatherModel>
}
```

### 3. Retrofit Instance

```kotlin
object RetrofitInstance {
    private const val BASE_URL = "https://api.weatherapi.com"
    
    private fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val weatherApi: WeatherAPI = getInstance().create(WeatherAPI::class.java)
}
```

## Implementation Steps

### 1. ViewModel Implementation

```kotlin
class WeatherViewModel : ViewModel() {
    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<WeatherModel>()
    val weatherResult: LiveData<WeatherModel> = _weatherResult

    fun getData(city: String, days: String = "5") {
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(YOUR_API_KEY, city, days)
                if (response.isSuccessful) {
                    _weatherResult.value = response.body()
                } else {
                    // Handle error
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }
}
```

### 2. UI Implementation

```kotlin
@Composable
fun WeatherPage(viewModel: WeatherViewModel) {
    val weatherResult = viewModel.weatherResult.observeAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(onSearch = { city ->
            viewModel.getData(city)
        })
        
        weatherResult.value?.let { weather ->
            WeatherDetails(data = weather)
        }
    }
}

@Composable
fun WeatherDetails(data: WeatherModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = data.location.name,
            fontSize = 30.sp
        )
        Text(
            text = "${data.current.temp_f}°F",
            fontSize = 56.sp
        )
    }
}
```

### 3. Location Services

```kotlin
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()
    }
    
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getCurrentLocation()
        }
    }
}
```

## Resources

- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Android Location Services](https://developer.android.com/training/location)
- [WeatherAPI Documentation](https://www.weatherapi.com/docs/)

## Important Notes

> ⚠️ Remember to replace `YOUR_API_KEY` with your actual WeatherAPI.com API key.

> 🔒 Never commit API keys directly in your code. Use environment variables or a secure configuration file.

## Contributing

Feel free to submit issues and enhancement requests!

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

---
Made with ❤️ by Catherine Stacey, Gabbi, Munsong, Blaze Lauer
