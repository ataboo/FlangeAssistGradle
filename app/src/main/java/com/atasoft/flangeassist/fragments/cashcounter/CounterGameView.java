package com.atasoft.flangeassist.fragments.cashcounter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.format.Time;
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
    private long nextFineAnim = -1;
    private final int fineAnimPeriod = 2000;
    private long nextCoarseAnim = -1;
    private final int coarseAnimPeriod = 5000;
    private float earnings = 0f;
    private CashCounter2.EarningType earningType = CashCounter2.EarningType.OFF_SHIFT;
    private CashCounterData cashCounterData = new CashCounterData();
    Thread gameThread;

    CounterScene activeScene;

    public CounterGameView(Context context) {
        super(context);

        surfaceHolder = getHolder();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        activeScene = CounterScene.Scene.OIL_DRIP.makeScene(context, new IntVector(getWidth(), getHeight()));
        updateEarnings();
        activeScene.setEarnings(earnings, earningType);
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

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        activeScene.screenResize(new IntVector(w, h));

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

    public void destroy(){
        activeScene.dispose();
    }

    /*
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
    */

    private void update() {
        long time = System.currentTimeMillis();

        if(time > nextFineAnim){
            nextFineAnim = time + fineAnimPeriod;

            updateEarnings();

            activeScene.addFineAnim(nextFineAnim, earnings, earningType);
        }

        if(time > nextCoarseAnim){
            nextCoarseAnim = time + coarseAnimPeriod;
            activeScene.addCoarseAnim(nextCoarseAnim, 0);
        }

        activeScene.update();
    }

    private void updateEarnings(){
        Time now = new Time();
        now.setToNow();
        CashCounterData.EarningAttributes attributes = cashCounterData.new EarningAttributes(new int[]{12, 0}, new float[]{8f, 2f, 2f}, 40f, false, false, true, false);
        CashCounterData.EarningsReturn earningsRet = cashCounterData.getEarnings(now, attributes);
        earnings = (float) earningsRet.earnings;
        earningType = earningsRet.earningType;
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = surfaceHolder.lockCanvas();

            // Draw the background color
            //canvas.drawColor(Color.argb(255, 26, 128, 182));
            canvas.drawColor(Color.DKGRAY);
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
