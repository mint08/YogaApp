package com.example.administrator.yogafitnessapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.administrator.yogafitnessapp.Database.YogaDB;

import java.util.Calendar;
import java.util.Date;

public class SettingPage extends AppCompatActivity {

    Button btnSave;
    RadioButton rdiEasy, rdiMedium, rdiHard;
    RadioGroup rdiGroup;
    YogaDB yogaDB;
    ToggleButton switchAlarm;
    TimePicker timePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        //Init View
        btnSave = findViewById(R.id.btnSave);
        rdiGroup = findViewById(R.id.rdiGroup);
        rdiEasy = findViewById(R.id.rdiEasy);
        rdiHard = findViewById(R.id.rdiHard);

        switchAlarm = findViewById(R.id.switchAlarm);

        timePicker = findViewById(R.id.timePicker);

        yogaDB = new YogaDB(this);

        int mode = yogaDB.getSettingMode();
        setRadioButton(mode);

        //Init Event
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkoutMode();
                saveAlarm(switchAlarm.isChecked());
                Toast.makeText(SettingPage.this, "SAVE!!!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void saveAlarm(boolean checked) {
        if(checked){
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent;
            PendingIntent pendingIntent;
            intent = new Intent(SettingPage.this, AlarmNotificationReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            //Set time to alarm
            Calendar calendar = Calendar.getInstance();
            Date toDay = Calendar.getInstance().getTime();
            calendar.set(toDay.getYear(), toDay.getMonth(), toDay.getDay(), timePicker.getHour(), timePicker.getMinute());
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            Log.d("DEBUG", "Alarm will wake up at: "+timePicker.getHour()+":"+timePicker.getMinute());
        }
        else {
            Intent intent = new Intent(SettingPage.this, AlarmNotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
        }
    }

    private void saveWorkoutMode() {
        int selectedID = rdiGroup.getCheckedRadioButtonId();
        if(selectedID == rdiEasy.getId())
            yogaDB.saveSettingMode(0);
        else if(selectedID == rdiMedium.getId())
            yogaDB.saveSettingMode(1);
        else if(selectedID == rdiHard.getId())
            yogaDB.saveSettingMode(2);
    }

    private void setRadioButton(int mode) {
        if(mode == 0 )
            rdiGroup.check(R.id.rdiEasy);
        else if(mode == 1)
            rdiGroup.check(R.id.rdiMedium);
        else if(mode == 2)
            rdiGroup.check(R.id.rdiHard);
    }
}
