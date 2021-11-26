package com.trzebiatowski.serkowski.biometricdatacollector.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.trzebiatowski.serkowski.biometricdatacollector.service.GyroAccService;

public class StopDataCollectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, GyroAccService.class);
        context.stopService(serviceIntent);

        int collectionTimeSeconds = intent.getIntExtra("collectionTime", -1);
        int minutesBetweenSurveys = intent.getIntExtra("timeBetweenSurveys", -1);

        if(collectionTimeSeconds != -1 && minutesBetweenSurveys != -1) {
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, StartDataCollectionReceiver.class);
            alarmIntent.putExtra("collectionTimeSeconds", collectionTimeSeconds);
            alarmIntent.putExtra("postponeTimeSeconds", intent.getIntExtra("postponeTimeSeconds", -1));
            PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 4,
                    alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            int timeUntilCollectionStart = minutesBetweenSurveys * 60 * 1000 - 2 * collectionTimeSeconds * 1000;
            // Time until next collection period is the time between surveys minus the time, that has passed
            // since last survey and time needed to collect data before next survey.

            alarmMgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            timeUntilCollectionStart, pendingAlarmIntent);
        }
    }
}
