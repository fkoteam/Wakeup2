package com.fkoteam.wakeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class AlarmFired extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_fired);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle b = getIntent().getExtras();
        final int idAlarm=b.getInt("idAlarm");



        Button btnCancelPopup = (Button) findViewById(R.id.stopAlarm);
        btnCancelPopup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(getBaseContext(),
                        AlarmReceiver.class);

                PendingIntent pendingIntent
                        = PendingIntent.getBroadcast(getBaseContext(),
                        idAlarm, myIntent, 0);

                AlarmManager alarmManager
                        = (AlarmManager) getSystemService(ALARM_SERVICE);

                alarmManager.cancel(pendingIntent);

                Intent intent = new Intent();
                intent.setClass(AlarmFired.this,
                        MainActivity.class);
                startActivity(intent);
                finish();
                WakeLocker.release();
                stopService(new Intent(getApplicationContext(), MediaPlayerService.class));

            }
        });

        Button btnSnoozePopup = (Button) findViewById(R.id.snooze);
        btnSnoozePopup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(getBaseContext(),
                        AlarmReceiver.class);

                PendingIntent pendingIntent
                        = PendingIntent.getBroadcast(getBaseContext(),
                        idAlarm, myIntent, 0);

                AlarmManager alarmManager
                        = (AlarmManager)getSystemService(ALARM_SERVICE);

                alarmManager.cancel(pendingIntent);

                Intent intent = new Intent();
                intent.setClass(AlarmFired.this,
                        MainActivity.class);
                //5minutos
                intent.putExtra("snooze", 5);
                startActivity(intent);
                finish();
                WakeLocker.release();
                stopService(new Intent(getApplicationContext(), MediaPlayerService.class));



            }
        });


    }

}
