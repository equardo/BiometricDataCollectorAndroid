package com.trzebiatowski.serkowski.biometricdatacollector.listener;

import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.writeToFile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class TouchEventListener implements View.OnTouchListener {

    public static final String TOUCH_DATA_FOLDER = "touch";
    public static final String SWIPE_DATA_FOLDER = "swipe";
    private final TextView text;
    private final Context context;
    private final String currentFileSuffix;

    private int previousEventAction = MotionEvent.INVALID_POINTER_ID;
    private float previousX = -100;
    private float previousY = -100;

    private final String touchPath;
    private final String swipePath;

    public TouchEventListener(TextView text, Context context, String currentFileSuffix) {
        this.text = text;
        this.context = context;
        this.currentFileSuffix = currentFileSuffix;
        this.touchPath = currentFileSuffix + ".txt";
        this.swipePath = currentFileSuffix + ".txt";
    }

    @SuppressLint("DefaultLocale")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float x = event.getRawX();
        float y = event.getRawY();

        text.setText(String.format("x: %.2f, y: %.2f", x, y));

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setPreviousEventData(event);
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            if (Math.sqrt(Math.pow(x - previousX, 2) + Math.pow(y - previousY, 2)) < 20) {
                return false;
            }

            if(previousEventAction != MotionEvent.INVALID_POINTER_ID) {
                if (previousEventAction == MotionEvent.ACTION_DOWN) {
                    writeToFile(context, "{\"points\":[\n", SWIPE_DATA_FOLDER, swipePath, false);
                }
                writeToFile(context, String.format("{\"x\": %.2f,\"y\": %.2f}\n", previousX, previousY),
                        SWIPE_DATA_FOLDER, swipePath, false);
            }

            setPreviousEventData(event);

        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {

            v.performClick();

            if(previousEventAction != MotionEvent.INVALID_POINTER_ID) {
                if (previousEventAction == MotionEvent.ACTION_DOWN) {
                    writeToFile(context, String.format("{\"x\": %.2f,\"y\": %.2f}\n", previousX, previousY),
                            TOUCH_DATA_FOLDER, touchPath, false);
                } else if (previousEventAction == MotionEvent.ACTION_MOVE) {
                    writeToFile(context, String.format("{\"x\": %.2f,\"y\": %.2f}\n", previousX, previousY),
                            SWIPE_DATA_FOLDER, swipePath, false);
                    writeToFile(context, String.format("{\"x\": %.2f,\"y\": %.2f}\n", x, y),
                            SWIPE_DATA_FOLDER, swipePath, false);

                    writeToFile(context, "\n]}\n", swipePath, false);
                }
            }

            setPreviousEventData(event);

            return true;
        }

        return false;
    }

    private void setPreviousEventData(MotionEvent event) {
        previousEventAction = event.getAction();
        previousX = event.getRawX();
        previousY = event.getRawY();
    }
}
