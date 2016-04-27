package com.fkoteam.wakeup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by SweetHome2 on 27/04/2016.
 */
public class MiListener implements AdapterView.OnItemLongClickListener {

    private MainActivity mainActivity;

    public MiListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


        // Alternative: show a dialog
        (new AlertDialog.Builder(this.mainActivity))
                .setMessage(mainActivity.getString(R.string.confirm_delete) + " " + mainActivity.currentAlarms.get(position).getTxtTimeAlarm() + "H?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mainActivity.deleteAlarm(position, true, true);
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
