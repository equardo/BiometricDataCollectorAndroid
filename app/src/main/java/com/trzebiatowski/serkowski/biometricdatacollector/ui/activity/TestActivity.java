package com.trzebiatowski.serkowski.biometricdatacollector.ui.activity;

import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.getFolderSize;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.readConfigFile;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.removeFolderContents;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.writeToFile;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.trzebiatowski.serkowski.biometricdatacollector.client.ServerApplicationClient;
import com.trzebiatowski.serkowski.biometricdatacollector.receiver.StartDataCollectionReceiver;
import com.trzebiatowski.serkowski.biometricdatacollector.receiver.StartSurveyReceiver;
import com.trzebiatowski.serkowski.biometricdatacollector.receiver.StopDataCollectionReceiver;
import com.trzebiatowski.serkowski.biometricdatacollector.service.GyroAccService;
import com.trzebiatowski.serkowski.biometricdatacollector.R;
import com.trzebiatowski.serkowski.biometricdatacollector.dto.ConfigFileDto;


import java.io.IOException;
import java.text.MessageFormat;

public class TestActivity extends AppCompatActivity {

    private ConfigFileDto configData;
    private static final int SURVEY_NOTIFICATION_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

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

        double acclength = getFolderSize(this, "accelerometer");
        acclength = acclength / 1000;
        accFileText.setText(MessageFormat.format("Accelerometer file size: {0,number,#.##}", acclength));

        double gyrolength = getFolderSize(this, "gyroscope");
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
        /*String accPath = "acc_data.txt";
        String gyroPath = "gyro_data.txt";
        String touchPath = "touch_data.txt";
        String swipePath = "swipe_data.txt";

        writeToFile(getApplicationContext(), "", accPath, true);
        writeToFile(getApplicationContext(), "", gyroPath, true);
        writeToFile(getApplicationContext(), "", touchPath, true);
        writeToFile(getApplicationContext(), "", swipePath, true);*/

        removeFolderContents(getApplicationContext(), "accelerometer");
        removeFolderContents(getApplicationContext(), "gyroscope");
        removeFolderContents(getApplicationContext(), "swipe");
        removeFolderContents(getApplicationContext(), "touch");
        removeFolderContents(getApplicationContext(), "answers");
        writeToFile(this, "", "id.txt", true);
    }

    public void startService(View v) {

        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StartDataCollectionReceiver.class);
        intent.putExtra("collectionTimeSeconds", configData.getCollectionTimeSeconds());
        intent.putExtra("postponeTimeSeconds", configData.getPostponeTimeSeconds());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 2, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        2 * 1000, alarmIntent);

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

        NotificationManager nMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(SURVEY_NOTIFICATION_ID);
    }

    public void sendData(View v) {
        ServerApplicationClient client = new ServerApplicationClient("https://mock.codes/202");

        try {
            client.sendData(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}