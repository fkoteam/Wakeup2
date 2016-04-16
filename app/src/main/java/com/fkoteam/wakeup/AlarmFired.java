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
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Calendar;

public class AlarmFired extends AppCompatActivity {
    int progress = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_fired);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle b = getIntent().getExtras();
        final int idAlarm=b.getInt("idAlarm");
        final int typeAlarm=b.getInt("typeAlarm");

        SeekBar seekSnooze = (SeekBar) findViewById(R.id.seekSnooze);


        seekSnooze.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                TextView textView = (TextView) findViewById(R.id.snoozeTimeTxt);
                //todo
                textView.setText(seekBarToMinutes(progress) + " minutes");

            }
        });




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
                intent.putExtra("tryDisableAlarm", idAlarm);
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

                intent.putExtra("snooze", seekBarToMinutes(progress));
                intent.putExtra("typeAlarm", typeAlarm);
                intent.putExtra("idAlarm",idAlarm);



                startActivity(intent);
                finish();
                WakeLocker.release();
                stopService(new Intent(getApplicationContext(), MediaPlayerService.class));



            }
        });


    }


    private int seekBarToMinutes(int progress)
    {
        //0 -> 1 minuto
        //1 -> 2 minutos
        //2 -> 5 minutos
        //3 -> 8 minutos
        //4 -> 10 minutos
        //5 -> 15 minutos
        //6 -> 20 minutos

        if(progress==0)
            return 1;
        if(progress==1)
            return 2;
        if(progress==2)
            return 5;
        if(progress==3)
            return 8;
        if(progress==4)
            return 10;
        if(progress==5)
            return 15;
        if(progress==6)
            return 20;



        return 5;
    }

}
