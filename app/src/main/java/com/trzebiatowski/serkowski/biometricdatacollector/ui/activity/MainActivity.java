package com.trzebiatowski.serkowski.biometricdatacollector.ui.activity;

import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.readConfigFile;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.writeToFile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.trzebiatowski.serkowski.biometricdatacollector.alarmreciever.StartDataCollectionReceiver;
import com.trzebiatowski.serkowski.biometricdatacollector.alarmreciever.StartSurveyReceiver;
import com.trzebiatowski.serkowski.biometricdatacollector.alarmreciever.StopDataCollectionReceiver;
import com.trzebiatowski.serkowski.biometricdatacollector.service.GyroAccService;
import com.trzebiatowski.serkowski.biometricdatacollector.R;
import com.trzebiatowski.serkowski.biometricdatacollector.dto.ConfigFileDto;


import java.io.File;
import java.text.MessageFormat;

public class MainActivity extends AppCompatActivity {

    private PowerManager.WakeLock wakeLock;

    private ConfigFileDto configData;

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

        /*PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire(10*60*1000L);*/

        configData = readConfigFile(this);

        TextView accFileText = findViewById(R.id.accelerometerTextView);
        TextView gyroFileText = findViewById(R.id.gyroscopeTextView);

        double acclength = new File(getApplicationContext().getFilesDir() + "/" + "acc_data.txt").length();
        acclength = acclength / 1000;
        accFileText.setText(MessageFormat.format("Accelerometer file size: {0,number,#.##}", acclength));

        double gyrolength = new File(getApplicationContext().getFilesDir() + "/" + "gyro_data.txt").length();
        gyrolength = gyrolength / 1000;
        gyroFileText.setText(MessageFormat.format("Gyroscope file size: {0,number,#.##}", gyrolength));

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
        String touchPath = "touch_data.txt";
        String swipePath = "swipe_data.txt";

        writeToFile(getApplicationContext(), "", accPath, true);
        writeToFile(getApplicationContext(), "", gyroPath, true);
        writeToFile(getApplicationContext(), "", touchPath, true);
        writeToFile(getApplicationContext(), "", swipePath, true);
    }

    public void startService(View v) {

        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StartDataCollectionReceiver.class);
        intent.putExtra("timeUntilNextSurvey", configData.getCollectionTimeSeconds());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 2, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        5 * 1000, alarmIntent);

    }

    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, GyroAccService.class);
        getApplicationContext().stopService(serviceIntent);

        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        Intent startCollectionIntent = new Intent(this, StartDataCollectionReceiver.class);
        PendingIntent pendingStartCollectionIntent = PendingIntent.getBroadcast(this, 4,
                startCollectionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendingStartCollectionIntent);

        Intent createSurveyNotificationIntent = new Intent(this, StartSurveyReceiver.class);
        PendingIntent pendingCreateSurveyNotificationIntent = PendingIntent.getBroadcast(this, 3,
                createSurveyNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendingCreateSurveyNotificationIntent);

        Intent stopCollectionIntent = new Intent(this, StopDataCollectionReceiver.class);
        PendingIntent pendingStopCollectionIntent = PendingIntent.getBroadcast(this, 5,
                stopCollectionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendingStopCollectionIntent);
    }

    private boolean checkWriteExternalPermission()
    {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}