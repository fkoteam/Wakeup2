package com.fkoteam.wakeup;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nilanchala
 *         <p/>
 *         Broadcast reciever, starts when the device gets starts.
 *         Start your repeating alarm here.
 */
public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_alarms_file), Context.MODE_PRIVATE);
        List<AlarmInfo> currentAlarms = new ArrayList<AlarmInfo>();

        //      load tasks from preference

        try{
            currentAlarms = (ArrayList<AlarmInfo>) ObjectSerializer.deserialize(sharedPref.getString("AlarmasPrefs", ObjectSerializer.serialize(new ArrayList<AlarmInfo>())));
            for(AlarmInfo ai : currentAlarms)
            {
                if(ai.active)
                {
                    ai.setSnoozingId(null);
                    ai.setSnoozingTime(null);
                    Utils.startAlarm(ai, context,false);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}