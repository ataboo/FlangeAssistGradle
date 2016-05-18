package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.atasoft.utilities.AtaMathUtils;
import com.atasoft.utilities.AtaVector;
import com.atasoft.utilities.IntVector;

/**
 * Created by ataboo on 2016-05-15.
 */
public class OnceText extends CounterAnim {
    private static final float FADE_LENGTH = 0.25f;
    private static final float START_FONT = 0.3f;
    private static final float FONT_SIZE = 72f;
    private String text;
    private AtaVector startFactor;

    private AtaVector endFactor;
    private IntVector startPoint = IntVector.zero();
    private IntVector endPoint = IntVector.zero();
    private long startTime;
    private int duration;


    public OnceText(String text, AtaVector startFactor, AtaVector endFactor, long startTime,
                    int duration){
        this.text = text;
        this.startFactor = startFactor;
        this.endFactor = endFactor;
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public void draw(long timeMillis, Canvas canvas, Paint paint) {
        if(timeMillis < startTime || isDone){
            return;
        }

        if(timeMillis > startTime + duration){
            isDone = true;
            return;
        }

        float progress = (float)(timeMillis - startTime) / (float) duration;
        progress = AtaMathUtils.clampFloat(progress, 0, 1);

        float opacity = 1f;
        if(progress < FADE_LENGTH){
            opacity = AtaMathUtils.lerpFloat(0f, 1f, progress / FADE_LENGTH);
        }

        if(progress > 1f - FADE_LENGTH){
            opacity = AtaMathUtils.lerpFloat(0f, 1f, (1 - progress) / FADE_LENGTH);
        }

        IntVector drawPoint = startPoint.lerpTowards(endPoint, progress);
        paint.setTextSize(AtaMathUtils.lerpFloat(FONT_SIZE * START_FONT, FONT_SIZE, progress));

        paint.setAlpha((int)(opacity * 255));
        canvas.drawText(text, drawPoint.x, drawPoint.y, paint);
        paint.setAlpha(255);
    }

    @Override
    public void resize(Rect sceneRect) {
        this.startPoint = pointFromFactor(sceneRect, startFactor);
        this.endPoint = pointFromFactor(sceneRect, endFactor);
    }

    @Override
    public void dispose() {

    }
}
