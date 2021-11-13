package com.trzebiatowski.serkowski.biometricdatacollector.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.trzebiatowski.serkowski.biometricdatacollector.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileViewActivity extends AppCompatActivity {

    private TextView accFileText;
    private TextView gyroFileText;
    private final String accPath = "acc_data.txt";
    private final String gyroPath = "gyro_data.txt";
    private final String touchPath = "touch_data.txt";
    private final String swipePath = "swipe_data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_content_view);
        accFileText = findViewById(R.id.accFileView);
        gyroFileText = findViewById(R.id.gyroFileView);
        accFileText.setText(readFromFile(touchPath));
        gyroFileText.setText(readFromFile(swipePath));
    }

    private String readFromFile(String filepath) {

        String ret = "";

        try {
            InputStream inputStream = getApplicationContext().openFileInput(filepath);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("Exception", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Exception", "Can not read file: " + e.toString());
        }

        return ret;
    }
}