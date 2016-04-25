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
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public MainActivity() {
    }


    boolean click = true;
    SharedPreferences sharedPref;
    ArrayList<AlarmInfo> currentAlarms = new ArrayList<AlarmInfo>();
    MyAdapter listAdapter = new MyAdapter();
    FloatingActionButton addButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addButton = (FloatingActionButton) findViewById(R.id.fab);


        initAlarmPrefs();
        //Utils.initAlarmPrefs(sharedPref,getApplicationContext(),getString(R.string.preference_alarms_file),currentAlarms);


        Bundle b = getIntent().getExtras();
        if (b != null) {
            int snooze = b.getInt("snooze", -2);
            int idAlarm = b.getInt("tryDisableAlarm");
            AlarmInfo ai = null;
            if (idAlarm > 0) {
                ai = getAlarmById(idAlarm);

            }

            if (ai != null) {

                unSnoozeAlarm(ai, -1);

                if (snooze > 0)
                    snoozeAlarm(snooze, ai);
                else
                    tryDisableAlarm(ai, idAlarm);


            }

        }


        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                doPopupWindow(null, -1);
            }

        });


        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        Spannable text = new SpannableString(getString(R.string.title));
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ListView list = (ListView) this.findViewById(R.id.listView1);
        TextView emptyText = (TextView) findViewById(R.id.empty_list_item);
        list.setEmptyView(emptyText);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                            @Override
                                            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                                                // Alternative: show a dialog
                                                (new AlertDialog.Builder(MainActivity.this))
                                                        .setMessage(getString(R.string.confirm_delete) + " " + currentAlarms.get(position).getTxtTimeAlarm() + "H?")
                                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                deleteAlarm(position, true, true);
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

    private void doPopupWindow(final AlarmInfo ai, final int position) {
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


            if (ai != null) {
                TextView txtAddModifyAlarm = (TextView) popupView.findViewById(R.id.txtAddModifyAlarm);
                txtAddModifyAlarm.setText(getString(R.string.modifyAlarm));

            }


            final TextView txtTime = (TextView) popupView.findViewById(R.id.txtTime);
            if (ai != null)
                txtTime.setText(ai.getTxtTimeAlarm());
            txtTime.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    popUpHora(popupView, popupWindow, txtTime.getText() + "");
                }
            });

            final Button btnCancelPopup = (Button) popupView.findViewById(R.id.dismiss);
            btnCancelPopup.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    popupWindow.dismiss();
                    click = true;
                }
            });

            if (ai != null && ai.anyRepeat()) {
                if (ai.repeatMon) {
                    CheckBox chk = (CheckBox) popupView.findViewById(R.id.checkMon);
                    chk.setChecked(true);
                }
                if (ai.repeatTue) {
                    CheckBox chk = (CheckBox) popupView.findViewById(R.id.checkTue);
                    chk.setChecked(true);
                }
                if (ai.repeatWed) {
                    CheckBox chk = (CheckBox) popupView.findViewById(R.id.checkWed);
                    chk.setChecked(true);
                }
                if (ai.repeatThu) {
                    CheckBox chk = (CheckBox) popupView.findViewById(R.id.checkThu);
                    chk.setChecked(true);
                }
                if (ai.repeatFri) {
                    CheckBox chk = (CheckBox) popupView.findViewById(R.id.checkFri);
                    chk.setChecked(true);
                }
                if (ai.repeatSat) {
                    CheckBox chk = (CheckBox) popupView.findViewById(R.id.checkSat);
                    chk.setChecked(true);
                }
                if (ai.repeatSun) {
                    CheckBox chk = (CheckBox) popupView.findViewById(R.id.checkSun);
                    chk.setChecked(true);
                }
            }

            if (ai != null && ai.isOnline()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    Switch internet = ((Switch) popupView.findViewById(R.id.switchInternet));
                    internet.setChecked(true);
                } else {
                    CheckBox chk = (CheckBox) popupView.findViewById(R.id.switchInternet);
                    chk.setChecked(true);
                }
            }


            if (ai != null && ai.isVibration()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    Switch vibra = ((Switch) popupView.findViewById(R.id.switchVibrate));
                    vibra.setChecked(true);
                } else {
                    CheckBox chk = (CheckBox) popupView.findViewById(R.id.switchVibrate);
                    chk.setChecked(true);
                }
            }


            SeekBar seekTypeAlarm = (SeekBar) popupView.findViewById(R.id.seekBar);
            if (ai != null && ai.getTypeAlarm() == 1) {
                seekTypeAlarm.setProgress(1);
                TextView txtClassical = (TextView) popupView.findViewById(R.id.txtClassical);
                TextView txtNatural = (TextView) popupView.findViewById(R.id.txtNatural);
                txtClassical.setTextColor(Color.parseColor("#909090"));
                txtNatural.setTextColor(Color.parseColor("#000000"));

            }
            seekTypeAlarm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


                @Override
                public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                    int progress = progresValue;
                    if (progress == 0) {
                        TextView txtClassical = (TextView) popupView.findViewById(R.id.txtClassical);
                        TextView txtNatural = (TextView) popupView.findViewById(R.id.txtNatural);
                        txtClassical.setTextColor(Color.parseColor("#000000"));
                        txtNatural.setTextColor(Color.parseColor("#909090"));

                    } else {
                        TextView txtClassical = (TextView) popupView.findViewById(R.id.txtClassical);
                        TextView txtNatural = (TextView) popupView.findViewById(R.id.txtNatural);
                        txtClassical.setTextColor(Color.parseColor("#909090"));
                        txtNatural.setTextColor(Color.parseColor("#000000"));
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {


                }
            });
            final Button btnAcceptPopup = (Button) popupView.findViewById(R.id.accept);
            btnAcceptPopup.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    int hourInt = -1;
                    int minuteInt = -1;

                    try {
                        String[] lines = (txtTime.getText() + "").split(":");
                        hourInt = Integer.parseInt(lines[0]);
                        minuteInt = Integer.parseInt(lines[1]);

                        SeekBar seekBar = (SeekBar) popupView.findViewById(R.id.seekBar);

                        boolean internet;
                        boolean vibration;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            internet = ((Switch) popupView.findViewById(R.id.switchInternet)).isChecked();
                            vibration = ((Switch) popupView.findViewById(R.id.switchVibrate)).isChecked();
                        } else {
                            internet = ((CheckBox) popupView.findViewById(R.id.switchInternet)).isChecked();
                            vibration = ((CheckBox) popupView.findViewById(R.id.switchVibrate)).isChecked();
                        }
                        if (ai == null) {
                            AlarmInfo ai_new = new AlarmInfo(txtTime.getText() + "", hourInt, minuteInt, ((CheckBox) popupView.findViewById(R.id.checkMon)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkTue)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkWed)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkThu)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkFri)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkSat)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkSun)).isChecked(), seekBar.getProgress(), internet, vibration);
                            //el id de la alarma es la fecha actual en milis
                            ai_new.setIdAlarm();
                            addAlarm(ai_new);
                        } else {
                            deleteAlarm(position, false, false);

                            ai.modifyValues(txtTime.getText() + "", hourInt, minuteInt, ((CheckBox) popupView.findViewById(R.id.checkMon)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkTue)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkWed)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkThu)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkFri)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkSat)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkSun)).isChecked(), seekBar.getProgress(), internet, vibration);
                            currentAlarms.set(position, ai);
                            saveData();
                            listAdapter.notifyDataSetChanged();
                            Utils.startAlarm(ai, getApplicationContext(),true);


                        }
                        popupWindow.dismiss();
                        click = true;
                    } catch (Exception e) {

                        Toast.makeText(getApplicationContext(), getString(R.string.wrong_hour), Toast.LENGTH_SHORT).show();

                    }


                }
            });


            if (click) {
                //popupWindow.showAsDropDown(addButton, 50, 30);
                popupWindow.showAtLocation(addButton, Gravity.CENTER, 0, 0);

                if (ai == null)
                    popUpHora(popupView, popupWindow, null);


                click = false;
            } else {
                popupWindow.dismiss();
                click = true;
            }
        }
    }

    private void stopAlarm(int idAlarm) {


        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);

        alarmIntent.setAction(String.valueOf(idAlarm));


        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, idAlarm, alarmIntent, 0);


        manager.cancel(pendingIntent);
        pendingIntent.cancel();


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

            final FloatingActionButton addButton =
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

    private void unSnoozeAlarm(AlarmInfo ai, int position) {
        if (ai.getSnoozingId() != null)
            stopAlarm(ai.getSnoozingId().intValue());
        int pos = 0;
        if (position > -1)
            pos = position;
        else
            pos = ai.getPosition();
        AlarmInfo ai2 = currentAlarms.get(pos);
        ai2.setSnoozed(0);
        ai2.setSnoozingTime(null);
        ai2.setSnoozingId(null);
        currentAlarms.set(pos, ai2);
        saveData();

    }


    private AlarmInfo getAlarmById(int id) {

        int pos = 0;
        for (AlarmInfo ai : currentAlarms) {
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


    private void tryDisableAlarm(AlarmInfo ai, int idAlarm) {


        if (!ai.anyRepeat()) {

            //si no se repite, se puede eliminar
            stopAlarm(idAlarm);


            AlarmInfo ai2 = currentAlarms.get(ai.getPosition());
            ai2.setActive(false);
            currentAlarms.set(ai.getPosition(), ai2);
            deleteAlarm(ai.getPosition(), false, false);


            saveData();
            listAdapter.notifyDataSetChanged();


        }


    }

    private void popUpHora(final View popupView, final PopupWindow popupWindow, String textViewText) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        //si v es nulo, venimos del click en el campo de texto. intentamos recuperar la hora
        if (textViewText != null && textViewText.length() > 0) {
            try {
                String[] lines = textViewText.split(":");
                hour = Integer.parseInt(lines[0]);
                minute = Integer.parseInt(lines[1]);
            } catch (Exception e) {

            }
        }
        TimePickerDialog timeDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                TextView txtTime = (TextView) popupView.findViewById(R.id.txtTime);
                String hourTxt = "" + selectedHour;
                String minuteTxt = "" + selectedMinute;
                if (hourTxt != null && hourTxt.length() == 1)
                    hourTxt = "0" + hourTxt;
                if (minuteTxt != null && minuteTxt.length() == 1)
                    minuteTxt = "0" + minuteTxt;

                txtTime.setText(hourTxt + ":" + minuteTxt);


            }
        }, hour, minute, true);//Yes 24 hour time
        timeDialog.show();
        if (textViewText == null || textViewText.length() == 0) {
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        doPopupWindow(currentAlarms.get(position), position);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row, null);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);

            if (currentAlarms.get(position).isActive()) {
                imageView.setImageResource(R.drawable.ic_action_alarm);
                convertView.setBackgroundColor(Color.WHITE);
                ((TextView) convertView.findViewById(R.id.textViewHora)).setTextColor(Color.BLACK);
            } else {
                imageView.setImageResource(R.drawable.ic_action_alarm_off);
                convertView.setBackgroundColor(Color.LTGRAY);
                ((TextView) convertView.findViewById(R.id.textViewHora)).setTextColor(Color.GRAY);
            }

            final View finalConvertView = convertView;
            imageView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    ImageView imageView2 = (ImageView) v.findViewById(R.id.imageView1);
                    if (currentAlarms.get(position).isActive()) {

                        imageView2.setImageResource(R.drawable.ic_action_alarm_off);
                        finalConvertView.setBackgroundColor(Color.LTGRAY);
                        ((TextView) finalConvertView.findViewById(R.id.textViewHora)).setTextColor(Color.GRAY);
                        currentAlarms.get(position).setActive(false);
                        saveData();


                        deleteAlarm(position, false, true);
                        Toast.makeText(getApplicationContext(), getString(R.string.alarm_disabled), Toast.LENGTH_SHORT).show();


                    } else {
                        imageView2.setImageResource(R.drawable.ic_action_alarm);

                        finalConvertView.setBackgroundColor(Color.WHITE);
                        ((TextView) finalConvertView.findViewById(R.id.textViewHora)).setTextColor(Color.BLACK);
                        currentAlarms.get(position).setActive(true);

                        saveData();


                        Utils.startAlarm(currentAlarms.get(position), getApplicationContext(),false);

                        Toast.makeText(getApplicationContext(), getString(R.string.alarm_enabled), Toast.LENGTH_SHORT).show();

                    }


                }
            });

            TextView textview = (TextView) convertView.findViewById(R.id.textViewHora);
            textview.setText(currentAlarms.get(position).getTxtTimeAlarm());

            TextView textRepeat = (TextView) convertView.findViewById(R.id.textRepeat);
            String textRepeatTmp = "";
            if (currentAlarms.get(position).anyRepeat()) {
                if (currentAlarms.get(position).repeatMon) {
                    textRepeatTmp = getString(R.string.monday) + " ";
                }
                if (currentAlarms.get(position).repeatTue) {
                    textRepeatTmp += getString(R.string.tuesday) + " ";
                }
                if (currentAlarms.get(position).repeatWed) {
                    textRepeatTmp += getString(R.string.wednesday) + " ";
                }
                if (currentAlarms.get(position).repeatThu) {
                    textRepeatTmp += getString(R.string.thursday) + " ";
                }
                if (currentAlarms.get(position).repeatFri) {
                    textRepeatTmp += getString(R.string.friday) + " ";
                }
                if (currentAlarms.get(position).repeatSat) {
                    textRepeatTmp += getString(R.string.saturday) + " ";
                }
                if (currentAlarms.get(position).repeatSun) {
                    textRepeatTmp += getString(R.string.sunday);
                }
            } else {
                textRepeatTmp = getString(R.string.no_repeat);
            }

            if (currentAlarms.get(position).getSnoozed() > 0) {
                String s = "";
                if (currentAlarms.get(position).getSnoozed() > 1)
                    s = getString(R.string.plural);
                textRepeatTmp += "\n(" + getString(R.string.snoozed) + " " + currentAlarms.get(position).getSnoozed() + " " + getString(R.string.minute) + s + ")";
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

        Utils.startAlarm(t, this,false);
        currentAlarms.add(t);

        saveData();
        listAdapter.notifyDataSetChanged();


    }

    private void deleteAlarm(int position, boolean deleteFromList, boolean deleteSnooze) {

        if (deleteSnooze)
            unSnoozeAlarm(currentAlarms.get(position), position);

        for (Integer idAlarm : currentAlarms.get(position).getIdAlarm()) {
            stopAlarm(idAlarm.intValue());

        }


        if (deleteFromList) {
            currentAlarms.remove(position);

            saveData();
        }

        listAdapter.notifyDataSetChanged();


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

    private void initAlarmPrefs() {
        boolean firstTime = false;
        sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_alarms_file), Context.MODE_PRIVATE);
        if (null == currentAlarms) {
            currentAlarms = new ArrayList<AlarmInfo>();
            firstTime = true;
        }

        //      load tasks from preference

        try {
            currentAlarms = (ArrayList<AlarmInfo>) ObjectSerializer.deserialize(sharedPref.getString("AlarmasPrefs", ObjectSerializer.serialize(new ArrayList<AlarmInfo>())));
            int pos = 0;
            if (firstTime) {
                for (AlarmInfo ai : currentAlarms) {
                    if (ai.active) {
                        unSnoozeAlarm(ai, pos);
                        Utils.startAlarm(ai, this,false);
                    }
                    pos++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void snoozeAlarm(int snooze, AlarmInfo ai) {

        Calendar today = Calendar.getInstance();
        Calendar cal = (Calendar) today.clone();
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 0);
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + snooze);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        AlarmInfo ai2 = currentAlarms.get(ai.getPosition());
        ai2.setSnoozed(snooze);
        ai2.setSnoozingTime(cal);
        ai2.setSnoozingId();
        currentAlarms.set(ai.getPosition(), ai2);
        saveData();


        Utils.startAlarm(ai2, this,false);
        WakeLocker.release();


    }


}