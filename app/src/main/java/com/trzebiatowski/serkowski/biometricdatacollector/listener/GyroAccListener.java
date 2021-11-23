package com.trzebiatowski.serkowski.biometricdatacollector.listener;

import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.writeToFile;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.PowerManager;
import android.os.SystemClock;

import java.io.File;
import java.sql.Timestamp;

public class GyroAccListener implements SensorEventListener {

    private long lastAccUpdate;
    private long lastGyroUpdate;
    private Context context;
    private String accPath;
    private String gyroPath;

    private PowerManager.WakeLock wakeLock;

    public GyroAccListener(Context context, String accPath, String gyroPath) {
        this.context = context;
        this.accPath = accPath;
        this.gyroPath = gyroPath;
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
            writeToFile(context, "\n\n\n\n\n", accPath, false);
        }

        lastAccUpdate = actualAccTime;

        long timeTenthsSeconds = actualAccTime / 100000000;
        Timestamp time = new Timestamp(System.currentTimeMillis());

        String toFile = time.toString() + ": x: " +  x + ", y: " + y + ", z: " + z + "\n" + timeTenthsSeconds + "\n";
        writeToFile(context, toFile, "accelerometer", accPath, false);
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
            writeToFile(context, "\n\n\n\n\n", gyroPath, false);
        }

        lastGyroUpdate = actualGyroTime;

        long timeTenthsSeconds = actualGyroTime / 100000000;
        Timestamp time = new Timestamp(System.currentTimeMillis());

        String toFile = time.toString() + ": x: " +  x + ", y: " + y + ", z: " + z + "\n" + timeTenthsSeconds + "\n";
        writeToFile(context, toFile, "gyroscope", gyroPath, false);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
