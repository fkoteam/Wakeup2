package com.fkoteam.wakeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by agomezlo on 29/04/2016.
 */
public class AlarmList {
    private static AlarmList instance;
    private static Context mainActivity;
    private static Intent serviceIntentMediaPlayer;

    private AlarmList(){}
    public static synchronized AlarmList getInstance(){
        if(instance==null){
            instance=new AlarmList();
        }
        return instance;
    }

    private static ArrayList<AlarmInfo> currentAlarms = new ArrayList<AlarmInfo>();

    private static SharedPreferences sharedPref;

    public static ArrayList<AlarmInfo> getCurrentAlarms() {
        return currentAlarms;
    }

    public static void setCurrentAlarms(ArrayList<AlarmInfo> currentAlarms) {
        AlarmList.currentAlarms = currentAlarms;
    }

    public static SharedPreferences getSharedPref() {
        return sharedPref;
    }

    public static void setSharedPref(SharedPreferences sharedPref) {
        AlarmList.sharedPref = sharedPref;
    }

    public static AlarmInfo getAlarmById(int id) {

        int pos = 0;
        for (AlarmInfo ai : AlarmList.getCurrentAlarms()) {
            if (ai.getSnoozingId() != null && ai.getSnoozingId().intValue() == id) {
                ai.setPosition(pos);
                return ai;
            }
            if (ai.findAlarm(id)) {
                ai.setPosition(pos);

                return ai;
            }
            pos++;
        }
        return null;

    }


    public static void initAlarmPrefs(Context mainActivity) {
        boolean firstTime = false;
        if(getMainActivity()==null)
            setMainActivity( mainActivity);
        AlarmList.setSharedPref(mainActivity.getSharedPreferences(
                mainActivity.getString(R.string.preference_alarms_file), Context.MODE_PRIVATE));
        if (null == AlarmList.getCurrentAlarms() ||  AlarmList.getCurrentAlarms().size()==0) {
            AlarmList.setCurrentAlarms(new ArrayList<AlarmInfo>());
            firstTime = true;
        }

        stopMediaPlayer();
        //      load tasks from preference
        try {
            AlarmList.setCurrentAlarms((ArrayList<AlarmInfo>) ObjectSerializer.deserialize(AlarmList.getSharedPref().getString("AlarmasPrefs", ObjectSerializer.serialize(new ArrayList<AlarmInfo>()))));
            int pos = 0;
            if (firstTime) {
                for (AlarmInfo ai : AlarmList.getCurrentAlarms()) {
                    if (ai.active) {
                        unSnoozeAlarmInThePast(ai.getSnoozingId(), pos);
                        startAlarm(ai,  false);
                    }
                    pos++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopMediaPlayer() {
        if(serviceIntentMediaPlayer!=null) {
            mainActivity.stopService(serviceIntentMediaPlayer);
            serviceIntentMediaPlayer=null;
        }
    }


    public static void startAlarm(AlarmInfo t,  boolean isModify) {
        AlarmManager manager = (AlarmManager) mainActivity.getSystemService(Context.ALARM_SERVICE);


        if (t.getSnoozingTime() != null && t.getSnoozingId() != null) {
            Intent alarmIntent = new Intent(mainActivity, AlarmReceiver.class);
            alarmIntent.putExtra("idAlarm", t.getSnoozingId().intValue());
            alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
            alarmIntent.putExtra("vibration", t.isVibration());
            alarmIntent.putExtra("online", t.isOnline());
            alarmIntent.setAction(String.valueOf(t.getSnoozingId().intValue()));


            PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, t.getSnoozingId().intValue(), alarmIntent, 0);

            manager.set(AlarmManager.RTC_WAKEUP,
                    t.getSnoozingTime().getTimeInMillis(), pendingIntent);
        }

        //si se está modificando, además del snooze, hay que dar de alta las alarmas normales
        if (t.getSnoozingTime() == null || t.getSnoozingId() == null || isModify) {

            if (t.anyRepeat()) {
                int i = 0;
                if (t.repeatMon) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(mainActivity, AlarmReceiver.class);
                    alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                    alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                    alarmIntent.putExtra("vibration", t.isVibration());
                    alarmIntent.putExtra("online", t.isOnline());
                    alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(i).intValue()));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                    i++;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                            setCalendarAlarm(2, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);

                }
                if (t.repeatTue) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(mainActivity, AlarmReceiver.class);
                    alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                    alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                    alarmIntent.putExtra("vibration", t.isVibration());
                    alarmIntent.putExtra("online", t.isOnline());
                    alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(i).intValue()));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                    i++;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                            setCalendarAlarm(3, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
                }
                if (t.repeatWed) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(mainActivity, AlarmReceiver.class);
                    alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                    alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                    alarmIntent.putExtra("vibration", t.isVibration());
                    alarmIntent.putExtra("online", t.isOnline());
                    alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(i).intValue()));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                    i++;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                            setCalendarAlarm(4, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
                }
                if (t.repeatThu) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(mainActivity, AlarmReceiver.class);
                    alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                    alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                    alarmIntent.putExtra("vibration", t.isVibration());
                    alarmIntent.putExtra("online", t.isOnline());
                    alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(i).intValue()));


                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                    i++;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                            setCalendarAlarm(5, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
                }
                if (t.repeatFri) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(mainActivity, AlarmReceiver.class);
                    alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                    alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                    alarmIntent.putExtra("vibration", t.isVibration());
                    alarmIntent.putExtra("online", t.isOnline());
                    alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(i).intValue()));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                    i++;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                            setCalendarAlarm(6, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
                }
                if (t.repeatSat) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(mainActivity, AlarmReceiver.class);
                    alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                    alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                    alarmIntent.putExtra("vibration", t.isVibration());
                    alarmIntent.putExtra("online", t.isOnline());
                    alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(i).intValue()));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                    i++;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                            setCalendarAlarm(7, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
                }
                if (t.repeatSun) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(mainActivity, AlarmReceiver.class);
                    alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                    alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                    alarmIntent.putExtra("vibration", t.isVibration());
                    alarmIntent.putExtra("online", t.isOnline());
                    alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(i).intValue()));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                    i++;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                            setCalendarAlarm(1, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
                }

            } else {

                Calendar timestamp = Calendar.getInstance();


                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, t.getHourAlarm());
                calendar.set(Calendar.MINUTE, t.getMinuteAlarm());

                if ((t.getHourAlarm() < timestamp.get(Calendar.HOUR_OF_DAY)) || ((t.getHourAlarm() == timestamp.get(Calendar.HOUR_OF_DAY)) && (t.getMinuteAlarm() <= timestamp.get(Calendar.MINUTE))))
                    calendar.add(Calendar.DATE, 1);


                calendar.set(Calendar.SECOND, 0);


            /* Retrieve a PendingIntent that will perform a broadcast */
                Intent alarmIntent = new Intent(mainActivity, AlarmReceiver.class);
                alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(0).intValue());
                alarmIntent.putExtra("typeAlarm", t.getTypeAlarm());
                alarmIntent.putExtra("vibration", t.isVibration());
                alarmIntent.putExtra("online", t.isOnline());
                alarmIntent.setAction(String.valueOf(t.getIdAlarm().get(0).intValue()));


                PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, t.getIdAlarm().get(0).intValue(), alarmIntent, 0);


                manager.set(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), pendingIntent);


            }
        }
    }


    private static Calendar setCalendarAlarm(int myAlarmDayOfTheWeek, int myAlarmHour, int myAlarmMinute) {
        Calendar timestamp = Calendar.getInstance();


//Check whether the day of the week was earlier in the week:
        if (myAlarmDayOfTheWeek > timestamp.get(Calendar.DAY_OF_WEEK)) {
            //Set the day of the AlarmManager:
            timestamp.add(Calendar.DAY_OF_YEAR, (myAlarmDayOfTheWeek - timestamp.get(Calendar.DAY_OF_WEEK)));
        } else {
            if (myAlarmDayOfTheWeek < timestamp.get(Calendar.DAY_OF_WEEK)) {
                //Set the day of the AlarmManager:
                timestamp.add(Calendar.DAY_OF_YEAR, (7 - (timestamp.get(Calendar.DAY_OF_WEEK) - myAlarmDayOfTheWeek)));
            } else {  // myAlarmDayOfTheWeek == time.get(Calendar.DAY_OF_WEEK)
                //Check whether the time has already gone:
                if ((myAlarmHour < timestamp.get(Calendar.HOUR_OF_DAY)) || ((myAlarmHour == timestamp.get(Calendar.HOUR_OF_DAY)) && (myAlarmMinute <= timestamp.get(Calendar.MINUTE)))) {
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


    public static String connect(String url) throws MalformedURLException, IOException, SocketTimeoutException {

        URL urlCont = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) urlCont.openConnection();
        urlConnection.setConnectTimeout(5000); //set timeout to 5 seconds


        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader r = new BufferedReader(new InputStreamReader(in));

            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total + "";


        } finally {
            urlConnection.disconnect();
        }
    }


    public static int unSnoozeAlarm(Integer idAlarm, int position) {
        if(idAlarm!=null) {
            AlarmInfo ai = getAlarmById(idAlarm);
            if (ai.getSnoozingId() != null)
                stopAlarm(ai.getSnoozingId().intValue());
            int pos = 0;
            if (position > -1)
                pos = position;
            else
                pos = ai.getPosition();
            getCurrentAlarms().get(pos).setSnoozed(0);
            getCurrentAlarms().get(pos).setSnoozingTime(null);
            getCurrentAlarms().get(pos).setSnoozingId(null);
            saveData();
            return pos;
        }
        return -1;

    }



    public static void unSnoozeAlarmInThePast(Integer idAlarm, int position) {
        if(idAlarm!=null) {
            AlarmInfo ai = getAlarmById(idAlarm);
            boolean inThePast=false;
            if (ai.getSnoozingId() != null && ai.snoozingTime!=null) {
                Calendar today = Calendar.getInstance();
                if(today.after(ai.snoozingTime)) {
                    stopAlarm(ai.getSnoozingId().intValue());
                    inThePast=true;
                }
            }
            int pos = 0;
            if (position > -1)
                pos = position;
            else
                pos = ai.getPosition();
            if(inThePast) {
                getCurrentAlarms().get(pos).setSnoozed(0);
                getCurrentAlarms().get(pos).setSnoozingTime(null);
                getCurrentAlarms().get(pos).setSnoozingId(null);
                saveData();
            }
        }

    }

    public static void stopAlarm(int idAlarm) {


        AlarmManager manager = (AlarmManager) mainActivity.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(mainActivity, AlarmReceiver.class);

        alarmIntent.setAction(String.valueOf(idAlarm));


        PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, idAlarm, alarmIntent, 0);


        manager.cancel(pendingIntent);
        pendingIntent.cancel();


    }


    public static void saveData() {
        //save the task list to preference
        SharedPreferences.Editor editor = mainActivity.getSharedPreferences(
                mainActivity.getString(R.string.preference_alarms_file), Context.MODE_PRIVATE).edit();
        try {
            editor.putString("AlarmasPrefs", ObjectSerializer.serialize(getCurrentAlarms()));
        } catch (IOException e) {

        }
        editor.commit();
    }


    public static void tryDisableAlarm(int pos) {


        if (!getCurrentAlarms().get(pos).anyRepeat()) {

            //si no se repite, se puede eliminar
            //stopAlarm(idAlarm);


            getCurrentAlarms().get(pos).setActive(false);

            //deleteAlarm(ai.getPosition(), false, false);


            saveData();
            //todo ?
            //listAdapter.notifyDataSetChanged();


        }
    }


    public static void deleteAlarm(int position, boolean deleteFromList, boolean deleteSnooze) {

        if (deleteSnooze)
            unSnoozeAlarm(getCurrentAlarms().get(position).getSnoozingId(), position);

        for (Integer idAlarm : getCurrentAlarms().get(position).getIdAlarm()) {
            stopAlarm(idAlarm.intValue());

        }


        if (deleteFromList) {
            getCurrentAlarms().remove(position);

            saveData();
        }




    }



    public static void snoozeAlarm(int snooze, int pos) {

        Calendar today = Calendar.getInstance();
        Calendar cal = (Calendar) today.clone();
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 0);
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + snooze);


        getCurrentAlarms().get(pos).setSnoozed(snooze);
        getCurrentAlarms().get(pos).setSnoozingTime(cal);
        getCurrentAlarms().get(pos).setSnoozingId();
        saveData();


        startAlarm(getCurrentAlarms().get(pos), false);
        WakeLocker.release();


    }

    public static Context getMainActivity() {
        return mainActivity;
    }

    public static void setMainActivity(Context mainActivity) {
        AlarmList.mainActivity = mainActivity;
    }

    public static Intent getServiceIntentMediaPlayer() {
        return serviceIntentMediaPlayer;
    }

    public static void setServiceIntentMediaPlayer(Intent serviceIntentMediaPlayer) {
        AlarmList.serviceIntentMediaPlayer = serviceIntentMediaPlayer;
    }
}
