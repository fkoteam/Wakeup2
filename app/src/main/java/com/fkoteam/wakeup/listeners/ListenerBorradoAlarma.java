package com.fkoteam.wakeup.listeners;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;

import com.fkoteam.wakeup.AlarmList;
import com.fkoteam.wakeup.MainActivity;
import com.fkoteam.wakeup.R;

/**
 * Created by SweetHome2 on 27/04/2016.
 */
public class ListenerBorradoAlarma implements AdapterView.OnItemLongClickListener, View.OnLongClickListener {

    private MainActivity.MyAdapter myAdapter;
    private MainActivity mainActivity;
    private int position;
    private AlarmList al = AlarmList.getInstance();


    public ListenerBorradoAlarma(MainActivity mainActivity,MainActivity.MyAdapter myAdapter) {
        this.mainActivity = mainActivity;
        this.myAdapter=myAdapter;

    }

    public ListenerBorradoAlarma(MainActivity mainActivity, int position,MainActivity.MyAdapter myAdapter) {
        this.mainActivity = mainActivity;
        this.position=position;
        this.myAdapter=myAdapter;

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        this.position=position;
        return mensajeBorrar();

    }

    @Override
    public boolean onLongClick(View v) {
        return mensajeBorrar();
    }


    private boolean mensajeBorrar()
    {

        // Alternative: show a dialog
        (new AlertDialog.Builder(this.mainActivity))
                .setMessage(mainActivity.getString(R.string.confirm_delete) + " " + al.getCurrentAlarms().get(position).getTxtTimeAlarm() + "H?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlarmList.deleteAlarm(position, true, true);
                        myAdapter.notifyDataSetChanged();


                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel: do something
                    }
                })
                .setTitle(mainActivity.getString(R.string.confirm_delete_title))
                .show();

        return true;
    }
}
