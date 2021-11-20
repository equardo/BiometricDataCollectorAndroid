package com.trzebiatowski.serkowski.biometricdatacollector.alarmreciever;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import androidx.core.content.ContextCompat;

import com.trzebiatowski.serkowski.biometricdatacollector.service.GyroAccService;

public class StartDataCollectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, GyroAccService.class);
        ContextCompat.startForegroundService(context, serviceIntent);

        long secondsUntilSurvey = intent.getIntExtra("timeUntilNextSurvey", -1);

        if(secondsUntilSurvey != -1) {
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, StartSurveyReceiver.class);
            PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 3,
                    alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            secondsUntilSurvey * 1000, pendingAlarmIntent);
        }
    }
}
