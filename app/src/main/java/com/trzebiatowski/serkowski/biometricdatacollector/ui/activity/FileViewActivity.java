package com.trzebiatowski.serkowski.biometricdatacollector.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.trzebiatowski.serkowski.biometricdatacollector.R;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.readFromFile;

import java.io.File;

public class FileViewActivity extends AppCompatActivity {

    private TextView accFileText;
    private TextView gyroFileText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_content_view);
        accFileText = findViewById(R.id.accFileView);
        gyroFileText = findViewById(R.id.gyroFileView);

        accFileText.setText(getAccData());
        gyroFileText.setText(getGyroData());
        /* accFileText.setText(readFromFile(touchPath));
        gyroFileText.setText(readFromFile(swipePath));*/
    }

    private String getAccData() {
        StringBuilder out = new StringBuilder();
        File dir = getApplicationContext().getDir("accelerometer", Context.MODE_PRIVATE);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    out.append("\n\n Reading file: ").append(file.getName()).append("\n\n");
                    out.append(readFromFile("accelerometer", file.getName(), getApplicationContext()));
                    out.append("\n\n END OF FILE \n\n");
                }
            }
        }
        return out.toString();
    }

    private String getGyroData() {
        StringBuilder out = new StringBuilder();
        File dir = getApplicationContext().getDir("gyroscope", Context.MODE_PRIVATE);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    out.append("\n\n Reading file: ").append(file.getName()).append("\n\n");
                    out.append(readFromFile("gyroscope", file.getName(), getApplicationContext()));
                    out.append("\n\n END OF FILE \n\n");
                }
            }
        }
        return out.toString();
    }

    private String getSwipeData() {
        StringBuilder out = new StringBuilder();
        File dir = getApplicationContext().getDir("swipe", Context.MODE_PRIVATE);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    out.append("\n\n Reading file: ").append(file.getName()).append("\n\n");
                    out.append(readFromFile("swipe", file.getName(), getApplicationContext()));
                    out.append("\n\n END OF FILE \n\n");
                }
            }
        }
        return out.toString();
    }

    private String getAnswerData() {
        StringBuilder out = new StringBuilder();
        File dir = getApplicationContext().getDir("answers", Context.MODE_PRIVATE);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; ++i) {
                    File file = files[i];
                    out.append("\n\n Reading file: ").append(file.getName()).append("\n\n");
                    out.append(readFromFile("answers", file.getName(), getApplicationContext()));
                    out.append("\n\n END OF FILE \n\n");
                }
            }
        }
        return out.toString();
    }
}