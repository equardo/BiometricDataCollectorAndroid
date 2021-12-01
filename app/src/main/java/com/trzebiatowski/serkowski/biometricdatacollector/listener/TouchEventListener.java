package com.trzebiatowski.serkowski.biometricdatacollector.listener;

import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.writeToFile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

public class TouchEventListener implements View.OnTouchListener, Serializable {

    private static final String TOUCH_DATA_FOLDER = "touch";
    private static final String SWIPE_DATA_FOLDER = "swipe";
    private transient Context context;

    private int previousEventAction = MotionEvent.INVALID_POINTER_ID;
    private float previousX = -100;
    private float previousY = -100;
    private long previousTime = -1;
    private boolean firstTouchCall;
    private boolean firstSwipeCall;

    private final String touchPath;
    private final String swipePath;

    public TouchEventListener(Context context, String currentFileSuffix) {
        //this.text = text;
        this.context = context;
        this.touchPath = currentFileSuffix + ".txt";
        this.swipePath = currentFileSuffix + ".txt";
        firstTouchCall = true;
        firstSwipeCall = true;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float x = event.getRawX();
        float y = event.getRawY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setPreviousEventData(event);
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            if (Math.sqrt(Math.pow(x - previousX, 2) + Math.pow(y - previousY, 2)) < 20) {
                return false;
            }

            if(previousEventAction != MotionEvent.INVALID_POINTER_ID) {
                if (previousEventAction == MotionEvent.ACTION_DOWN) {
                    if(firstSwipeCall) {
                        firstSwipeCall = false;
                    }
                    else {
                        writeToFile(context, ",", SWIPE_DATA_FOLDER, swipePath, false);
                    }
                    writeToFile(context, "{\"points\":[", SWIPE_DATA_FOLDER, swipePath, false);
                }
                else {
                    writeToFile(context, ",", SWIPE_DATA_FOLDER, swipePath, false);
                }
                writeToFile(context, String.format("{\"t\":%d,\"x\": %.2f,\"y\": %.2f}", previousTime, previousX, previousY),
                        SWIPE_DATA_FOLDER, swipePath, false);
            }

            setPreviousEventData(event);

        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {

            // v.performClick();

            if(previousEventAction != MotionEvent.INVALID_POINTER_ID) {
                if (previousEventAction == MotionEvent.ACTION_DOWN) {
                    savePreviousTouchDataPoint();
                } else if (previousEventAction == MotionEvent.ACTION_MOVE) {
                    writeToFile(context, String.format(",{\"t\":%d,\"x\": %.2f,\"y\": %.2f}", previousTime, previousX, previousY),
                            SWIPE_DATA_FOLDER, swipePath, false);
                    writeToFile(context, String.format(",{\"t\":%d,\"x\": %.2f,\"y\": %.2f}", event.getEventTime(), x, y),
                            SWIPE_DATA_FOLDER, swipePath, false);

                    writeToFile(context, "]}", SWIPE_DATA_FOLDER, swipePath, false);
                }
            }

            setPreviousEventData(event);

            return false;
        }

        return false;
    }

    @SuppressLint("DefaultLocale")
    private void savePreviousTouchDataPoint() {
        if(firstTouchCall) {
            firstTouchCall = false;
        }
        else {
            writeToFile(context, "," ,TOUCH_DATA_FOLDER, touchPath, false);
        }
        writeToFile(context, String.format("{\"t\":%d,\"x\": %.2f,\"y\": %.2f}", previousTime, previousX, previousY),
                TOUCH_DATA_FOLDER, touchPath, false);
    }

    private void setPreviousEventData(MotionEvent event) {
        previousEventAction = event.getAction();
        previousX = event.getRawX();
        previousY = event.getRawY();
        previousTime = event.getEventTime();
    }
}
