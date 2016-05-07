package com.fkoteam.wakeup;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.SyncStateContract;
import android.util.Log;

/**
 * Created by SweetHome2 on 07/05/2016.
 */
public class AlarmIntentService extends IntentService {
    public AlarmIntentService() {
        super("AlarmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Intent intent2=new Intent(this, AlarmFired.class);

        intent2.putExtra("idAlarm", intent.getIntExtra("idAlarm", -1));
        intent2.putExtra("typeAlarm",intent.getIntExtra("typeAlarm",0));
        intent2.putExtra("isConnected", intent.getIntExtra("isConnected",0));
        intent2.putExtra("online", intent.getBooleanExtra("online", false));
        intent2.putExtra("vibration",intent.getBooleanExtra("vibration",false));


        Log.i("SimpleWakefulReceiver", "Completed service @ " + SystemClock.elapsedRealtime());

        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity( intent2);

        AlarmReceiver.completeWakefulIntent(intent);
    }
}