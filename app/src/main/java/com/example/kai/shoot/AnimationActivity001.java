package com.example.kai.shoot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

/**
 *  Class used to control the activity of the game.
 */

public class AnimationActivity001 extends Activity{

    AnimationActivity001_Layout animationActivity001_LayoutView;

    double startX, startY, endX, endY, theta, startTime, endTime, timeDifference, distance, speed;
    double minTheta, maxTheta;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create an object of the layout class
        animationActivity001_LayoutView = new AnimationActivity001_Layout(this);
        //set the content to the game activity layout object
        setContentView(animationActivity001_LayoutView);

        //used for checking the bounds of theta in triggerFling()
        minTheta = (float)Math.PI/6;
        maxTheta = (float)Math.PI/6*5;


        //set up a listener on the animation activity layout
        animationActivity001_LayoutView.setOnTouchListener(new SurfaceView.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();
                        startTime = System.nanoTime();
                        break;

                    case MotionEvent.ACTION_UP:
                        endX = event.getRawX();
                        endY = event.getRawY();
                        endTime = System.nanoTime();
                        calculateStats();
                        triggerFling();
                        stats();
                        break;
                }

                return true;
            }
        });
    }

    //method used to calculate theta, distance, time difference, and speed
    public void calculateStats(){
        //THETA CALCULATION
        //when theta is perpendicular
        if(startX == endX)
        {
            theta = Math.PI/2;
        }
        //when theta is horizontal convert to 0 or PI
        else if(startY == endY){
            //if pointing towards the right, theta is 0
            if(endX > startX){
                theta = 0;
            }
            //if pointing towards the left, theta is PI
            else{
                theta = Math.PI;
            }
        }
        else {
            //calculate theta in radians
            theta = Math.atan((startY - endY)/(endX - startX));
            //convert negative theta to positive so that it is using the 0 to PI scale
            if(theta < 0){
                theta += Math.PI;
            }
        }

        //DISTANCE CALCULATION
        distance = Math.sqrt(Math.pow(Math.abs(endX - startX),2.0) + Math.pow(Math.abs(startY - endY), 2.0));

        //TIME DIFFERENCE
        timeDifference = (endTime - startTime)/1000000000;

        //SPEED
        //0.025 used to bring down the value of speed from 0 to 300
        speed = distance * 0.025 / timeDifference;


        //update the layout
        animationActivity001_LayoutView.distance = distance;
        animationActivity001_LayoutView.speed = speed;
        animationActivity001_LayoutView.theta = theta;


    }

    //method used to trigger the fling in activity
    public void triggerFling(){
        if( endY < startY && theta > minTheta && theta < maxTheta && distance > 100){
            animationActivity001_LayoutView.flingPressed = true;
        }
    }

    //create the on pause method in order to be able to pause in this class.
    @Override
    protected void onPause() {
        super.onPause();
        animationActivity001_LayoutView.pause(); //call layouts pause method
    }

    //create the on resume method in order to be able to resume in this class.
    @Override
    protected void onResume() {
        super.onResume();
        animationActivity001_LayoutView.resume();  //call layouts resume method
    }

    private void stats() {
        Log.d("Fling", "Start X: " + startX);
        Log.d("Fling", "Start Y: " + startY);
        Log.d("Fling", "End X: " + endX);
        Log.d("Fling", "End Y: " + endY);
        Log.d("Fling","Distance: " + distance);
        Log.d("Fling","Theta in Radians: " + theta);
        Log.d("Fling", "Start Time: " + (startTime/ 1000000000));
        Log.d("Fling", "End Time: " + (endTime/ 1000000000));
        Log.d("Fling","Time Difference: " + timeDifference);
        Log.d("Fling","Speed: " + speed);
        Log.d("----------", "----------------");
    }
}