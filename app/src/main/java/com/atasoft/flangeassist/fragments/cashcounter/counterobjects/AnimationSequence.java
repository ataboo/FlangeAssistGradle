package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.animation.Animation;

/**
 * Created by ataboo on 2016-05-17.
 */
public class AnimationSequence extends CounterAnim {
    private OnceAnimation[] animations;
    private int fullDuration = 0;
    private long startTime;

    public AnimationSequence(OnceAnimation[] animations, long startTime){
        this.animations = animations;
        this.startTime = startTime;

        for(OnceAnimation anim : animations){
            anim.setStartTime(startTime + fullDuration);
            fullDuration += anim.getDuration();
        }

    }

    @Override
    public void draw(long timeMillis, Canvas canvas, Paint paint) {
        if(timeMillis < startTime || isDone){
            return;
        }

        if(timeMillis > fullDuration + startTime){
            dispose();
            return;
        }

        for(OnceAnimation anim: animations){
            draw(timeMillis, canvas,paint);
        }
    }

    @Override
    public void resize(Rect sceneRect) {
        for(CounterAnim anim: animations){
            anim.resize(sceneRect);
        }
    }

    @Override
    public void dispose() {
        for(CounterAnim anim: animations){
            anim.dispose();
        }
        isDone = true;
    }
}
