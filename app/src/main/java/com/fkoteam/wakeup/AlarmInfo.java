package com.fkoteam.wakeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.Serializable;

public class AlarmInfo implements Serializable {

    String txtTimeAlarm;
    int hourAlarm;
    int minuteAlarm;
    boolean active;
    int idAlarm;


    public AlarmInfo(String txtTimeAlarm, int hourAlarm, int minuteAlarm) {
        this.txtTimeAlarm = txtTimeAlarm;
        this.hourAlarm = hourAlarm;
        this.minuteAlarm = minuteAlarm;
        this.active=true;
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

    public int getIdAlarm() {
        return idAlarm;
    }

    public void setIdAlarm(int idAlarm) {
        this.idAlarm = idAlarm;
    }
}
