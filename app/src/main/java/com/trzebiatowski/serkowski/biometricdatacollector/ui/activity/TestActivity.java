package com.trzebiatowski.serkowski.biometricdatacollector.ui.activity;

import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.getFolderSize;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.readConfigFile;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.readFromFile;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.removeFolderContents;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.writeToFile;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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

        String configString = readFromFile("config.json", this);
        if("".equals(configString)) {
            ServerApplicationClient client = new ServerApplicationClient("https://biometrical-collector.herokuapp.com",
                    "config", "");
            client.getConfigFile(this);
        }
        else {
            getConfigFromMemory();
        }

        TextView accFileText = findViewById(R.id.accelerometerTextView);
        TextView gyroFileText = findViewById(R.id.gyroscopeTextView);

        double acclength = getFolderSize(this, "accelerometer");
        acclength = acclength / 1000;
        accFileText.setText(MessageFormat.format("Accelerometer file size: {0,number,#.##}", acclength));

        double gyrolength = getFolderSize(this, "gyroscope");
        gyrolength = gyrolength / 1000;
        gyroFileText.setText(MessageFormat.format("Gyroscope file size: {0,number,#.##}", gyrolength));

    }

    private void getConfigFromMemory() {
        try {
            configData = readConfigFile(this);
        } catch (JsonMappingException | JsonParseException e) {
            showErrorDialog(this,
                    "There was an error parsing the config file. Please contact the person conducting this research.");
            writeToFile(this, "", "config.json", true);
        }
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
        removeFolderContents(getApplicationContext(), "accelerometer");
        removeFolderContents(getApplicationContext(), "gyroscope");
        removeFolderContents(getApplicationContext(), "swipe");
        removeFolderContents(getApplicationContext(), "touch");
        removeFolderContents(getApplicationContext(), "answers");
        writeToFile(this, "", "id.txt", true);
        writeToFile(this, "", "config.json", true);
    }

    private void showErrorDialog(Context context, String text) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(text)
                .setNeutralButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void startService(View v) {
        if(configData == null) {
            getConfigFromMemory();
            if(configData == null) {
                return;
            }
        }
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
        ServerApplicationClient client = new ServerApplicationClient("https://mock.codes/202",
                "", "");

        try {
            client.sendData(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}