package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by ataboo on 2016-05-14.
 */
public class StaticAnim extends CounterAnim {
    private AtaSprite ataSprite;

    public StaticAnim(Bitmap texture, IntVector screenSize){
        this.ataSprite = new AtaSprite(texture);
        resize(screenSize);
    }

    @Override
    public void draw(long timeMillis, Canvas canvas, Paint paint) {
        ataSprite.draw(canvas, paint);
    }

    @Override
    public void resize(IntVector screenSize) {
        //ataSprite.size = screenSize;
    }

    @Override
    public void dispose() {
        ataSprite.dispose();
    }
}
