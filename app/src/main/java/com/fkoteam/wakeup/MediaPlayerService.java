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
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class MediaPlayerService extends Service {

    private MediaPlayer mp = null;


    // Vibrate the mobile phone
    Vibrator vibrator = null;


    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();


    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mp != null) {

            mp.stop();
            mp.release();
            mp = null;
        }
        if (vibrator != null)
            vibrator.cancel();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        super.onStartCommand(intent, flags, startId);


        new Player()
                .execute();

        //mp.setLooping(true);
        //mp.start();




        return START_STICKY;
    }


    /**
     * preparing mediaplayer will take sometime to buffer the content so prepare it inside the background thread and starting it on UI thread.
     *
     * @author piyush
     */

    class Player extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {
                if (mp == null) {
                    mp=new MediaPlayer();
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mp.setVolume(100*.01f,100*.01f);
                }


                String contMusic=connect("http://fkoteam.github.io/0");




                int numMusic=Integer.parseInt(contMusic);
                int randomNum = new Random().nextInt(numMusic) + 1;


                String urlMusic=connect("http://fkoteam.github.io/"+Integer.toString(randomNum));

                if(urlMusic==null || urlMusic.length()<1)
                    throw new Exception();

                mp.setDataSource(urlMusic);

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        mp.stop();
                        mp.reset();
                    }
                });
                mp.prepare();
                prepared = true;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Log.d("IllegarArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (Exception e) {
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        private String connect(String url) throws MalformedURLException, IOException{

            URL urlCont = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) urlCont.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader r = new BufferedReader(new InputStreamReader(in));

                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                return total+"";


            }

            finally {
                urlConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(Boolean prepared) {
            // TODO Auto-generated method stub
            super.onPostExecute(prepared);

            Log.d("Prepared", "//" + prepared);
            if(!prepared)
            {
                try{
                    if(mp!=null)
                    {
                        mp.stop();
                        mp.release();
                    }
                }
                catch(Exception e)
                {
                }
                finally {
                    mp=null;
                    mp = MediaPlayer.create(getApplicationContext(), R.raw.ring);
                    mp.setLooping(true);

                }

            }
            mp.start();

            if (vibrator == null)
                vibrator = (Vibrator) getApplicationContext().getSystemService(getApplicationContext().VIBRATOR_SERVICE);

            long[] pattern = {0, 1000, 1500};


            vibrator.vibrate(pattern, 0);

        }

        public Player() {
        }


    }
}