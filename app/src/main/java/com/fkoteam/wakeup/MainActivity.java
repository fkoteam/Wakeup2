package com.fkoteam.wakeup;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fkoteam.wakeup.listeners.ListenerBorradoAlarma;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    AdView mAdView;
    private AlarmList al = AlarmList.getInstance();


    public MainActivity() {
    }


    boolean click = true;

    public MyAdapter listAdapter = new MyAdapter();
    FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            mAdView = (AdView) findViewById(R.id.adViewHome);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("1D2A83BF786B1B994E5672D7AE75A822").addTestDevice("DE266E0FACC2F0D7336E2678258DDD82").build();
            mAdView.loadAd(adRequest);
            addButton = (FloatingActionButton) findViewById(R.id.fab);


        

            al.initAlarmPrefs(this);
            checkUpdate runner = new checkUpdate();
            runner.execute();




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
            list.setOnItemLongClickListener(new ListenerBorradoAlarma(this,listAdapter));


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


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                Switch internet = ((Switch) popupView.findViewById(R.id.switchInternet));
                internet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                        if(arg1)
                            Toast.makeText(getApplicationContext(), getString(R.string.internet_warning) , Toast.LENGTH_LONG).show();
                    }
                } );
            } else {
                CheckBox internet = (CheckBox) popupView.findViewById(R.id.switchInternet);
                internet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                        if(arg1)
                            Toast.makeText(getApplicationContext(), getString(R.string.internet_warning) , Toast.LENGTH_LONG).show();
                    }
                } );                    }


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

                            Integer snoozingId=ai.getSnoozingId();
                            Calendar snoozingTime=ai.getSnoozingTime();
                            int snoozed =ai.getSnoozed();
                            AlarmList.deleteAlarm(position, false, true);
                            listAdapter.notifyDataSetChanged();



                            //ai.modifyValues(txtTime.getText() + "", hourInt, minuteInt, ((CheckBox) popupView.findViewById(R.id.checkMon)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkTue)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkWed)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkThu)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkFri)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkSat)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkSun)).isChecked(), seekBar.getProgress(), internet, vibration, snoozingId, snoozingTime, snoozed);
                            ai.modifyValues(txtTime.getText() + "", hourInt, minuteInt, ((CheckBox) popupView.findViewById(R.id.checkMon)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkTue)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkWed)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkThu)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkFri)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkSat)).isChecked(), ((CheckBox) popupView.findViewById(R.id.checkSun)).isChecked(), seekBar.getProgress(), internet, vibration, null, null, 0);
                            al.getCurrentAlarms().set(position, ai);
                            AlarmList.saveData();
                            listAdapter.notifyDataSetChanged();
                            AlarmList.startAlarm(ai,true);


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
            TextView version_id= (TextView) popupViewAbout.findViewById(R.id.version_id);
            try {
                version_id.setText(version_id.getText()+" "+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
            }
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
        doPopupWindow(al.getCurrentAlarms().get(position), position);
    }


    public class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return al.getCurrentAlarms().size();
        }

        @Override
        public Object getItem(int position) {
            return al.getCurrentAlarms().get(position);
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

            if (al.getCurrentAlarms().get(position).isActive()) {
                imageView.setImageResource(R.drawable.ic_action_alarm);
                convertView.setBackgroundColor(Color.WHITE);
                ((TextView) convertView.findViewById(R.id.textViewHora)).setTextColor(Color.BLACK);
            } else {
                imageView.setImageResource(R.drawable.ic_action_alarm_off);
                convertView.setBackgroundColor(Color.LTGRAY);
                ((TextView) convertView.findViewById(R.id.textViewHora)).setTextColor(Color.GRAY);
            }

            final View finalConvertView = convertView;
            imageView.setOnLongClickListener(new ListenerBorradoAlarma(MainActivity.this,position,listAdapter));
            imageView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    ImageView imageView2 = (ImageView) v.findViewById(R.id.imageView1);
                    if (al.getCurrentAlarms().get(position).isActive()) {

                        imageView2.setImageResource(R.drawable.ic_action_alarm_off);
                        finalConvertView.setBackgroundColor(Color.LTGRAY);
                        ((TextView) finalConvertView.findViewById(R.id.textViewHora)).setTextColor(Color.GRAY);
                        al.getCurrentAlarms().get(position).setActive(false);
                        AlarmList.saveData();


                        AlarmList.deleteAlarm(position, false, true);
                        listAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), getString(R.string.alarm_disabled), Toast.LENGTH_SHORT).show();


                    } else {
                        imageView2.setImageResource(R.drawable.ic_action_alarm);

                        finalConvertView.setBackgroundColor(Color.WHITE);
                        ((TextView) finalConvertView.findViewById(R.id.textViewHora)).setTextColor(Color.BLACK);
                        al.getCurrentAlarms().get(position).setActive(true);

                        AlarmList.saveData();


                        AlarmList.startAlarm(al.getCurrentAlarms().get(position), false);

                        Toast.makeText(getApplicationContext(), getString(R.string.alarm_enabled), Toast.LENGTH_SHORT).show();

                    }


                }
            });

            TextView textview = (TextView) convertView.findViewById(R.id.textViewHora);
            textview.setText(al.getCurrentAlarms().get(position).getTxtTimeAlarm());

            TextView textRepeat = (TextView) convertView.findViewById(R.id.textRepeat);
            String textRepeatTmp = "";
            if (al.getCurrentAlarms().get(position).anyRepeat()) {
                if (al.getCurrentAlarms().get(position).repeatMon) {
                    textRepeatTmp = getString(R.string.monday) + " ";
                }
                if (al.getCurrentAlarms().get(position).repeatTue) {
                    textRepeatTmp += getString(R.string.tuesday) + " ";
                }
                if (al.getCurrentAlarms().get(position).repeatWed) {
                    textRepeatTmp += getString(R.string.wednesday) + " ";
                }
                if (al.getCurrentAlarms().get(position).repeatThu) {
                    textRepeatTmp += getString(R.string.thursday) + " ";
                }
                if (al.getCurrentAlarms().get(position).repeatFri) {
                    textRepeatTmp += getString(R.string.friday) + " ";
                }
                if (al.getCurrentAlarms().get(position).repeatSat) {
                    textRepeatTmp += getString(R.string.saturday) + " ";
                }
                if (al.getCurrentAlarms().get(position).repeatSun) {
                    textRepeatTmp += getString(R.string.sunday);
                }
            } else {
                textRepeatTmp = getString(R.string.no_repeat);
            }

            if (al.getCurrentAlarms().get(position).getSnoozed() > 0) {
                String s = "";
                if (al.getCurrentAlarms().get(position).getSnoozed() > 1)
                    s = getString(R.string.plural);
                if(textRepeatTmp.length()>0)
                    textRepeatTmp += "\n";
                textRepeatTmp += "(" + getString(R.string.snoozed) + " " + al.getCurrentAlarms().get(position).getSnoozed() + " " + getString(R.string.minute) + s + ")";
            }

            textview.setText(al.getCurrentAlarms().get(position).getTxtTimeAlarm());
            if(textRepeatTmp.length()>0)
                textRepeat.setVisibility(View.VISIBLE) ;
            else
                textRepeat.setVisibility(View.GONE);
            textRepeat.setText(textRepeatTmp);

            return convertView;
        }

    }


    private void addAlarm(AlarmInfo t) {
        al.getCurrentAlarms();

        AlarmList.startAlarm(t, false);
        al.getCurrentAlarms().add(t);

        AlarmList.saveData();
        listAdapter.notifyDataSetChanged();


    }















private class checkUpdate extends AsyncTask<Void, Boolean, Boolean> {
boolean haveInternet=true;

    @Override
    protected Boolean doInBackground(Void... params) {



            try {
                SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");



                String updateDate = getSharedPreferences(
                        getString(R.string.preference_alarms_file), Context.MODE_PRIVATE).getString("updateDate", "0");

                if (updateDate == null || "0".equals(updateDate)) {
                    Calendar timestamp = Calendar.getInstance();
                    //actualizacion cada 15 dias
                    timestamp.add(Calendar.DAY_OF_YEAR, 15);
                    SharedPreferences.Editor editor = getSharedPreferences(
                            getString(R.string.preference_alarms_file), Context.MODE_PRIVATE).edit();
                    editor.putString("updateDate", format1.format(timestamp.getTime()));

                    editor.commit();

                }
                else
                {
                    try{
                        Calendar parsedDate = Calendar.getInstance();

                        parsedDate.setTime(format1.parse(updateDate));
                        int diff = (int) (Calendar.getInstance().getTimeInMillis() - parsedDate.getTimeInMillis());
                        if (diff > 0) {
                            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                            String last_version=AlarmList.connect(getString(R.string.host)+"v.txt");
                            if(!versionName.equals(last_version)) {
                                return true;
                            }
                        }


                    }catch(Exception e){//this generic but you can control another types of exception
                        haveInternet=false;
                    }
                }

            }catch(Exception e)
            {
                haveInternet=false;
            }
        return false;

    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Boolean result) {
        if(result)
        {

        // Alternative: show a dialog
        (new AlertDialog.Builder(MainActivity.this))
                .setMessage(getString(R.string.question_update))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW ,Uri.parse(getString(R.string.host)+"wakeup_latest.apk"));
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");

                        Calendar timestamp = Calendar.getInstance();
                        //actualizacion cada 15 dias
                        timestamp.add(Calendar.DAY_OF_YEAR, 15);
                        SharedPreferences.Editor editor = getSharedPreferences(
                                getString(R.string.preference_alarms_file), Context.MODE_PRIVATE).edit();
                        editor.putString("updateDate", format1.format(timestamp.getTime()));

                        editor.commit();
                    }
                })
                .setTitle(getString(R.string.confirm_update_title))
                .show();

    }
        else
        {
            //si tiene internet, retrasamos la comprobacion, sino no para volver a comprobarlo
            if(haveInternet) {
                SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");

                Calendar timestamp = Calendar.getInstance();
                //actualizacion cada 15 dias
                timestamp.add(Calendar.DAY_OF_YEAR, 15);
                SharedPreferences.Editor editor = getSharedPreferences(
                        getString(R.string.preference_alarms_file), Context.MODE_PRIVATE).edit();
                editor.putString("updateDate", format1.format(timestamp.getTime()));


                editor.commit();
            }
        }







}
}

    @Override
    protected void onPause() {
        if(mAdView!=null)
            mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlarmList.stopMediaPlayer();
        if(mAdView!=null)
            mAdView.resume();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {

        //do nothing
    }


}