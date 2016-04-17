package com.fkoteam.wakeup;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlarmInfo implements Serializable {

    String txtTimeAlarm;
    int hourAlarm;
    int minuteAlarm;
    boolean active;
    //casi nunca se usa
    int position;
    int snoozed;

    //listado de alarmas. Pueden ser 7 si se repiten todos los dias
    List<Integer> idAlarm;
    List<PendingIntent> pendingIntents;

    //0- classical, 1-nature
    int typeAlarm;
    boolean repeatMon;
    boolean repeatTue;
    boolean repeatWed;
    boolean repeatThu;
    boolean repeatFri;
    boolean repeatSat;
    boolean repeatSun;


    public AlarmInfo(String txtTimeAlarm, int hourAlarm, int minuteAlarm,boolean repeatMon,
            boolean repeatTue,
            boolean repeatWed,
            boolean repeatThu,
            boolean repeatFri,
            boolean repeatSat,
            boolean repeatSun,int typeAlarm) {
        this.txtTimeAlarm = txtTimeAlarm;
        this.hourAlarm = hourAlarm;
        this.minuteAlarm = minuteAlarm;
        this.active=true;
        this.typeAlarm=typeAlarm;

        this.repeatMon=repeatMon;
                this.repeatTue=repeatTue;
                this.repeatWed=repeatWed;
        this.repeatThu=repeatThu;
                this.repeatFri=repeatFri;
                this.repeatSat=repeatSat;
                this.repeatSun=repeatSun;
    }

    public String getTxtTimeAlarm() {
        return txtTimeAlarm;
    }

    public void setTxtTimeAlarm(String txtTimeAlarm) {
        this.txtTimeAlarm = txtTimeAlarm;
    }

    public int getHourAlarm() {
        return hourAlarm;
    }

    public void setHourAlarm(int hourAlarm) {
        this.hourAlarm = hourAlarm;
    }

    public int getMinuteAlarm() {
        return minuteAlarm;
    }

    public void setMinuteAlarm(int minuteAlarm) {
        this.minuteAlarm = minuteAlarm;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Integer> getIdAlarm() {
        return idAlarm;
    }

    public void setIdAlarm(List<Integer> idAlarm) {
        this.idAlarm = idAlarm;
    }


    //calculamos los id's automaticamente
    public void setIdAlarm() {
        idAlarm=new ArrayList<Integer>();
        int currentTimeMillis=(int) System.currentTimeMillis( ) % Integer.MAX_VALUE;
        //no se repite: solo un id. se repite: un id para cada dia
        if(!anyRepeat())
            idAlarm.add(currentTimeMillis);
        else {
            if(repeatMon)
                idAlarm.add(++currentTimeMillis);
            if(repeatTue)
                idAlarm.add(++currentTimeMillis);
            if(repeatWed)
                idAlarm.add(++currentTimeMillis);
            if(repeatThu)
                idAlarm.add(++currentTimeMillis);
            if(repeatFri)
                idAlarm.add(++currentTimeMillis);
            if(repeatSat)
                idAlarm.add(++currentTimeMillis);
            if(repeatSun)
                idAlarm.add(++currentTimeMillis);

        }



    }

    public boolean anyRepeat()
    {
        if(repeatMon || repeatTue || repeatWed || repeatThu || repeatFri || repeatSat || repeatSun)
            return true;
        return false;
    }


    public boolean findAlarm(int alarmToSearch)
    {
        if(idAlarm==null ||idAlarm.size()==0 )
            return false;
        else
        {
            for(Integer id : idAlarm)
            {
                if(id!=null && id.intValue()==alarmToSearch)
                    return true;
            }
            return false;
        }
    }


    public List<PendingIntent> getPendingIntents() {
        return pendingIntents;
    }

    public void setPendingIntents(List<PendingIntent> pendingIntents) {
        this.pendingIntents = pendingIntents;
    }

    public void addPendingIntent(PendingIntent pendingIntent) {
        if(pendingIntents==null)
        {
            pendingIntents=new ArrayList<PendingIntent>();
        }
        pendingIntents.add(pendingIntent);
    }

    public int getTypeAlarm() {
        return typeAlarm;
    }

    public void setTypeAlarm(int typeAlarm) {
        this.typeAlarm = typeAlarm;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSnoozed() {
        return snoozed;
    }

    public void setSnoozed(int snoozed) {
        this.snoozed = snoozed;
    }
}
