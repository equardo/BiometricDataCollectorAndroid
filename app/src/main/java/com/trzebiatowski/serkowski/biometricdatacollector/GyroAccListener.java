package com.trzebiatowski.serkowski.biometricdatacollector;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;

public class GyroAccListener implements SensorEventListener {

    private long lastAccUpdate;
    private long lastGyroUpdate;
    private Context context;
    private final String accPath = "acc_data.txt";
    private final String gyroPath = "gyro_data.txt";

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private SensorManager sensorManager;
    private PowerManager.WakeLock wakeLock;

    public GyroAccListener(Context context) {
        this.context = context;
        lastAccUpdate = SystemClock.elapsedRealtimeNanos();
        lastGyroUpdate = SystemClock.elapsedRealtimeNanos();
    }

    public GyroAccListener(){
        lastAccUpdate = SystemClock.elapsedRealtimeNanos();
        lastGyroUpdate = SystemClock.elapsedRealtimeNanos();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            getGyroscope(event);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        long actualAccTime = event.timestamp;

        if (actualAccTime - lastAccUpdate < 100000000) {
            return;
        }
        else if(actualAccTime - lastAccUpdate > 60000000000L) {
            writeToFile("\n\n\n\n\n", accPath, false);
        }

        lastAccUpdate = actualAccTime;

        long timeTenthsSeconds = actualAccTime / 100000000;
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String toFile = time.toString() + ": x: " +  x + ", y: " + y + ", z: " + z + "\n" + timeTenthsSeconds + "\n";
        writeToFile(toFile, accPath, false);
    }

    private void getGyroscope(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        long actualGyroTime = event.timestamp;

        if (actualGyroTime - lastGyroUpdate < 100000000) {
            return;
        }
        else if(actualGyroTime - lastGyroUpdate > 60000000000L) {
            writeToFile("\n\n\n\n\n", gyroPath, false);
        }

        lastGyroUpdate = actualGyroTime;

        long timeTenthsSeconds = actualGyroTime / 100000000;
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String toFile = time.toString() + ": x: " +  x + ", y: " + y + ", z: " + z + "\n" + timeTenthsSeconds + "\n";
        writeToFile(toFile, gyroPath, false);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
