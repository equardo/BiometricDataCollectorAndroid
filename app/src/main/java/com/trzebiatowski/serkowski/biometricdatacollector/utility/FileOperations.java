package com.trzebiatowski.serkowski.biometricdatacollector.utility;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trzebiatowski.serkowski.biometricdatacollector.dto.ConfigFileDto;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileOperations {

    public static void writeToFile(Context context, String data, String filepath, boolean overwrite) {

        try {
            FileOutputStream fOut;

            if(overwrite) {
                fOut = context.openFileOutput(filepath, Context.MODE_PRIVATE);
            }
            else {
                fOut = context.openFileOutput(filepath, Context.MODE_APPEND);
            }

            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            osw.write(data);

            osw.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    public static ConfigFileDto readConfigFile(Context ctx){
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            AssetManager am = ctx.getAssets();
            InputStream stream = am.open("biometricdatacollector-survey.json");

            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            StringBuilder total = new StringBuilder();

            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }

            return objectMapper.readValue(total.toString(), ConfigFileDto.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
