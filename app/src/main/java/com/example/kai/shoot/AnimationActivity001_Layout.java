package com.example.kai.shoot;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * Layout for the animation game activity class.
 * Used instead of xml class.
 */

public class AnimationActivity001_Layout extends SurfaceView implements Runnable {

    Thread thread = null;  //thread used for running/stopping by runnable
    double frames_per_second, frame_time_seconds, frame_time_ms, frame_time_ns;
    double last_frameT, end_of_renderT, deltaT;

    boolean canDraw; //used to check if game is capable of drawing to
    public static boolean flingPressed;  //used to check if fling was pressed

    //bitmap collects pixel data of image
    Bitmap grassfield_bm, soccerball_bm, soccergoaltop_bm, soccergoalbottom_bm;
    // x & y coordinates
    int grassfield_x, grassfield_y, soccerball_x, soccerball_y, soccergoaltop_x, soccergoaltop_y,
            soccergoalbottom_x, soccergoalbottom_y;
    int ballWidth, ballHeight, goalTopWidth, goalTopHeight, goalBottomWidth, goalBottomHeight;
    int screenWidth, screenHeight;
    Canvas canvas;  // class that can be drawn on by hosting the bitmap and calling draw
    SurfaceHolder surfaceHolder; //the background window of the app, returns a canvas to draw on
    int goalSpeed; //goalSpeed of the goal
    double density; //screen density

    //VARIABLES USED IN BALLMOTION()
    public static double speed, theta, distance; //speed, angle, and distance of the ball
    int ballSize, prevBallSize; //temporary variables used for resizing the ball in ballMotion()
    double endpointX, endpointY; // (x,y) coordinate of the endpoint of the parabola
    double vertexX, vertexY; // (x,y) coordinates of the vertex of the parabola
    double minSpeed, maxSpeed;

    public AnimationActivity001_Layout(Context context) {
        super(context);

        minSpeed = 30;
        maxSpeed = 100;
        flingPressed = false;  //initially set to false


        surfaceHolder = getHolder();
        //initialize the bitmaps of each drawable
        grassfield_bm = BitmapFactory.decodeResource(getResources(), R.drawable.grassfield);
        soccerball_bm = BitmapFactory.decodeResource(getResources(), R.drawable.soccerball);
        soccergoaltop_bm = BitmapFactory.decodeResource(getResources(), R.drawable.soccergoaltop);
        soccergoalbottom_bm = BitmapFactory.decodeResource(getResources(), R.drawable.soccergoalbottom);

        density = getResources().getDisplayMetrics().density; //screen density

        //used to decode bounds of goal top, bottom, and the ball
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.soccergoaltop, options);
        goalTopWidth = toPxs(options.outWidth);
        goalTopHeight = toPxs(options.outHeight);
        // decode size of bottom of the goal
        BitmapFactory.decodeResource(getResources(), R.drawable.soccergoalbottom, options);
        goalBottomWidth = toPxs(options.outWidth);
        goalBottomHeight = toPxs(options.outHeight);
        //decode size of the ball
        BitmapFactory.decodeResource(getResources(), R.drawable.soccerball, options);
        ballWidth = toPxs(options.outWidth);
        ballHeight = toPxs(options.outHeight);


        //used to calculate the height of the nav bar
        int navBar;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navBar = resources.getDimensionPixelSize(resourceId);
        } else {
            navBar = 0;
        }


        //screen pixels
        //height is off so use the equation to make up for the distance of the navigation bar
        screenHeight = getResources().getDisplayMetrics().heightPixels - navBar / 2;
        screenWidth = getResources().getDisplayMetrics().widthPixels;


        Log.i("Nav Bar", navBar + "");
        Log.i("Height Pixels", screenHeight + "");
        Log.i("Width Pixels", screenWidth + "");
        Log.i("Ball Height", ballHeight + "");

        // starting location
        grassfield_x = 0;
        grassfield_y = (int) (screenHeight / 25.0 * 12.0);
        soccerball_x = (int) (screenWidth / 2.0 - ballWidth / 2.0);
        soccerball_y = screenHeight - ballHeight;
        soccergoaltop_x = (int) (screenWidth / 2.0 - goalTopWidth / 2.0);
        soccergoaltop_y = (int) (screenHeight / 25.0 * 12.0) - goalTopHeight;
        soccergoalbottom_x = (int) (screenWidth / 2.0 - goalBottomWidth / 2.0);
        soccergoalbottom_y = (int) (screenHeight / 25.0 * 12.0);


        goalSpeed = 5;  // used for the goal goalSpeed in goalMotion()
        speed = 5; //ball speed used when fling is pressed
        frames_per_second = 40; //frames per second
        theta = 0;    //used to calculate angle when fling is pressed
        prevBallSize = ballHeight; //used in ballMotion() to calculate trajectory
        ballSize = ballHeight;   //used in ballMotion() to calculate trajectory


        frame_time_seconds = 1 / frames_per_second;
        frame_time_ms = frame_time_seconds * 1000;
        frame_time_ns = frame_time_ms * 1000000;
        //1 sec=1,000 ms
        //1 sec=1,000,000,000 ns
        //1 ms=1,000,000 ns
    }

    public double getFrame_time_seconds() {
        return frame_time_seconds;
    }

    //runs once when the class is called bc it implements runnable
    //probably has something to do with super(context)
    @Override
    public void run() {
        //update start time
        last_frameT = System.nanoTime();
        deltaT = 0;


        //continue drawing until canDraw is set to false
        while (canDraw) {
            //while the surface can be reached
            if (surfaceHolder.getSurface().isValid()) {

                canvas = surfaceHolder.lockCanvas();
                goalMotion();
                canvas.drawColor(Color.WHITE);  //clears the canvas with a white background
                //draw bitmaps to canvas
                canvas.drawBitmap(grassfield_bm, grassfield_x, grassfield_y, null);
                canvas.drawBitmap(soccergoaltop_bm, soccergoaltop_x, soccergoaltop_y, null);
                canvas.drawBitmap(soccergoalbottom_bm, soccergoalbottom_x, soccergoalbottom_y, null);
                if(flingPressed)
                {
                    //calculates and displays the trajectory of the ball
                    ballMotion();
                }
                else{
                    //draw the ball bitmap
                    canvas.drawBitmap(soccerball_bm, soccerball_x, soccerball_y, null);
                }


                surfaceHolder.unlockCanvasAndPost(canvas);

                //calculating finish time
                end_of_renderT = System.nanoTime();
                //calculate deltaT (time to wait)
                deltaT = frame_time_ns - (end_of_renderT - last_frameT);

                //stats();

                //try to sleep
                try {
                    if (deltaT > 0) {
                        thread.sleep((long) (deltaT / 1000000));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //update start time
                last_frameT = System.nanoTime();
            }
        }
    }

    //used to pause the thread
    public void pause() {
        //set canDraw to false in order to stop performing run()
        canDraw = false;
        //thread.join may not execute first time so use while(true)
        while (true) {
            try {
                thread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //stop the thread which stops the animation
        thread = null;

    }

    //used to resume the game, called in the beginning of the game
    public void resume() {
        canDraw = true;
        thread = new Thread(this);
        thread.start(); //calls run()
    }

    //used to calculate goal coordinates
    private void goalMotion() {
        //if goal reaches the edge borders switch directions
        if ((soccergoaltop_x + goalTopWidth) >= screenWidth || soccergoaltop_x <= 0) {
            //changes the goalSpeed
            goalSpeed = goalSpeed * -1;
        }
        //update x coordinate of goal
        soccergoaltop_x = soccergoaltop_x + goalSpeed;
        soccergoalbottom_x = soccergoalbottom_x + goalSpeed;
    }

//    //used to calculate ball coordinates
//    private void ballMotion(){
//
//        if (ballSize < 20 || soccerball_y < 0 || soccerball_y > screenHeight){
//
//            //pause the screen for one second
//            try{
//                thread.sleep(1000);
//            }catch(InterruptedException e){
//                e.printStackTrace();
//            }
//
//            //reset ballSize for next fling
//            prevBallSize = ballHeight;
//            ballSize = ballHeight;
//
//            //reset soccerball coordinates
//            soccerball_x = (int) (screenWidth / 2.0 - ballWidth / 2.0);
//            soccerball_y = screenHeight - ballHeight;
//            flingPressed = false;
//            arcReached = true;
//            parabolaX = -1;
//        }
//        else{
//            if(soccerball_y >= grassfield_y){
////                soccerball_y = soccerball_y + prevBallSize/2 - (int)Math.round(Math.sin(theta)*(100*speed)) - ballSize/2;
////                soccerball_x = soccerball_x + prevBallSize/2 + (int)Math.round(Math.cos(theta)*(100*speed)) - ballSize/2;
//                soccerball_y = soccerball_y + prevBallSize/2 - (int)Math.round(Math.sin(theta)*5) - ballSize/2;
//                soccerball_x = soccerball_x + prevBallSize/2 + (int)Math.round(Math.cos(theta)*5) - ballSize/2;
//            }
//            else{
//                soccerball_y += (-5*(Math.pow(parabolaX,2)) + 1) * 10;
//                soccerball_x += 10;
//                parabolaX += 0.1;
//
//                Log.d("X Value: ", soccerball_x + "");
//                Log.d("Y Value: ", soccerball_y + "");
//                Log.d(" ","");
//
//
////                if(soccerball_y >= grassfield_y)
////                {
////                    soccerball_y = 500;
////                }
//            }
//
//
//            //figure out new size of the ball
//            prevBallSize = ballSize;
//            ballSize = (int)Math.round(ballSize * 0.98);
//
//            Bitmap resizedBitmap = Bitmap.createScaledBitmap(soccerball_bm, ballSize, ballSize, false);
//            canvas.drawBitmap(resizedBitmap,soccerball_x,soccerball_y,null);
////            Log.d("Soccer Ball", "X:" + soccerball_x);
////            Log.d("Soccer Ball", "Y:" + soccerball_y);
//        }
//
//    }

    //temporary ball motion
    private void ballMotion() {

        if (soccerball_y <= soccergoaltop_y) {

            //pause the screen for one second
            try{
                thread.sleep(1000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }

            //reset ballSize for next fling
            prevBallSize = ballHeight;
            ballSize = ballHeight;

            //reset soccerball coordinates
            soccerball_x = (int) (screenWidth / 2.0 - ballWidth / 2.0);
            soccerball_y = screenHeight - ballHeight;
            flingPressed = false;

        } else {

                soccerball_y = soccerball_y + prevBallSize/2 - (int)Math.round(Math.sin(theta)* speed) - ballSize/2;
                soccerball_x = soccerball_x + prevBallSize/2 + (int)Math.round(Math.cos(theta)* speed) - ballSize/2;


            //figure out new size of the ball
            prevBallSize = ballSize;
            ballSize = (int)Math.round(ballSize * 0.97);

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(soccerball_bm, ballSize, ballSize, false);
            canvas.drawBitmap(resizedBitmap,soccerball_x,soccerball_y,null);

        }
    }

    //density pixel to local pixel converter
    private int toPxs(int dps) {
        return (int) (dps * density + 0.5f);
    }

    private void stats() {
        Log.d("Frames per second", Double.toString(frames_per_second));
        Log.d("Frame time seconds", Double.toString(frame_time_seconds));
        Log.d("Last Frame Time", Double.toString(last_frameT / 1000000000));
        Log.d("End of Render Time", Double.toString(end_of_renderT / 1000000000));
        Log.d("Time Used Up Seconds", Double.toString((end_of_renderT - last_frameT) / 1000000000));
        Log.d("DeltaT remaining second", Double.toString(deltaT / 1000000000));
        Log.d("---", "-----");
    }
}
