package com.example.weatherapp

import android.app.AlarmManager
import android.content.Context
import java.time.ZoneId


class AndriodAlarmScheduler ( private val context: Context): AlarmScheduler{

    private val alarmManager = context. getSystemService(AlarmManager:: class.java)
    override fun schedule(item: AlarmItem) {

      alarmManager.setAndAllowWhileIdle(
          AlarmManager.RTC_WAKEUP,
          item.time.atZone(ZoneId.systemDefault()).toEpochSecond() *1000
      )
    }

    override fun cancel(item: AlarmItem) {
        TODO("Not yet implemented")
    }
}
