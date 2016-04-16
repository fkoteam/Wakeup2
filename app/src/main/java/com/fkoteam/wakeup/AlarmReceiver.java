package com.fkoteam.wakeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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
    public void onReceive(Context context, Intent intent) {

        WakeLocker.acquire(context);

        // For our recurring task, we'll just display a message
        /**/
        Toast.makeText(context, "I'm running" + new Date().toString(), Toast.LENGTH_LONG).show();


        context.stopService(new Intent(context, MediaPlayerService.class));

        Intent serviceIntent = new Intent(context,MediaPlayerService.class);
        context.startService(serviceIntent);




        try {

            Intent intent2=new Intent(context, AlarmFired.class);

            //le pasamos el id de la alarma a AlarmFired
            int idAlarm = intent.getIntExtra("idAlarm", -1);
            intent2.putExtra("idAlarm", idAlarm);
            intent2.putExtra("typeAlarm",intent.getIntExtra("typeAlarm",0));


            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            context.startActivity(intent2);

        }catch (Exception e)

        {
            Log.e("AlarmReceiver-Error",e.getStackTrace()+"");
        }



        /*Por si quieres notificacion
        Intent service1 = new Intent(context, MyAlarmService.class);
        context.startService(service1);*/




    }


}
