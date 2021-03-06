package com.fkoteam.wakeup;

/**
 * Created by SweetHome2 on 08/04/2016.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class MediaPlayerService extends Service {

    private MediaPlayer mp = null;
    MyTaskParams myTaskParams;
    AudioManager mAudioManager;
    int userVolume;

    // Vibrate the mobile phone
    Vibrator vibrator = null;
    boolean ringing = false;

    private MyHandler myHandler;

    private final class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
                //use the unique startId so you don't stop the
                //service while processing other requests
                stopSelfResult(msg.arg1);

        }
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
//Create a new HandlerThread with a specified priority
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        //Start the handler thread so that our Handler queue will start processing messages
        thread.start();
        //Run the handler using the new HandlerThread
        myHandler = new MyHandler(thread.getLooper());

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mp != null) {

            mp.stop();
            mp.release();
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, userVolume, 0);
            mp = null;
        }
        if (vibrator != null)
            vibrator.cancel();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
        userVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        super.onStartCommand(intent, flags, startId);

        Message msg = myHandler.obtainMessage();
        msg.arg1 = startId;
        myHandler.sendMessage(msg);

        myTaskParams = new MyTaskParams(intent.getIntExtra("typeAlarm", 0), intent.getIntExtra("isConnected", 0), intent.getBooleanExtra("online", false), intent.getBooleanExtra("vibration", false));
        new Player()
                .execute();


        return START_NOT_STICKY;
    }



    private static class MyTaskParams {
        private int typeAlarm;
        private int isConnected;
        private boolean online;
        private boolean vibration;

        MyTaskParams(int typeAlarm, int isConnected, boolean online, boolean vibration) {
            this.typeAlarm = typeAlarm;
            this.isConnected = isConnected;
            this.online = online;
            this.vibration = vibration;
        }

        public int getTypeAlarm() {
            return typeAlarm;
        }


        public int getIsConnected() {
            return isConnected;
        }

        public boolean isOnline() {
            return online;
        }

        public boolean isVibration() {
            return vibration;
        }

    }

    /**
     * preparing mediaplayer will take sometime to buffer the content so prepare it inside the background thread and starting it on UI thread.
     *
     * @author piyush
     */

    class Player extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean prepared = false;
//solo vamos a internet si el movil está conectado y el usuario quiere
            if (myTaskParams.getIsConnected() == 1 && myTaskParams.isOnline()) {

                try {
                    if (mp == null) {
                        mp = new MediaPlayer();
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mp.setVolume(100 * .01f, 100 * .01f);
                    }


                    //para musica clasica iria a buscar 00
                    //para naturaleza, 01
                    String contMusic =AlarmList.connect(getString(R.string.host)+ "0"+ String.valueOf(myTaskParams.getTypeAlarm()));


                    int numMusic = Integer.parseInt(contMusic);
                    int randomNum = new Random().nextInt(numMusic) + 1;


                    String urlMusic = AlarmList.connect(getString(R.string.host) + String.valueOf(randomNum) + String.valueOf(myTaskParams.getTypeAlarm()));

                    if (urlMusic == null || urlMusic.length() < 1)
                        throw new Exception();

                    mp.setDataSource(urlMusic);

                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        public void onPrepared(MediaPlayer mp) {
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                            Handler someHandler = new Handler();
                            someHandler.post(new VolumeRunnable(someHandler));
                            mp.start();
                            if (myTaskParams.isVibration())
                                vibrate();
                        }
                    });


                    mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            alarmOffline();
                            return false;
                        }
                    });


                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            mp.stop();
                            mp.reset();
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, userVolume, 0);

                        }
                    });
                    mp.prepare();
                    mp.setLooping(true);
                    prepared = true;
                } catch (IllegalArgumentException e) {
                    Log.d("IllegarArgument", e.getMessage());
                    prepared = false;
                    e.printStackTrace();
                } catch (SecurityException e) {
                    prepared = false;
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    prepared = false;
                    e.printStackTrace();
                } catch (IOException e) {
                    prepared = false;
                    e.printStackTrace();
                } catch (Exception e) {
                    prepared = false;
                    e.printStackTrace();
                }
            }

            return prepared;

        }

        @Override
        protected void onPostExecute(Boolean prepared) {
            super.onPostExecute(prepared);

            Log.d("Prepared", "//" + prepared);
            if (!prepared) {
                alarmOffline();
            }


        }

        private void alarmOffline() {
            try {
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, userVolume, 0);

                }
            } catch (Exception e) {
            } finally {
                mp = null;

                //tenemos 2 musicas clasicas y 2 sonidos naturales. hacemos random
                int numMusic = 2;
                int randomNum = new Random().nextInt(numMusic) + 1;
                mp = MediaPlayer.create(getApplicationContext(), getResources().getIdentifier("a" + String.valueOf(randomNum) + String.valueOf(myTaskParams.getTypeAlarm()), "raw", getPackageName()));
                mp.setVolume(100 * .01f, 100 * .01f);

                mp.setLooping(true);


            }
            if (!ringing) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                Handler someHandler = new Handler();
                someHandler.post(new VolumeRunnable(someHandler));
                mp.start();

                if (myTaskParams.isVibration())
                    vibrate();
                ringing = true;
            }

        }

        private void vibrate() {
            if (vibrator == null)
                vibrator = (Vibrator) getApplicationContext().getSystemService(getApplicationContext().VIBRATOR_SERVICE);

            long[] pattern = {0, 1000, 1500, 0, 1000, 1500, 0, 1000, 1500, 0, 1000, 1500, 0, 1000, 1500};


            vibrator.vibrate(pattern, -1);
        }

        public Player() {
        }


    }


    public class VolumeRunnable implements Runnable {

        private Handler mHandlerThatWillIncreaseVolume;
        private final static int DELAY_UNTILL_NEXT_INCREASE = 3 * 1000;//5 seconds between each increment
        private final  int VOLUME_INCREASE=mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/10;

        VolumeRunnable(Handler handler) {
            this.mHandlerThatWillIncreaseVolume = handler;
        }

        @Override
        public void run() {
            int currentAlarmVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (currentAlarmVolume < mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) { //if we havent reached the max

                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentAlarmVolume+VOLUME_INCREASE, 0);
                mHandlerThatWillIncreaseVolume.postDelayed(this, DELAY_UNTILL_NEXT_INCREASE); //"recursively call this runnable again with some delay between each increment of the volume, untill the condition above is satisfied.
            }

        }
    }

}