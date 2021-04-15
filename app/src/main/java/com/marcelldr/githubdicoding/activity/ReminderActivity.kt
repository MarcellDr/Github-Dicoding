package com.marcelldr.githubdicoding.activity

import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.marcelldr.githubdicoding.database.DatabaseHandler
import com.marcelldr.githubdicoding.database.DatabaseSchema
import com.marcelldr.githubdicoding.databinding.ActivityReminderBinding
import com.marcelldr.githubdicoding.service.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.*

class ReminderActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    private var alarmReceiver: AlarmReceiver = AlarmReceiver()
    private lateinit var binding: ActivityReminderBinding
    private lateinit var databaseHandler: DatabaseHandler
    private lateinit var preferences: SharedPreferences

    companion object {
        private const val PREFS_NAME = "reminder_pref"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)
        databaseHandler = DatabaseHandler.getInstance(applicationContext)
        preferences = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        setContentView(binding.root)
        noStatusBar()
        getUIReady()
    }

    private fun noStatusBar() {
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window?.statusBarColor = Color.TRANSPARENT
    }

    private fun getUIReady() {
        databaseHandler.open()
        val settingCursor = databaseHandler.where(
            DatabaseSchema.SettingTable.TABLE_NAME,
            DatabaseSchema.SettingTable.KEY_ID,
            "1"
        )
        settingCursor.apply {
            if (count > 0) {
                moveToFirst()
                val alarm = getString(getColumnIndexOrThrow(DatabaseSchema.SettingTable.KEY_ALARM))
                binding.time.text = alarm
            }
        }
        settingCursor.close()
        databaseHandler.close()
        binding.switchAlarm.isChecked = preferences.getBoolean("isOn", false)

        binding.backButton.setOnClickListener { finish() }
        binding.timeAlarm.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val formatHour24 = true
            TimePickerDialog(this@ReminderActivity, this, hour, minute, formatHour24).show()
        }
        binding.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val time = binding.time.text.toString()
                databaseHandler.open()
                val cursor = databaseHandler.where(
                    DatabaseSchema.SettingTable.TABLE_NAME,
                    DatabaseSchema.SettingTable.KEY_ID,
                    "1"
                )
                cursor.apply {
                    if (count > 0) {
                        val contentValues = ContentValues()
                        contentValues.put(DatabaseSchema.SettingTable.KEY_ALARM, time)
                        databaseHandler.update(
                            DatabaseSchema.SettingTable.TABLE_NAME,
                            DatabaseSchema.SettingTable.KEY_ID,
                            "1",
                            contentValues
                        )
                    } else {
                        val contentValues = ContentValues()
                        contentValues.put(DatabaseSchema.SettingTable.KEY_ID, "1")
                        contentValues.put(DatabaseSchema.SettingTable.KEY_ALARM, time)
                        databaseHandler.insert(
                            DatabaseSchema.SettingTable.TABLE_NAME,
                            contentValues
                        )
                    }
                }
                cursor.close()
                databaseHandler.close()
                alarmReceiver.setRepeatingAlarm(
                    applicationContext,
                    time,
                    "Reminder",
                    "Reminder untuk $time"
                )
                Toast.makeText(
                    applicationContext,
                    "Alarm telah diset ke $time",
                    Toast.LENGTH_SHORT
                )
                    .show()
                preferences.edit().putBoolean("isOn", true).apply()
                Log.i("Reminder", preferences.getBoolean("isOn", false).toString())

            } else {
                alarmReceiver.cancelAlarm(applicationContext)
                Toast.makeText(applicationContext, "Alarm dihapus", Toast.LENGTH_SHORT).show()
                preferences.edit().putBoolean("isOn", false).apply()
            }

        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        binding.time.text = timeFormat.format(calendar.time)
        binding.switchAlarm.isChecked = false
    }
}