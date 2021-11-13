package com.trzebiatowski.serkowski.biometricdatacollector.utility;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
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
}
