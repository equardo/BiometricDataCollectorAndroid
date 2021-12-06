package com.trzebiatowski.serkowski.biometricdatacollector.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.trzebiatowski.serkowski.biometricdatacollector.R;
import com.trzebiatowski.serkowski.biometricdatacollector.ui.activity.SurveyActivity;

public class StartSurveyReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "FillOutSurveyNotificationChannel";
    public static final int SURVEY_NOTIFICATION_ID = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        String currentFileSuffix = intent.getStringExtra("currentFileSuffix");
        int minutesBetweenSurveys = intent.getIntExtra("timeBetweenSurveys", -1);

        createNotificationChannel(context);
        Intent notificationIntent = new Intent(context, SurveyActivity.class);
        notificationIntent.putExtra("currentFileSuffix", currentFileSuffix);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context,
                8, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent postponeIntent = new Intent(context, PostponeSurveyReceiver.class);
        postponeIntent.putExtra("currentFileSuffix", currentFileSuffix);
        postponeIntent.putExtra("postponeTimeSeconds", intent.getIntExtra("postponeTimeSeconds", -1));
        postponeIntent.putExtra("collectionTimeSeconds", intent.getIntExtra("collectionTimeSeconds", -1));
        postponeIntent.putExtra("timeBetweenSurveys", minutesBetweenSurveys);
        PendingIntent pendingPostponeIntent = PendingIntent.getBroadcast(context,
                9, postponeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(minutesBetweenSurveys != -1) {
            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            (long) minutesBetweenSurveys * 60 * 1000, pendingPostponeIntent);
        }

        NotificationCompat.Action postponeAction = new NotificationCompat.Action(0, "Postpone", pendingPostponeIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Survey ready! Tap this to fill it out.")
                .setContentText("Please fill it out as soon as possible. Thank you!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingNotificationIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                .addAction(postponeAction)
                .setOngoing(true)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(SURVEY_NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel surveyChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Survey notification channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            surveyChannel.enableVibration(true);
            surveyChannel.shouldVibrate();
            surveyChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = ctx.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(surveyChannel);
        }
    }
}
