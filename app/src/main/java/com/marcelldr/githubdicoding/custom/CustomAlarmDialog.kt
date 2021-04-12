package com.marcelldr.githubdicoding.custom

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.view.Window
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.marcelldr.githubdicoding.database.DatabaseHandler
import com.marcelldr.githubdicoding.database.DatabaseSchema
import com.marcelldr.githubdicoding.databinding.CustomAlarmDialogBinding
import com.marcelldr.githubdicoding.service.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.*

class CustomAlarmDialog(private var activity: AppCompatActivity) : Dialog(activity),
    TimePickerDialog.OnTimeSetListener {
    private var binding: CustomAlarmDialogBinding = CustomAlarmDialogBinding.inflate(layoutInflater)
    private var alarmReceiver: AlarmReceiver = AlarmReceiver()
    private var databaseHandler: DatabaseHandler = DatabaseHandler.getInstance(context)

    init {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.setContentView(binding.root)
        getUIReady()
    }

    private fun getUIReady() {
        databaseHandler.open()
        val settingCursor = databaseHandler.where(
            DatabaseSchema.SettingTable.TABLE_NAME,
            DatabaseSchema.SettingTable._ID,
            "1"
        )
        settingCursor.apply {
            if (count > 0) {
                moveToFirst()
                val alarm = getString(getColumnIndexOrThrow(DatabaseSchema.SettingTable.KEY_ALARM))
                binding.time.text = alarm
            }
        }
        databaseHandler.close()

        binding.timeAlarm.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val formatHour24 = true
            TimePickerDialog(activity, this, hour, minute, formatHour24).show()
        }
        binding.setAlarm.setOnClickListener {
            val time = binding.time.text.toString()

            if (time != "") {
                databaseHandler.open()
                val cursor = databaseHandler.where(
                    DatabaseSchema.SettingTable.TABLE_NAME,
                    DatabaseSchema.SettingTable._ID,
                    "1"
                )
                cursor.apply {
                    if (count > 0) {
                        val contentValues = ContentValues()
                        contentValues.put(DatabaseSchema.SettingTable.KEY_ALARM, time)
                        databaseHandler.update(
                            DatabaseSchema.SettingTable.TABLE_NAME,
                            DatabaseSchema.SettingTable._ID,
                            "1",
                            contentValues
                        )
                    } else {
                        val contentValues = ContentValues()
                        contentValues.put(DatabaseSchema.SettingTable._ID, "1")
                        contentValues.put(DatabaseSchema.SettingTable.KEY_ALARM, time)
                        databaseHandler.insert(
                            DatabaseSchema.SettingTable.TABLE_NAME,
                            contentValues
                        )
                    }
                }
                databaseHandler.close()
                alarmReceiver.setRepeatingAlarm(
                    context,
                    time,
                    "Reminder",
                    "Reminder untuk $time"
                )
                Toast.makeText(context, "Alarm telah diset ke $time", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Alarm belum diset", Toast.LENGTH_SHORT).show()
            }
        }
        binding.clearAlarm.setOnClickListener {
            databaseHandler.open()
            databaseHandler.delete(
                DatabaseSchema.SettingTable.TABLE_NAME,
                DatabaseSchema.SettingTable._ID,
                "1"
            )
            databaseHandler.close()
            alarmReceiver.cancelAlarm(context)
            binding.time.text = ""
            Toast.makeText(context, "Alarm dihapus", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        binding.time.text = timeFormat.format(calendar.time)
    }
}