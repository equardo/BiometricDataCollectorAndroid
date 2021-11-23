package com.trzebiatowski.serkowski.biometricdatacollector.utility;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trzebiatowski.serkowski.biometricdatacollector.dto.ConfigFileDto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileOperations {

    public static String readFromFile(String filename, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

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

    public static String readFromFile(String dirname, String filename, Context context) {

        String ret = "";
        File directory = context.getDir(dirname, Context.MODE_PRIVATE);

        try {
            File file = new File(directory, filename);
            InputStream inputStream = new FileInputStream(file);

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

    public static void writeToFile(Context context, String data, String filename, boolean overwrite) {

        try {
            FileOutputStream fOut;

            if(overwrite) {
                fOut = context.openFileOutput(filename, Context.MODE_PRIVATE);
            }
            else {
                fOut = context.openFileOutput(filename, Context.MODE_APPEND);
            }

            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            osw.write(data);

            osw.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void writeToFile(Context context, String data, String dirname, String filename, boolean overwrite) {

        File directory = context.getDir(dirname, Context.MODE_PRIVATE);

        if(!directory.exists())
            directory.mkdir();

        File file = new File(directory, filename);

        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try  {
            FileOutputStream fOut = new FileOutputStream(file, !overwrite);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fOut);
            outputWriter.write(data);
            outputWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void removeFolderContents(Context context, String dirname) {
        File directory = context.getDir(dirname, Context.MODE_PRIVATE);

        if(!directory.exists())
            throw new RuntimeException("Folder doesn't exist");

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
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
