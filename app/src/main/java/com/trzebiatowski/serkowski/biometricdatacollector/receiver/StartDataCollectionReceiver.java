package com.trzebiatowski.serkowski.biometricdatacollector.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import androidx.core.content.ContextCompat;

import com.trzebiatowski.serkowski.biometricdatacollector.service.GyroAccService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StartDataCollectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.US);
        String dateTime = simpleDateFormat.format(calendar.getTime());
        String accPath = dateTime + ".txt";
        String gyroPath = dateTime + ".txt";

        Intent serviceIntent = new Intent(context, GyroAccService.class);
        serviceIntent.putExtra("accPath", accPath);
        serviceIntent.putExtra("gyroPath", gyroPath);
        ContextCompat.startForegroundService(context, serviceIntent);

        int secondsUntilSurvey = intent.getIntExtra("collectionTimeSeconds", -1);

        if(secondsUntilSurvey != -1) {
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, StartSurveyReceiver.class);
            alarmIntent.putExtra("currentFileSuffix", dateTime);
            alarmIntent.putExtra("postponeTimeSeconds", intent.getIntExtra("postponeTimeSeconds", -1));
            alarmIntent.putExtra("collectionTimeSeconds", secondsUntilSurvey);
            PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 3,
                    alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmMgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            (long)secondsUntilSurvey * 1000, pendingAlarmIntent);
        }
    }
}
