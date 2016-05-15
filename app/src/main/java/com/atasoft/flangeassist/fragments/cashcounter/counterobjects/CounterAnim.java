package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by ataboo on 2016-05-14.
 */
public abstract class CounterAnim {
    public abstract void draw(long timeMillis, Canvas canvas, Paint paint);

    public abstract void resize(IntVector screenSize);

    public abstract void dispose();

    public int nextIndex(Object[] arr, int current){
        return (current >= arr.length - 1) ? 0 : current + 1;
    }
}
