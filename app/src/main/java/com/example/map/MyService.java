package com.example.map;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Thread serviceThread = new Thread(new Runnable() {

            public void run() {

                while (true) {
                    try {

                        Thread.sleep(60000);

                        new Synchroniz.GetDataFromServer(getApplicationContext()).execute(Synchroniz.HttpURLGet);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        serviceThread.start();

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}