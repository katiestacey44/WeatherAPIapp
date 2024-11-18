package com.example.weatherapp

interface AlarmScheduler {

    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}