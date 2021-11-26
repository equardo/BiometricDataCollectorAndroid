package com.trzebiatowski.serkowski.biometricdatacollector.receiver;

import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.removeFile;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;

import com.trzebiatowski.serkowski.biometricdatacollector.service.GyroAccService;

public class PostponeSurveyReceiver extends BroadcastReceiver {

    private static final int SURVEY_NOTIFICATION_ID = 2;

    @Override
    public void onReceive(Context context, Intent intent) {

        stopCollection(context);
        removeUntaggedData(context, intent.getStringExtra("currentFileSuffix"));
        removeSurveyNotification(context);

        int collectionTimeSeconds = intent.getIntExtra("collectionTimeSeconds", -1);
        int postponeTimeSeconds = intent.getIntExtra("postponeTimeSeconds", -1);

        if(collectionTimeSeconds != -1 && postponeTimeSeconds != -1) {
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, StartDataCollectionReceiver.class);
            alarmIntent.putExtra("collectionTimeSeconds", collectionTimeSeconds);
            alarmIntent.putExtra("postponeTimeSeconds", postponeTimeSeconds);
            PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 4,
                    alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            int timeUntilCollectionStart = (postponeTimeSeconds) * 1000;

            alarmMgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            timeUntilCollectionStart, pendingAlarmIntent);
        }
    }

    private void removeSurveyNotification(Context context) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(SURVEY_NOTIFICATION_ID);
    }

    private void removeUntaggedData(Context context, String currentFileSuffix) {
        removeFile(context, "accelerometer", currentFileSuffix + ".txt");
        removeFile(context, "gyroscope", currentFileSuffix + ".txt");
    }

    private void stopCollection(Context context) {
        Intent serviceIntent = new Intent(context, GyroAccService.class);
        context.stopService(serviceIntent);

        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent startCollectionIntent = new Intent(context, StartDataCollectionReceiver.class);
        PendingIntent pendingStartCollectionIntent = PendingIntent.getBroadcast(context, 4,
                startCollectionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendingStartCollectionIntent);

        Intent createSurveyNotificationIntent = new Intent(context, StartSurveyReceiver.class);
        PendingIntent pendingCreateSurveyNotificationIntent = PendingIntent.getBroadcast(context, 3,
                createSurveyNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendingCreateSurveyNotificationIntent);

        Intent stopCollectionIntent = new Intent(context, StopDataCollectionReceiver.class);
        PendingIntent pendingStopCollectionIntent = PendingIntent.getBroadcast(context, 5,
                stopCollectionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendingStopCollectionIntent);
    }
}
