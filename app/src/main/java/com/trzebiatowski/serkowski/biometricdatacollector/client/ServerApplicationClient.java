package com.trzebiatowski.serkowski.biometricdatacollector.client;

import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.getUserId;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.readFromFile;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.writeToFile;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.trzebiatowski.serkowski.biometricdatacollector.R;
import com.trzebiatowski.serkowski.biometricdatacollector.dto.ConfigFileDto;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ServerApplicationClient {

    public static final String CHANNEL_ID = "SendDataNotificationChannel";
    public static final int FAILURE_NOTIFICATION_ID = 31;

    private final String serverUrl;
    private final String getConfigEndpoint;
    private final String postDataEndpoint;
    private final OkHttpClient client = new OkHttpClient();

    public ServerApplicationClient(String serverUrl, String getConfigEndpoint, String postDataEndpoint) {
        this.serverUrl = serverUrl;
        this.getConfigEndpoint = getConfigEndpoint;
        this.postDataEndpoint = postDataEndpoint;
    }

    public void getConfigFile(Activity context) {
        Request request = new Request.Builder()
                .url(serverUrl + "/" + getConfigEndpoint)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                if(response.isSuccessful()) {
                    createToast(context, "Received config");
                    writeToFile(context, body, "config.json", true);
                }
                else {
                    createToast(context, "Failed to get configuration from server. Please relaunch the application and try again.");
                }
            }

            public void onFailure(Call call, IOException e) {
                createToast(context, "Failed to get configuration from server. Please relaunch the application and try again.");
            }
        });
    }

    public void sendData(Context context) throws IOException {
        File dir = context.getDir("answers", Context.MODE_PRIVATE);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    sendCollectionPeriodData(context, file);
                }
            }
        }
    }

    private void sendCollectionPeriodData(Context context, File file){
        String name = file.getName();

        String id = getUserId(context);

        File answers_file = new File(context.getDir("answers", Context.MODE_PRIVATE), name);
        File acc_file = new File(context.getDir("accelerometer", Context.MODE_PRIVATE), name);
        File gyro_file = new File(context.getDir("gyroscope", Context.MODE_PRIVATE), name);
        File touch_file = new File(context.getDir("touch", Context.MODE_PRIVATE), name);
        File swipe_file = new File(context.getDir("swipe", Context.MODE_PRIVATE), name);

        Request request = getSendDataRequest(name, id, answers_file, acc_file, gyro_file, touch_file, swipe_file);

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        createFailureNotification(context, "Data send failed.",  "Please try again soon");
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        String res = response.body().string();
                        if(response.isSuccessful()) {
                            answers_file.delete();
                            acc_file.delete();
                            gyro_file.delete();
                            touch_file.delete();
                            swipe_file.delete();
                        }
                        else {
                            String notificationText = "Server returned response with code " + response.code();
                            createFailureNotification(context, "Data send failed.", notificationText);
                        }
                    }
                });

        /*
        removeFolderContents(getApplicationContext(), "accelerometer");
        removeFolderContents(getApplicationContext(), "gyroscope");
        removeFolderContents(getApplicationContext(), "swipe");
        removeFolderContents(getApplicationContext(), "touch");
        removeFolderContents(getApplicationContext(), "answers");
        */
    }

    @NonNull
    protected Request getSendDataRequest(String name, String id, File answers_file, File acc_file,
                                       File gyro_file, File touch_file, File swipe_file) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", id)
                .addFormDataPart("start_time", name)
                .addFormDataPart("answers", "answers.txt",
                        RequestBody.create(answers_file,
                                MediaType.parse("application/octet-stream")))
                .addFormDataPart("swipes_data", "swipe.txt",
                        RequestBody.create(swipe_file,
                                MediaType.parse("application/octet-stream")))
                .addFormDataPart("touch_data", "touch.txt",
                        RequestBody.create(touch_file,
                                MediaType.parse("application/octet-stream")))
                .addFormDataPart("acc_data", "accelerometer.txt",
                        RequestBody.create(acc_file,
                                MediaType.parse("application/octet-stream")))
                .addFormDataPart("gyro_data", "gyroscope.txt",
                        RequestBody.create(gyro_file,
                                MediaType.parse("application/octet-stream")))
                .build();

        return new Request.Builder()
                .url(serverUrl + "/" + postDataEndpoint)
                .post(requestBody)
                .build();
    }

    private void createFailureNotification(Context context, String title, String contentText) {
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(FAILURE_NOTIFICATION_ID, builder.build());
    }

    private void createToast(Activity context, String text) {
        context.runOnUiThread(() -> {
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        });
    }

    private void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Survey notification channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = ctx.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
