package com.trzebiatowski.serkowski.biometricdatacollector.ui.activity;

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
import android.os.SystemClock;
import android.view.View;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.trzebiatowski.serkowski.biometricdatacollector.R;
import com.trzebiatowski.serkowski.biometricdatacollector.client.ServerApplicationClient;
import com.trzebiatowski.serkowski.biometricdatacollector.dto.ConfigFileDto;
import com.trzebiatowski.serkowski.biometricdatacollector.receiver.StartDataCollectionReceiver;
import com.trzebiatowski.serkowski.biometricdatacollector.receiver.StartSurveyReceiver;
import com.trzebiatowski.serkowski.biometricdatacollector.receiver.StopDataCollectionReceiver;
import com.trzebiatowski.serkowski.biometricdatacollector.service.GyroAccService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ConfigFileDto configData;
    private static final int SURVEY_NOTIFICATION_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String configString = readFromFile("config.json", this);
        if("".equals(configString)) {
            ServerApplicationClient client = new ServerApplicationClient("https://biometrical-collector.herokuapp.com",
                    "config", "");
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
        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StartDataCollectionReceiver.class);
        intent.putExtra("collectionTimeSeconds", configData.getCollectionTimeSeconds());
        intent.putExtra("postponeTimeSeconds", configData.getPostponeTimeSeconds());
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