package com.fkoteam.wakeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmFired extends AppCompatActivity {
    //por defecto, 5 minutos
    int progress = 2;
    AdView mAdView;
    Button btnSnoozePopup;
    private static Timer timer;
    Button btnCancelPopup;
    private boolean isInFocus = false;
    int idAlarm;
    boolean buttonClicked=false;
    private Intent serviceIntentMediaPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_fired);
        buttonClicked=false;

        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        Calendar timestamp = Calendar.getInstance();
        Spannable text = new SpannableString(getString(R.string.alarm_at)+" "+format1.format(timestamp.getTime()));
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        if(AlarmList.getCurrentAlarms()==null || AlarmList.getCurrentAlarms().size()<1) {
            AlarmList.initAlarmPrefs(getApplicationContext(), false);
        }

        if(serviceIntentMediaPlayer!=null) {
            stopService(serviceIntentMediaPlayer);
            serviceIntentMediaPlayer=null;
        }

        Bundle b = getIntent().getExtras();
        Intent serviceIntent = new Intent(this,MediaPlayerService.class);
        idAlarm= b.getInt("idAlarm", -1);
        serviceIntent.putExtra("idAlarm",idAlarm);
        if(idAlarm==-1)
            Log.i("ERROR","error");
        serviceIntent.putExtra("typeAlarm",b.getInt("typeAlarm", 0));
        serviceIntent.putExtra("isConnected",b.getInt("isConnected", 0));
        serviceIntent.putExtra("online",b.getBoolean("online", false));
        serviceIntent.putExtra("vibration", b.getBoolean("vibration", false));

        serviceIntentMediaPlayer=serviceIntent;
        startService(serviceIntentMediaPlayer);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("1D2A83BF786B1B994E5672D7AE75A822").addTestDevice("DE266E0FACC2F0D7336E2678258DDD82").build();
            mAdView.loadAd(adRequest);


            timer = new Timer("timer", true);
            btnSnoozePopup = (Button) findViewById(R.id.snooze);
            //si no se para en 3 minutos, autosnooze
            timer.schedule(new autoSnooze(), 3 * 60 * 1000);


            final int isConnected = b.getInt("isConnected");
            final boolean online = b.getBoolean("online");

            //el movil no está online y el usuario queria alarma online
            if (isConnected == 0 && online) {
                TextView connectionProblems = (TextView) findViewById(R.id.connectionProblems);
                connectionProblems.setText(getString(R.string.connectionProblems));

            }
            SeekBar seekSnooze = (SeekBar) findViewById(R.id.seekSnooze);


            seekSnooze.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


                @Override
                public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                    progress = progresValue;
                    TextView textView = (TextView) findViewById(R.id.snoozeTimeTxt);
                    String s = "";
                    if (seekBarToMinutes(progress) > 1)
                        s = getString(R.string.plural);

                    textView.setText(seekBarToMinutes(progress) + " " + getString(R.string.minute) + s);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    TextView textView = (TextView) findViewById(R.id.snoozeTimeTxt);
                    String s = "";
                    if (seekBarToMinutes(progress) > 1)
                        s = getString(R.string.plural);

                    textView.setText(seekBarToMinutes(progress) + " " + getString(R.string.minute) + s);

                }
            });


            btnCancelPopup = (Button) findViewById(R.id.stopAlarm);
            btnCancelPopup.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    /*Intent intent = new Intent();
                    intent.setClass(AlarmFired.this,
                            MainActivity.class);
                    intent.putExtra("snooze", -1);

                    intent.putExtra("tryDisableAlarm", idAlarm);*/


                    /*startActivity(intent);*/

                    if(!buttonClicked) {
                        buttonClicked = true;
                        int pos=AlarmList.unSnoozeAlarm(idAlarm, -1);
                        if(pos>-1)
                            AlarmList.tryDisableAlarm(pos);
                        finish();

                    }
                }
            });

            btnSnoozePopup.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {



                    /*Intent intent = new Intent();
                    intent.setClass(AlarmFired.this,
                            MainActivity.class);

                    intent.putExtra("snooze", seekBarToMinutes(progress));
                    intent.putExtra("tryDisableAlarm", idAlarm);*/


                    //startActivity(intent);
                    if(!buttonClicked) {


                        buttonClicked = true;
                        int pos=AlarmList.unSnoozeAlarm(idAlarm, -1);
                        if(pos>-1)
                            AlarmList.snoozeAlarm(seekBarToMinutes(progress), pos);
                        finish();
                    }

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
        //ya no hace falta el autosnooze
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAdView!=null)
            mAdView.resume();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (!isInFocus) {


            if(serviceIntentMediaPlayer!=null) {
                stopService(serviceIntentMediaPlayer);
                serviceIntentMediaPlayer=null;
            }
            WakeLocker.release();

            if(!buttonClicked) {
                int pos=AlarmList.unSnoozeAlarm(idAlarm, -1);
                if(pos>-1)
                    AlarmList.tryDisableAlarm(pos);
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        isInFocus = hasFocus;
    }
    @Override
    public void onBackPressed() {
    }



    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            btnSnoozePopup.performClick();
        }
    };

    //Task for timer to execute when time expires
    class autoSnooze extends TimerTask {
        @Override
        public void run(){
            handler.sendEmptyMessage(0);
        }
    }


}
