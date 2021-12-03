package com.trzebiatowski.serkowski.biometricdatacollector.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.trzebiatowski.serkowski.biometricdatacollector.R;

import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.getUserId;
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

        accFileText.setText(getFileData("answers")); //getFileData("touch")
        gyroFileText.setText(getFileData("touch"));
    }

    private String getFileData(String dirname) {
        StringBuilder out = new StringBuilder();
        File dir = getApplicationContext().getDir(dirname, Context.MODE_PRIVATE);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    out.append("\n\n Reading file: ").append(file.getName()).append("\n\n");
                    out.append(readFromFile(dirname, file.getName(), getApplicationContext()));
                    out.append("\n\n END OF FILE \n\n");
                }
            }
        }
        return out.toString();
    }
}