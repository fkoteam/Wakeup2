package com.fkoteam.wakeup;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public MainActivity() {
    }


    boolean click = true;
    SharedPreferences sharedPref;
    ArrayList<AlarmInfo> currentAlarms=new ArrayList<AlarmInfo>();
    MyAdapter listAdapter=new MyAdapter();




    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FloatingActionButton addButton=(FloatingActionButton)findViewById(R.id.fab);


        initAlarmPrefs();






        Bundle b = getIntent().getExtras();
        if(b!=null)
        {
            int snooze = b.getInt("snooze");
            int typeAlarm=0;
            typeAlarm=b.getInt("typeAlarm");
            int idAlarm = b.getInt("tryDisableAlarm");
            AlarmInfo ai=null;
            if(idAlarm>0)
            {
                ai=getAlarmById(idAlarm);

            }

            if(ai!=null) {
                if(snooze>0)
                    snoozeAlarm(snooze, typeAlarm, ai);
                else
                    unSnoozeAlarm(ai);


                tryDisableAlarm(ai);

            }

        }


        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {

                LayoutInflater layoutInflater
                        = (LayoutInflater) getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.popup, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);

                final TextView txtTime = (TextView) popupView.findViewById(R.id.txtTime);
                txtTime.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        popUpHora( popupView, popupWindow,txtTime.getText()+"");
                    }
                });

                final Button btnCancelPopup = (Button) popupView.findViewById(R.id.dismiss);
                btnCancelPopup.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        click = true;
                    }
                });

                final Button btnAcceptPopup = (Button) popupView.findViewById(R.id.accept);
                btnAcceptPopup.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        int hourInt=-1;
                        int minuteInt=-1;

                        try{
                            String[] lines = (txtTime.getText()+"").split(":");
                            hourInt=Integer.parseInt(lines[0]);
                            minuteInt=Integer.parseInt(lines[1]);

                            SeekBar seekBar = (SeekBar) popupView.findViewById(R.id.seekBar);

                            AlarmInfo ai = new AlarmInfo(txtTime.getText()+"", hourInt, minuteInt,((CheckBox)popupView.findViewById(R.id.checkMon)).isChecked(),((CheckBox)popupView.findViewById(R.id.checkTue)).isChecked(),((CheckBox)popupView.findViewById(R.id.checkWed)).isChecked(),((CheckBox)popupView.findViewById(R.id.checkThu)).isChecked(),((CheckBox)popupView.findViewById(R.id.checkFri)).isChecked(),((CheckBox)popupView.findViewById(R.id.checkSat)).isChecked(),((CheckBox)popupView.findViewById(R.id.checkSun)).isChecked(),seekBar.getProgress());
                            //el id de la alarma es la fecha actual en milis
                            ai.setIdAlarm();
                            addAlarm(ai);

                            popupWindow.dismiss();
                            click = true;
                        }
                        catch(Exception e)
                        {

                            Toast.makeText(getApplicationContext(), getString(R.string.wrong_hour), Toast.LENGTH_SHORT).show();

                        }


                    }
                });






                if (click) {
                    //popupWindow.showAsDropDown(addButton, 50, 30);
                    popupWindow.showAtLocation(addButton, Gravity.CENTER, 0, 0);

                    popUpHora( popupView, popupWindow,null);

                    /*TimePickerDialog timeDialog=new TimePickerDialog(MainActivity.this, t, c
                            .get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                            true);*/

                    click = false;
                } else {
                    popupWindow.dismiss();
                    click = true;
                }
            }

        });









        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle(getString(R.string.title));

        ListView list = (ListView) this.findViewById(R.id.listView1);
        TextView emptyText = (TextView)findViewById(R.id.empty_list_item);
        list.setEmptyView(emptyText);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                            @Override
                                            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                                                // Alternative: show a dialog
                                                (new AlertDialog.Builder(MainActivity.this))
                                                        .setMessage(getString(R.string.confirm_delete) +" "+ currentAlarms.get(position).getTxtTimeAlarm() + "H?")
                                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                deleteAlarm(position, true);
                                                            }
                                                        })
                                                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // Cancel: do something
                                                            }
                                                        })
                                                        .setTitle(getString(R.string.confirm_delete_title))
                                                        .show();

                                                return true;
                                            }
                                        }

        );




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            LayoutInflater layoutInflater
                    = (LayoutInflater) getBaseContext()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            final View popupViewAbout = layoutInflater.inflate(R.layout.popup_about, null);
            final PopupWindow popupWindowAbout = new PopupWindow(
                    popupViewAbout,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindowAbout.setFocusable(true);

            final FloatingActionButton  addButton=
                    (FloatingActionButton) MainActivity.this.findViewById(R.id.fab);



            popupWindowAbout.showAtLocation(addButton, Gravity.CENTER, 0, 0);
            final Button btnCancelPopupAbout = (Button) popupViewAbout.findViewById(R.id.closePopupAbout);
            btnCancelPopupAbout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    popupWindowAbout.dismiss();
                }
            });

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void unSnoozeAlarm(AlarmInfo ai) {
        AlarmInfo ai2=currentAlarms.get(ai.getPosition());
        ai2.setSnoozed(0);
        currentAlarms.set(ai.getPosition(), ai2);
        saveData();

    }


    private AlarmInfo getAlarmById(int id) {

        int pos=0;
        for(AlarmInfo ai:currentAlarms)
        {
            if(ai.findAlarm(id))
            {
                ai.setPosition(pos);

                return ai;
            }
            pos++;
        }
        return null;

    }



    private void tryDisableAlarm(AlarmInfo ai) {


                if(!ai.anyRepeat())
                {

                        AlarmInfo ai2=currentAlarms.get(ai.getPosition());
                    ai2.setActive(false);
                    currentAlarms.set(ai.getPosition(),ai2);
                    deleteAlarm(ai.getPosition(),false);


                        saveData();
                        listAdapter.notifyDataSetChanged();


                }



    }

    private void popUpHora(final View popupView,final PopupWindow popupWindow,String textViewText) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        //si v es nulo, venimos del click en el campo de texto. intentamos recuperar la hora
        if(textViewText!=null && textViewText.length()>0)
        {
            try {
                String[] lines = textViewText.split(":");
                hour=Integer.parseInt(lines[0]);
                minute=Integer.parseInt(lines[1]);
            } catch(Exception e)
            {

            }
        }
        TimePickerDialog timeDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                TextView txtTime = (TextView) popupView.findViewById(R.id.txtTime);
                String hourTxt=""+selectedHour;
                String minuteTxt=""+selectedMinute;
                if(hourTxt!=null && hourTxt.length()==1)
                    hourTxt="0"+hourTxt;
                if(minuteTxt!=null && minuteTxt.length()==1)
                    minuteTxt="0"+minuteTxt;

                txtTime.setText(hourTxt + ":" + minuteTxt);


            }
        }, hour, minute, true);//Yes 24 hour time
        timeDialog.show();
        if(textViewText==null || textViewText.length()==0) {
            timeDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            popupWindow.dismiss();
                            click = true;
                        }
                    });
        }


    }



    /*public void cancelAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }*/

    public void startAlarm(AlarmInfo t) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);






        if (t.anyRepeat()) {
            int i=0;
            if (t.repeatMon) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                alarmIntent.putExtra("typeAlarm",t.getTypeAlarm());
                alarmIntent.setAction(Long.toString(System.currentTimeMillis()));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                t.addPendingIntent(pendingIntent);
                i++;

                manager.setRepeating(AlarmManager.RTC_WAKEUP,
                        setCalendarAlarm(2,t.getHourAlarm() ,t.getMinuteAlarm() ).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);

            }
            if (t.repeatTue) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                alarmIntent.putExtra("typeAlarm",t.getTypeAlarm());

                alarmIntent.setAction(Long.toString(System.currentTimeMillis()));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                t.addPendingIntent(pendingIntent);
                i++;

                manager.setRepeating(AlarmManager.RTC_WAKEUP,
                        setCalendarAlarm(3, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);            }
            if (t.repeatWed) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                alarmIntent.putExtra("typeAlarm",t.getTypeAlarm());

                alarmIntent.setAction(Long.toString(System.currentTimeMillis()));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                t.addPendingIntent(pendingIntent);
                i++;

                manager.setRepeating(AlarmManager.RTC_WAKEUP,
                        setCalendarAlarm(4,t.getHourAlarm() ,t.getMinuteAlarm() ).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);            }
            if (t.repeatThu) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                alarmIntent.putExtra("typeAlarm",t.getTypeAlarm());

                alarmIntent.setAction(Long.toString(System.currentTimeMillis()));


                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                t.addPendingIntent(pendingIntent);
                i++;

                manager.setRepeating(AlarmManager.RTC_WAKEUP,
                        setCalendarAlarm(5, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);            }
            if (t.repeatFri) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                alarmIntent.putExtra("typeAlarm",t.getTypeAlarm());

                alarmIntent.setAction(Long.toString(System.currentTimeMillis()));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,t.getIdAlarm().get(i).intValue(), alarmIntent,0);//PendingIntent.FLAG_UPDATE_CURRENT);
                t.addPendingIntent(pendingIntent);
                i++;

                manager.setRepeating(AlarmManager.RTC_WAKEUP,
                        setCalendarAlarm(6, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);            }
            if (t.repeatSat) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                alarmIntent.putExtra("typeAlarm",t.getTypeAlarm());

                alarmIntent.setAction(Long.toString(System.currentTimeMillis()));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                t.addPendingIntent(pendingIntent);
                i++;

                manager.setRepeating(AlarmManager.RTC_WAKEUP,
                        setCalendarAlarm(7, t.getHourAlarm(), t.getMinuteAlarm()).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);            }
            if (t.repeatSun) {
                /* Retrieve a PendingIntent that will perform a broadcast */
                Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(i).intValue());
                alarmIntent.putExtra("typeAlarm",t.getTypeAlarm());

                alarmIntent.setAction(Long.toString(System.currentTimeMillis()));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,t.getIdAlarm().get(i).intValue(), alarmIntent, 0);//PendingIntent.FLAG_UPDATE_CURRENT);
                t.addPendingIntent(pendingIntent);
                i++;

                manager.setRepeating(AlarmManager.RTC_WAKEUP,
                        setCalendarAlarm(1,t.getHourAlarm() ,t.getMinuteAlarm() ).getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);            }

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
            Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            alarmIntent.putExtra("idAlarm", t.getIdAlarm().get(0).intValue());
            alarmIntent.putExtra("typeAlarm",t.getTypeAlarm());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,t.getIdAlarm().get(0).intValue(), alarmIntent, 0);
            t.addPendingIntent(pendingIntent);


            manager.set(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pendingIntent);


        }
    }

    private Calendar setCalendarAlarm(int myAlarmDayOfTheWeek,int myAlarmHour,int myAlarmMinute) {
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
        Log.i("ALARM"+myAlarmDayOfTheWeek,format1.format(timestamp.getTime()));
        return timestamp;
    }


    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        if(currentAlarms.get(position).isActive()) {
            view.setBackgroundColor(Color.LTGRAY);
            ((TextView) view.findViewById(R.id.textViewHora)).setTextColor(Color.GRAY);
            currentAlarms.get(position).setActive(false);
            saveData();


            deleteAlarm(position,false);








            Toast.makeText(this, getString(R.string.alarm_disabled), Toast.LENGTH_SHORT).show();



        }
        else
        {
            view.setBackgroundColor(Color.WHITE);
            ((TextView) view.findViewById(R.id.textViewHora)).setTextColor(Color.BLACK);
            currentAlarms.get(position).setActive(true);

            saveData();


            startAlarm(currentAlarms.get(position));

            Toast.makeText(this, getString(R.string.alarm_enabled), Toast.LENGTH_SHORT).show();

        }

    }






    class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return currentAlarms.size();
        }

        @Override
        public Object getItem(int position) {
            return currentAlarms.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row, null);
            }

            if (currentAlarms.get(position).isActive()) {
                convertView.setBackgroundColor(Color.WHITE);
                ((TextView) convertView.findViewById(R.id.textViewHora)).setTextColor(Color.BLACK);
            }
            else
            {
                    convertView.setBackgroundColor(Color.LTGRAY);
                    ((TextView) convertView.findViewById(R.id.textViewHora)).setTextColor(Color.GRAY);
                }



            /*ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(240, 240));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(8, 8, 8, 1);
            imageView.setImageResource(icones[position]);*/

            TextView textview = (TextView) convertView.findViewById(R.id.textViewHora);
            textview.setText(currentAlarms.get(position).getTxtTimeAlarm());

            TextView textRepeat = (TextView) convertView.findViewById(R.id.textRepeat);
            String textRepeatTmp="";
            if(currentAlarms.get(position).anyRepeat())
            {
                if(currentAlarms.get(position).repeatMon)
                {
                    textRepeatTmp=getString(R.string.monday)+" ";
                }
                if(currentAlarms.get(position).repeatTue)
                {
                    textRepeatTmp+=getString(R.string.tuesday)+" ";
                }
                if(currentAlarms.get(position).repeatWed)
                {
                    textRepeatTmp+=getString(R.string.wednesday)+" ";
                }
                if(currentAlarms.get(position).repeatThu)
                {
                    textRepeatTmp+=getString(R.string.thursday)+" ";
                }
                if(currentAlarms.get(position).repeatFri)
                {
                    textRepeatTmp+=getString(R.string.friday)+" ";
                }
                if(currentAlarms.get(position).repeatSat)
                {
                    textRepeatTmp+=getString(R.string.saturday)+" ";
                }
                if(currentAlarms.get(position).repeatSun)
                {
                    textRepeatTmp+=getString(R.string.sunday);
                }
            }
            else
            {
                textRepeatTmp=getString(R.string.no_repeat);
            }

            if(currentAlarms.get(position).getSnoozed()>0) {
                String s="";
                if(currentAlarms.get(position).getSnoozed()>1)
                    s=getString(R.string.plural);
                textRepeatTmp += "\n("+getString(R.string.snoozed)+" " + currentAlarms.get(position).getSnoozed() + " "+getString(R.string.minute)+s+")";
            }

            textview.setText(currentAlarms.get(position).getTxtTimeAlarm());
            textRepeat.setText(textRepeatTmp);

            return convertView;
        }

    }



    private void addAlarm(AlarmInfo t) {
        if (null == currentAlarms) {
            currentAlarms = new ArrayList<AlarmInfo>();
        }
        currentAlarms.add(t);

        saveData();
        startAlarm(t);
        listAdapter.notifyDataSetChanged();


    }

    private void deleteAlarm(int position,boolean deleteFromList) {



        if(currentAlarms.get(position).getPendingIntents()!=null) {
            for (PendingIntent pendingIntent : currentAlarms.get(position).getPendingIntents()) {

                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                if(pendingIntent!=null) {

                    manager.cancel(pendingIntent);
                    pendingIntent.cancel();
                }
            }
        }


        if(deleteFromList) {
            currentAlarms.remove(position);

            saveData();
            listAdapter.notifyDataSetChanged();
        }



    }

    private void saveData() {
        //save the task list to preference
        SharedPreferences.Editor editor = sharedPref.edit();
        try {
            editor.putString("AlarmasPrefs", ObjectSerializer.serialize(currentAlarms));
        } catch (IOException e) {

        }
        editor.commit();
    }

    private void initAlarmPrefs()
    {
        sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_alarms_file), Context.MODE_PRIVATE);
        if (null == currentAlarms) {
            currentAlarms = new ArrayList<AlarmInfo>();
        }

        //      load tasks from preference

        try {
            currentAlarms = (ArrayList<AlarmInfo>) ObjectSerializer.deserialize(sharedPref.getString("AlarmasPrefs", ObjectSerializer.serialize(new ArrayList<AlarmInfo>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void snoozeAlarm(int snooze, int typeAlarm, AlarmInfo ai)
    {
        AlarmInfo ai2=currentAlarms.get(ai.getPosition());
        ai2.setSnoozed(snooze);
        currentAlarms.set(ai.getPosition(), ai2);
        saveData();


        Calendar today = Calendar.getInstance();
        Calendar cal = (Calendar) today.clone();


        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY)+ 0);
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + snooze);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat hour = new SimpleDateFormat(
                "HH");

        SimpleDateFormat minute = new SimpleDateFormat(
                "mm");

        String hourTxt = hour.format(cal.getTime());
        String minuteTxt = minute.format(cal.getTime());

        //7 falses porque el snooze no se repite según día de la semana
        AlarmInfo t=new AlarmInfo(hourTxt+":"+minuteTxt, Integer.parseInt(hourTxt), Integer.parseInt(minuteTxt),false,false,false,false,false,false,false,typeAlarm);
        t.setIdAlarm(currentAlarms.get(ai.getPosition()).getIdAlarm());
        startAlarm(t);
        WakeLocker.release();


    }


}