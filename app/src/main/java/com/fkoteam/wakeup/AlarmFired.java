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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;

public class AlarmFired extends AppCompatActivity {
    //por defecto, 5 minutos
    int progress = 2;
    AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_fired);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("1D2A83BF786B1B994E5672D7AE75A822").build();
        mAdView.loadAd(adRequest);



        Bundle b = getIntent().getExtras();
        final int idAlarm=b.getInt("idAlarm");
        final int isConnected=b.getInt("isConnected");


        if(isConnected==0)
        {
            TextView connectionProblems = (TextView) findViewById(R.id.connectionProblems);
            connectionProblems.setText(getString(R.string.connectionProblems));

        }
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
                String s="";
                if(seekBarToMinutes(progress)>1)
                    s= getString(R.string.plural);

                textView.setText(seekBarToMinutes(progress) + " "+getString(R.string.minute)+s);

            }
        });




        Button btnCancelPopup = (Button) findViewById(R.id.stopAlarm);
        btnCancelPopup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Intent myIntent = new Intent(getBaseContext(),
                        AlarmReceiver.class);

                PendingIntent pendingIntent
                        = PendingIntent.getBroadcast(getBaseContext(),
                        idAlarm, myIntent, 0);

                AlarmManager alarmManager
                        = (AlarmManager) getSystemService(ALARM_SERVICE);

                alarmManager.cancel(pendingIntent);*/

                Intent intent = new Intent();
                intent.setClass(AlarmFired.this,
                        MainActivity.class);
                intent.putExtra("snooze", -1);

                intent.putExtra("tryDisableAlarm", idAlarm);

                WakeLocker.release();
                stopService(new Intent(getApplicationContext(), MediaPlayerService.class));

                startActivity(intent);
                finish();



            }
        });

        Button btnSnoozePopup = (Button) findViewById(R.id.snooze);
        btnSnoozePopup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               /* Intent myIntent = new Intent(getBaseContext(),
                        AlarmReceiver.class);

                PendingIntent pendingIntent
                        = PendingIntent.getBroadcast(getBaseContext(),
                        idAlarm, myIntent, 0);

                AlarmManager alarmManager
                        = (AlarmManager)getSystemService(ALARM_SERVICE);

                alarmManager.cancel(pendingIntent);*/

                Intent intent = new Intent();
                intent.setClass(AlarmFired.this,
                        MainActivity.class);

                intent.putExtra("snooze", seekBarToMinutes(progress));
                intent.putExtra("tryDisableAlarm", idAlarm);


                WakeLocker.release();
                stopService(new Intent(getApplicationContext(), MediaPlayerService.class));

                startActivity(intent);
                finish();



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
    @Override
    protected void onPause() {
        if(mAdView!=null)
            mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAdView!=null)
            mAdView.resume();
    }
}
