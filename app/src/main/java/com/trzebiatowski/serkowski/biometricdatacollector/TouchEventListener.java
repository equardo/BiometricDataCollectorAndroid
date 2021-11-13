package com.trzebiatowski.serkowski.biometricdatacollector;

import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.writeToFile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class TouchEventListener implements View.OnTouchListener {

    private final TextView text;
    private final Context context;

    private int previousEventAction = MotionEvent.INVALID_POINTER_ID;
    private float previousX = -100;
    private float previousY = -100;

    private final String touchPath = "touch_data.txt";
    private final String swipePath = "swipe_data.txt";

    public TouchEventListener(TextView text, Context context) {
        this.text = text;
        this.context = context;
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
                    writeToFile(context, "{\"points\":[\n", swipePath, false);
                }
                writeToFile(context, String.format("{\"x\": %.2f,\"y\": %.2f}\n", previousX, previousY),
                        swipePath, false);
            }

            setPreviousEventData(event);

        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {

            v.performClick();

            if(previousEventAction != MotionEvent.INVALID_POINTER_ID) {
                if (previousEventAction == MotionEvent.ACTION_DOWN) {
                    writeToFile(context, String.format("{\"x\": %.2f,\"y\": %.2f}\n", previousX, previousY),
                            touchPath, false);
                } else if (previousEventAction == MotionEvent.ACTION_MOVE) {
                    writeToFile(context, String.format("{\"x\": %.2f,\"y\": %.2f}\n", previousX, previousY),
                            swipePath, false);
                    writeToFile(context, String.format("{\"x\": %.2f,\"y\": %.2f}\n", x, y),
                            swipePath, false);

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
