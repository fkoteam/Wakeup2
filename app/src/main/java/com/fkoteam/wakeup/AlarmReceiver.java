package com.fkoteam.wakeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        WakeLocker.acquire(context);



        final boolean online=intent.getBooleanExtra("online",false);

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    boolean isConnected = false;

                    if(online) {
                        //check if connected!
                        int waitRetries = 0;
                        isConnected = isConnected(context);
                        //esperamos 10 veces 2 segundo
                        while (!isConnected && waitRetries < 10) {
                            //Wait to connect
                            Thread.sleep(2000);
                            waitRetries++;
                            isConnected = isConnected(context);
                        }
                    }

                    try {



                        Intent intent2=new Intent(context, AlarmIntentService.class);

                        intent2.putExtra("idAlarm", intent.getIntExtra("idAlarm", -1));
                        intent2.putExtra("typeAlarm",intent.getIntExtra("typeAlarm",0));
                        intent2.putExtra("isConnected",isConnected?1:0);
                        intent2.putExtra("online",online);
                        intent2.putExtra("vibration",intent.getBooleanExtra("vibration",false));



                        Log.i("SimpleWakefulReceiver", "Starting service @ " + SystemClock.elapsedRealtime());
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startWakefulService(context,intent2);

                    }catch (Exception e)

                    {
                        Log.e("AlarmReceiver-Error",e.getStackTrace()+"");
                    }


                } catch (Exception e) {
                }
            }
        };
        t.start();




    }



    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }



}
