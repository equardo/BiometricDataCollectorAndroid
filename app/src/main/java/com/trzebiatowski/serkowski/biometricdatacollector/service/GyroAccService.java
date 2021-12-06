package com.trzebiatowski.serkowski.biometricdatacollector.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.trzebiatowski.serkowski.biometricdatacollector.R;
import com.trzebiatowski.serkowski.biometricdatacollector.ui.activity.TestActivity;
import com.trzebiatowski.serkowski.biometricdatacollector.listener.GyroAccListener;


public class GyroAccService extends Service {

    public static final int DATA_COLLECTION_NOTIFICATION_ID = 1;
    private Context context;

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private SensorManager sensorManager;
    private PowerManager.WakeLock wakeLock;
    private GyroAccListener listener;

    public GyroAccService(Context context) {
        this.context = context;
    }

    public GyroAccService(){
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String accPath = intent.getStringExtra("accPath");
        String gyroPath = intent.getStringExtra("gyroPath");

        context = getBaseContext();

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, TestActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                7, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Biometric Data Collector")
                .setContentText("Accelerometer and gyroscope data are being collected")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

        startForeground(DATA_COLLECTION_NOTIFICATION_ID, notification);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        listener = new GyroAccListener(context, accPath, gyroPath);

        sensorManager.registerListener(listener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(listener,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(listener);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
