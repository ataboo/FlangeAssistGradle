package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by ataboo on 2016-05-14.
 */
public abstract class CounterAnim {
    public boolean isDone = false;

    public abstract void draw(long timeMillis, Canvas canvas, Paint paint);

    public abstract void resize(Rect sceneRect);

    public abstract void dispose();

    public int nextIndex(Object[] arr, int current){
        return (current >= arr.length - 1) ? 0 : current + 1;
    }

    protected IntVector pointFromFactor(Rect sceneRect, AtaVector factor){
        return new IntVector(sceneRect.left + sceneRect.width() * factor.x,
                sceneRect.top + sceneRect.height() * factor.y);
    }
}
