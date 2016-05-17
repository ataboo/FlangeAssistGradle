package com.atasoft.flangeassist.fragments.cashcounter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.atasoft.flangeassist.R;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.CounterScene;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.IntVector;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.ShiftPickerPreference;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.TimePickerPreference;
import com.atasoft.flangeassist.fragments.ropevalues.RopeFragment;
import com.atasoft.helpers.AtaMathUtils;

/**
 * Created by ataboo on 2016-05-14.
 */
public class CounterGameView extends SurfaceView implements Runnable {
    public enum PrefKey{
        SHIFT_START_PICKER(R.string.counter_time_picker_key),
        SHIFT_HOURS_PICKER(R.string.counter_shift_picker_key),
        WEEKEND_DOUBLE(R.string.counter_weekend_key),
        FOUR_TENS(R.string.counter_fourtens_key),
        NIGHT_SHIFT(R.string.counter_night_key),
        HOLIDAY(R.string.counter_holiday_key),
        WAGE_RATE(R.string.counter_wage_key);

        public int stringRes;

        PrefKey(int res){
            this.stringRes = res;
        }

        public String getString(Resources resources){
            return resources.getString(stringRes);
        }
    }

    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Paint paint = new Paint();
    private long timeThisFrame;
    private long fps;
    private boolean playing;
    private long nextFineAnim = -1;
    private final int fineAnimPeriod = 2000;
    private long nextCoarseAnim = -1;
    private final int coarseAnimPeriod = 10000;
    private float earnings = 0f;
    private CashCounter2.EarningType earningType = CashCounter2.EarningType.OFF_SHIFT;
    private CashCounterData cashCounterData = new CashCounterData();

    Thread gameThread;
    CounterScene activeScene;
    SharedPreferences prefs;

    public CounterGameView(Context context) {
        super(context);

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        surfaceHolder = getHolder();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.DEFAULT_BOLD);

        resetScene();
    }

    public void resetScene(){
        String prefScene = prefs.getString(getResources().getString(R.string.counter_scene_key), CounterScene.Scene.OIL_DRIP.name);
        CounterScene.Scene newScene = CounterScene.Scene.getSceneFromName(prefScene);

        if(activeScene != null) {
            if (activeScene.scene == newScene) {
                return;
            } else {
                activeScene.dispose();
            }
        }

        activeScene = newScene.makeScene(getContext(), new IntVector(getWidth(), getHeight()));

        updateEarnings();
        activeScene.setEarnings(earnings, earningType);
        nextFineAnim = System.currentTimeMillis() + fineAnimPeriod - 200;
        nextCoarseAnim = System.currentTimeMillis() + coarseAnimPeriod - 200;
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

        resetScene();

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void destroy(){
        activeScene.dispose();
    }

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

        Resources resources = getResources();
        String shiftStartString = prefs.getString(PrefKey.SHIFT_START_PICKER.getString(resources), "8,0");
        int[] shiftStart = new int[]{TimePickerPreference.getHourFromPref(shiftStartString), TimePickerPreference.getMinFromPref(shiftStartString)};

        String shiftHoursString = prefs.getString(PrefKey.SHIFT_HOURS_PICKER.getString(resources), "8,2,0");
        float[] shiftHours = ShiftPickerPreference.parseHours(shiftHoursString);

        boolean fourTens = prefs.getBoolean(PrefKey.FOUR_TENS.getString(resources), false);
        boolean weekendDouble = prefs.getBoolean(PrefKey.WEEKEND_DOUBLE.getString(resources), true);
        boolean isHoliday = prefs.getBoolean(PrefKey.HOLIDAY.getString(resources), false);
        boolean nightShift = prefs.getBoolean(PrefKey.NIGHT_SHIFT.getString(resources), false);

        String wageRateString = prefs.getString(PrefKey.WAGE_RATE.getString(resources), "40");

        float wageRate;
        try{
            wageRate = AtaMathUtils.clampFloat(Float.parseFloat(wageRateString), 0f, 500f);
        } catch (NumberFormatException e){
            wageRate = 40f;
        }

        CashCounterData.EarningAttributes attributes = cashCounterData.new EarningAttributes(shiftStart,
                shiftHours, wageRate, isHoliday, fourTens, weekendDouble, nightShift);
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
