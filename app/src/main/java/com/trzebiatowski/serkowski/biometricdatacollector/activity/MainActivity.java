package com.trzebiatowski.serkowski.biometricdatacollector.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.trzebiatowski.serkowski.biometricdatacollector.GyroAccService;
import com.trzebiatowski.serkowski.biometricdatacollector.R;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;

public class MainActivity extends AppCompatActivity {

    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionListener dialogPermissionListener =
                DialogOnDeniedPermissionListener.Builder
                        .withContext(getApplicationContext())
                        .withTitle("File write permission")
                        .withMessage("File write is needed to save data")
                        .withButtonText(android.R.string.ok)
                        .build();

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(dialogPermissionListener)
                .check();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire(10*60*1000L /*10 minutes*/);


        TextView accFileText = findViewById(R.id.accelerometerTextView);
        TextView gyroFileText = findViewById(R.id.gyroscopeTextView);

        double acclength = new File(getApplicationContext().getFilesDir() + "/" + "acc_data.txt").length();
        acclength = acclength / 1000;
        accFileText.setText(MessageFormat.format("Accelerometer file size: {0,number,#.##}", acclength));

        double gyrolength = new File(getApplicationContext().getFilesDir() + "/" + "gyro_data.txt").length();
        gyrolength = gyrolength / 1000;
        gyroFileText.setText(MessageFormat.format("Gyroscope file size: {0,number,#.##}", gyrolength));

        Intent serviceIntent = new Intent(this, GyroAccService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }

    public void viewFiles(View v) {
        Intent intent = new Intent(this, FileViewActivity.class);
        startActivity(intent);
    }

    public void startSurvey(View v) {
        Intent intent = new Intent(this, SurveyActivity.class);
        startActivity(intent);
    }

    public void deleteFilesContent(View v) {
        String accPath = "acc_data.txt";
        String gyroPath = "gyro_data.txt";
        writeToFile("", accPath, true);
        writeToFile("", gyroPath, true);
    }

    private void writeToFile(String data, String filepath, boolean overwrite) {
        try {
            FileOutputStream fOut;

            if(overwrite) {
                fOut = getApplicationContext().openFileOutput(filepath, Context.MODE_PRIVATE);
            }
            else {
                fOut = getApplicationContext().openFileOutput(filepath, Context.MODE_APPEND);
            }

            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            osw.write(data);

            osw.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private boolean checkWriteExternalPermission()
    {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}