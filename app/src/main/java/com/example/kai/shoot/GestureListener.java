package com.example.kai.shoot;

/**
 * Created by Kai on 7/4/2017.
 */

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;


public class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//            return false; // Right to left
//        }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//            return false; // Left to right
//        }
//
//        if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//            return false; // Bottom to top
//        }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//            return false; // Top to bottom
//        }

        stats(e1,e2,velocityX,velocityY);
        return false;
    }

    private void stats(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("Gesture Listener","Velocity X" + velocityX);
        Log.d("Gesture Listener","Velocity Y" + velocityY);
        Log.d("Gesture Listener","Event 1 X" + e1.getX());
        Log.d("Gesture Listener","Event 1 Y" + e1.getY());
        Log.d("Gesture Listener","Event 2 X" + e2.getX());
        Log.d("Gesture Listener","Event 2 Y" + e2.getY());
        Log.d("---", "-----");
    }
}
