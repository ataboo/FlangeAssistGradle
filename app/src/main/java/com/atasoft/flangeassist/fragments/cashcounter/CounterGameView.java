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
import com.atasoft.flangeassist.fragments.cashcounter.scenes.CounterScene;
import com.atasoft.utilities.IntVector;
import com.atasoft.utilities.ShiftPickerPreference;
import com.atasoft.utilities.TimePickerPreference;
import com.atasoft.utilities.AtaMathUtils;

/**
 * Created by ataboo on 2016-05-14.
 */
public class CounterGameView extends SurfaceView implements Runnable {
    public enum PrefKey {
        SHIFT_START_PICKER(R.string.counter_time_picker_key),
        SHIFT_HOURS_PICKER(R.string.counter_shift_picker_key),
        WEEKEND_DOUBLE(R.string.counter_weekend_key),
        FOUR_TENS(R.string.counter_fourtens_key),
        NIGHT_SHIFT(R.string.counter_night_key),
        HOLIDAY(R.string.counter_holiday_key),
        WAGE_RATE(R.string.counter_wage_key);

        public int stringRes;

        PrefKey(int res) {
            this.stringRes = res;
        }

        public String getString(Resources resources) {
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
    private CashCounter.EarningType earningType = CashCounter.EarningType.OFF_SHIFT;
    private CashCounterData cashCounterData = new CashCounterData();
    private CashCounterData.EarningAttributes earningAttributes;

    Thread gameThread;
    CounterScene activeScene;
    SharedPreferences prefs;

    public CounterGameView(Context context) {
        super(context);

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        surfaceHolder = getHolder();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void resetScene() {
        String prefScene = prefs.getString(getResources().getString(R.string.counter_scene_key), CounterScene.Scene.OIL_DRIP.name);
        CounterScene.Scene newScene = CounterScene.Scene.getSceneFromName(prefScene);

        if (activeScene == null) {
            activeScene = newScene.makeScene(getContext(), new IntVector(getWidth(), getHeight()));
        } else {
            if(activeScene.scene != newScene) {
                activeScene.dispose();
                activeScene = newScene.makeScene(getContext(), new IntVector(getWidth(), getHeight()));
            }
        }

        updateEarnings(-fineAnimPeriod);
        activeScene.setEarnings(earnings, earningType);
        nextFineAnim = System.currentTimeMillis() + 100;
        nextCoarseAnim = System.currentTimeMillis() + 100;
    }

    @Override
    public void run() {

        while (playing) {
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

        recallSettings();

        resetScene();

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void destroy() {
        if(activeScene != null) {
            activeScene.dispose();
        }
    }

    private void update() {
        long time = System.currentTimeMillis();

        if (time > nextFineAnim) {
            nextFineAnim = time + fineAnimPeriod;

            updateEarnings(0);

            if (earningType == CashCounter.EarningType.OFF_SHIFT) {
                return;
            }

            activeScene.addFineAnim(nextFineAnim, earnings, earningType);
        }

        if (time > nextCoarseAnim) {
            nextCoarseAnim = time + coarseAnimPeriod;
            activeScene.addCoarseAnim(nextCoarseAnim, 0);
        }

        activeScene.update();
    }

    private void recallSettings(){
        //shift start
        //shift hours
        //weekend double
        //holiday double
        //four tens
        //wage rate

        Resources resources = getResources();

        String shiftStartString = prefs.getString(resources.getString(R.string.counter_time_picker_key), "8,0");
        int[] shiftStart = TimePickerPreference.getTimeFromPref(shiftStartString);

        String shiftHoursString = prefs.getString(resources.getString(R.string.counter_shift_picker_key), "8,2,0");
        float[] shiftHours = ShiftPickerPreference.parseHours(shiftHoursString);

        boolean weekendDouble = prefs.getBoolean(resources.getString(R.string.counter_weekend_key), true);

        boolean isHoliday = prefs.getBoolean(resources.getString(R.string.counter_holiday_key), false);

        float wageRate = 40;
        try {
            wageRate = Float.parseFloat(prefs.getString(resources.getString(R.string.counter_wage_key), "40"));
        } catch (NumberFormatException e){
            e.printStackTrace();
        }

        earningAttributes = cashCounterData.new EarningAttributes(shiftStart, shiftHours,
                wageRate, isHoliday, false, weekendDouble, false);
    }

    private void updateEarnings(int offset) {
        Time now = new Time();
        now.setToNow();

        if(offset != 0) {
            long millis = now.toMillis(false);
            now.set(millis + offset);
        }

        CashCounterData.EarningsReturn earningsRet = cashCounterData.getEarnings(now, earningAttributes);
        earnings = (float) earningsRet.earnings;
        earningType = earningsRet.earningType;
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            canvas.drawColor(Color.DKGRAY);

            // Display the current fps on the screen
            //canvas.drawText("FPS:" + fps, 20, 40, paint);

            activeScene.draw(System.currentTimeMillis(), canvas, paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
}
