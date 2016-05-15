package com.atasoft.flangeassist.fragments.cashcounter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.IntVector;

/**
 * Created by ataboo on 2016-05-14.
 */
public class CounterGameView extends SurfaceView implements Runnable {
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Paint paint = new Paint();
    private long timeThisFrame;
    private long fps;
    private boolean playing;
    Thread gameThread;

    CounterScene activeScene;

    public CounterGameView(Context context) {
        super(context);

        surfaceHolder = getHolder();

        activeScene = CounterScene.Scene.PULP_MILL.makeScene(context, new IntVector(getWidth(), getHeight()));


    }

    @Override
    public void run() {
        while(playing) {
            long startFrameTime = System.currentTimeMillis();


            update();
            draw();

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }



    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void resize(){
        int screenWidth = getWidth();
        int screenHeight = getHeight();

        if(screenWidth == 0){
            return;
        }

        if (screenHeight > screenWidth){
            screenHeight = (int) (screenWidth * 720f / 1024f);
        } else {
            screenWidth = (int) (screenHeight * 1024f / 720f);
        }

        activeScene.screenResize(new IntVector(screenWidth, screenHeight));
    }

    public void resize(IntVector size){
        activeScene.screenResize(size);
    }

    private void update() {

    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = surfaceHolder.lockCanvas();

            resize(new IntVector(canvas.getWidth(), canvas.getHeight()));

            // Draw the background color
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Choose the brush color for drawing
            paint.setColor(Color.argb(255, 249, 129, 0));

            // Make the text a bit bigger
            paint.setTextSize(45);

            // Display the current fps on the screen
            canvas.drawText("FPS:" + fps, 20, 40, paint);

            activeScene.draw(System.currentTimeMillis(), canvas, paint);


            // Draw bob at bobXPosition, 200 pixels
            //canvas.drawBitmap(bitmapBob, bobXPosition, 200, paint);

            // New drawing code goes here

            // Draw everything to the screen
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
}
