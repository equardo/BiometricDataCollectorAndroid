package com.trzebiatowski.serkowski.biometricdatacollector;

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
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.trzebiatowski.serkowski.biometricdatacollector.activity.MainActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class GyroAccService extends Service {

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
    //@Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        context = getBaseContext();

        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Accelerometer and gyroscope data are being collected")
                .setContentText(input)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        listener = new GyroAccListener(context);

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
        wakeLock.release();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void writeToFile(String data, String filepath, boolean overwrite) {

        try {
            FileOutputStream fOut;

            if(overwrite) {
                fOut = context.openFileOutput(filepath, Context.MODE_PRIVATE);
            }
            else {
                fOut = context.openFileOutput(filepath, Context.MODE_APPEND);
            }

            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            osw.write(data);

            osw.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
