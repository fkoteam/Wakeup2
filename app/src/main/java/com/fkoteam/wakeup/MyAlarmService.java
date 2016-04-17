package com.fkoteam.wakeup;

/**
 * Created by SweetHome2 on 08/04/2016.
 */

    import android.app.Notification;
    import android.app.NotificationManager;
    import android.app.PendingIntent;
    import android.app.Service;
    import android.content.Intent;
    import android.media.MediaPlayer;
    import android.os.IBinder;
    import android.os.Vibrator;
    import android.support.v4.app.NotificationCompat;
    import android.widget.Toast;

    import java.util.Date;


public class MyAlarmService extends Service
    {
//todo
        private NotificationManager mManager;

        @Override
        public IBinder onBind(Intent arg0)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onCreate()
        {
            // TODO Auto-generated method stub
            super.onCreate();
        }

        @SuppressWarnings("static-access")
        @Override
        public void onStart(Intent intent, int startId)
        {
            super.onStart(intent, startId);


            mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
            Intent intent1 = new Intent(this.getApplicationContext(),MainActivity.class);

            Notification notification = new Notification(R.drawable.ic_launcher,"This is a test message!", System.currentTimeMillis());
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext())
                    .setContentIntent(pendingNotificationIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("AlarmManagerDemo")
                    .setContentText("This is a test message!");
            notification = builder.build();


            mManager.notify(0, notification);



        }

        @Override
        public void onDestroy()
        {
            // TODO Auto-generated method stub
            super.onDestroy();
        }

    }
