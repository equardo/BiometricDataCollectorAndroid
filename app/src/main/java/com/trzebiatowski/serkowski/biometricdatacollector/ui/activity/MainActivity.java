package com.trzebiatowski.serkowski.biometricdatacollector.ui.activity;

import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.deleteUntaggedDataFiles;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.readConfigFile;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.readFromFile;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.writeToFile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.trzebiatowski.serkowski.biometricdatacollector.R;
import com.trzebiatowski.serkowski.biometricdatacollector.client.ServerApplicationClient;
import com.trzebiatowski.serkowski.biometricdatacollector.dto.ConfigFileDto;
import com.trzebiatowski.serkowski.biometricdatacollector.receiver.PostponeSurveyReceiver;
import com.trzebiatowski.serkowski.biometricdatacollector.receiver.StartDataCollectionReceiver;
import com.trzebiatowski.serkowski.biometricdatacollector.receiver.StartSurveyReceiver;
import com.trzebiatowski.serkowski.biometricdatacollector.receiver.StopDataCollectionReceiver;
import com.trzebiatowski.serkowski.biometricdatacollector.service.GyroAccService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ConfigFileDto configData;
    private static final int SURVEY_NOTIFICATION_ID = 2;
    private ServerApplicationClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        client = new ServerApplicationClient("https://biometrical-collector.herokuapp.com",
                "config", "insert_multipart");

        String configString = readFromFile("config.json", this);
        if("".equals(configString)) {
            client.getConfigFile(this);
        }
        else {
            getConfigFromMemory();
        }
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
        Intent intent = new Intent(this, StartDataCollectionReceiver.class);
        intent.putExtra("collectionTimeSeconds", configData.getCollectionTimeSeconds());
        intent.putExtra("postponeTimeSeconds", configData.getPostponeTimeSeconds());
        intent.putExtra("timeBetweenSurveys", configData.getTimeBetweenSurveysMinutes());

        sendBroadcast(intent);
    }

    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, GyroAccService.class);
        getApplicationContext().stopService(serviceIntent);

        cancelAlarms();

        NotificationManager nMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(SURVEY_NOTIFICATION_ID);

        removeUntaggedData();
    }

    private void removeUntaggedData() {
        deleteUntaggedDataFiles(this, "accelerometer");
        deleteUntaggedDataFiles(this, "gyroscope");
        deleteUntaggedDataFiles(this, "touch");
        deleteUntaggedDataFiles(this, "swipe");
    }

    private void cancelAlarms() {
        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        Intent startCollectionIntent = new Intent(this, StartDataCollectionReceiver.class);
        Intent createSurveyNotificationIntent = new Intent(this, StartSurveyReceiver.class);
        Intent stopCollectionIntent = new Intent(this, StopDataCollectionReceiver.class);
        Intent postponeIntent = new Intent(this, PostponeSurveyReceiver.class);

        PendingIntent pendingStartCollectionIntent = PendingIntent.getBroadcast(this, 4,
                startCollectionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendingStartCollectionIntent);

        PendingIntent pendingPostponeIntent = PendingIntent.getBroadcast(this,
                9, postponeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendingPostponeIntent);

        PendingIntent pendingCreateSurveyNotificationIntent = PendingIntent.getBroadcast(this, 3,
                createSurveyNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendingCreateSurveyNotificationIntent);

        PendingIntent pendingStopCollectionIntent = PendingIntent.getBroadcast(this, 5,
                stopCollectionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendingStopCollectionIntent);
    }

    public void sendData(View v) {
        try {
            client.sendData(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}