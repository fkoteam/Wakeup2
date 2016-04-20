package com.fkoteam.wakeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SweetHome2 on 19/04/2016.
 */
public class Utils {


    public static void startAlarm(AlarmInfo t, Context c) {
        AlarmManager manager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);


        if (t.getSnoozingTime() != null && t.getSnoozingId() != null) {
            Intent alarmIntent = new Intent(c, AlarmReceiver.class);
            alarmIntent.putExtra("idAlarm", t.getSnoozingId().intValue());
            alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
            alarmIntent.putExtra("vibration",t.isVibration());
            alarmIntent.putExtra("online",t.isOnline());
            alarmIntent.setAction(String.valueOf(t.getSnoozingId().intValue()));


            PendingIntent pendingIntent = PendingIntent.getBroadcast(c, t.getSnoozingId().intValue(), alarmIntent, 0);

            manager.set(AlarmManager.RTC_WAKEUP,
                    t.getSnoozingTime().getTimeInMillis(), pendingIntent);
        } else {


            if (t.anyRepeat()) {
                int i = 0;
                if (t.repeatMon) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(c, AlarmReceiver.class);
                    alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                    alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                    alarmIntent.putExtra("vibration",t.isVibration());
                    alarmIntent.putExtra("online",t.isOnline());
                    alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(i).intValue()));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(c, t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                    i++;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                            setCalendarAlarm(2, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);

                }
                if (t.repeatTue) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(c, AlarmReceiver.class);
                    alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                    alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                    alarmIntent.putExtra("vibration",t.isVibration());
                    alarmIntent.putExtra("online",t.isOnline());
                    alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(i).intValue()));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(c, t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                    i++;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                            setCalendarAlarm(3, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
                }
                if (t.repeatWed) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(c, AlarmReceiver.class);
                    alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                    alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                    alarmIntent.putExtra("vibration",t.isVibration());
                    alarmIntent.putExtra("online",t.isOnline());
                    alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(i).intValue()));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(c, t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                    i++;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                            setCalendarAlarm(4, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
                }
                if (t.repeatThu) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(c, AlarmReceiver.class);
                    alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                    alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                    alarmIntent.putExtra("vibration",t.isVibration());
                    alarmIntent.putExtra("online",t.isOnline());
                    alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(i).intValue()));


                    PendingIntent pendingIntent = PendingIntent.getBroadcast(c, t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                    i++;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                            setCalendarAlarm(5, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
                }
                if (t.repeatFri) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(c, AlarmReceiver.class);
                    alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                    alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                    alarmIntent.putExtra("vibration",t.isVibration());
                    alarmIntent.putExtra("online",t.isOnline());
                    alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(i).intValue()));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(c, t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                    i++;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                            setCalendarAlarm(6, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
                }
                if (t.repeatSat) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(c, AlarmReceiver.class);
                    alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                    alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                    alarmIntent.putExtra("vibration",t.isVibration());
                    alarmIntent.putExtra("online",t.isOnline());
                    alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(i).intValue()));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(c, t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                    i++;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                            setCalendarAlarm(7, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
                }
                if (t.repeatSun) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(c, AlarmReceiver.class);
                    alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                    alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                    alarmIntent.putExtra("vibration",t.isVibration());
                    alarmIntent.putExtra("online",t.isOnline());
                    alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(i).intValue()));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(c, t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                    i++;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                            setCalendarAlarm(1, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
                }

            } else {

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, t.getHourAlarm());
                calendar.set(Calendar.MINUTE, t.getMinuteAlarm());

                int diff = (int) (Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis());
                if (diff > 0)
                    calendar.add(Calendar.DATE, 1);


                calendar.set(Calendar.SECOND, 0);


            /* Retrieve a PendingIntent that will perform a broadcast */
                Intent alarmIntent = new Intent(c, AlarmReceiver.class);
                alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(0).intValue());
                alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                alarmIntent.putExtra("vibration",t.isVibration());
                alarmIntent.putExtra("online",t.isOnline());
                alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(0).intValue()));


                PendingIntent pendingIntent = PendingIntent.getBroadcast(c, t.getIdAlarm().get(0).intValue(), alarmIntent, 0);


                manager.set(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), pendingIntent);


            }
        }
    }


    private static Calendar setCalendarAlarm(int myAlarmDayOfTheWeek, int myAlarmHour, int myAlarmMinute) {
        Calendar timestamp = Calendar.getInstance();


//Check whether the day of the week was earlier in the week:
        if( myAlarmDayOfTheWeek > timestamp.get(Calendar.DAY_OF_WEEK) ) {
            //Set the day of the AlarmManager:
            timestamp.add(Calendar.DAY_OF_YEAR, (myAlarmDayOfTheWeek - timestamp.get(Calendar.DAY_OF_WEEK)));
        }
        else {
            if( myAlarmDayOfTheWeek < timestamp.get(Calendar.DAY_OF_WEEK) ) {
                //Set the day of the AlarmManager:
                timestamp.add(Calendar.DAY_OF_YEAR, (7 - (timestamp.get(Calendar.DAY_OF_WEEK) - myAlarmDayOfTheWeek)));
            }
            else {  // myAlarmDayOfTheWeek == time.get(Calendar.DAY_OF_WEEK)
                //Check whether the time has already gone:
                if ( (myAlarmHour < timestamp.get(Calendar.HOUR_OF_DAY)) || ((myAlarmHour == timestamp.get(Calendar.HOUR_OF_DAY)) && (myAlarmMinute < timestamp.get(Calendar.MINUTE))) ) {
                    //Set the day of the AlarmManager:
                    timestamp.add(Calendar.DAY_OF_YEAR, 7);
                }
            }
        }

//Set the time of the AlarmManager:
        timestamp.set(Calendar.HOUR_OF_DAY, myAlarmHour);
        timestamp.set(Calendar.MINUTE, myAlarmMinute);
        timestamp.set(Calendar.SECOND, 0);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.i("ALARM" + myAlarmDayOfTheWeek, format1.format(timestamp.getTime()));
        return timestamp;
    }


}
