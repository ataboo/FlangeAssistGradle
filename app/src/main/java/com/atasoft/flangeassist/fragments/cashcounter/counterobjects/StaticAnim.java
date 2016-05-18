package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.atasoft.utilities.AtaSprite;
import com.atasoft.utilities.IntVector;

/**
 * Created by ataboo on 2016-05-14.
 */
public class StaticAnim extends CounterAnim {
    private AtaSprite sprite;

    public StaticAnim(Bitmap texture){
        this.sprite = new AtaSprite(texture);
    }

    @Override
    public void draw(long timeMillis, Canvas canvas, Paint paint) {
        if(sprite.texture.isRecycled()){
            return;
        }
        sprite.draw(canvas, paint);
    }

    @Override
    public void resize(Rect sceneRect) {
        sprite.position = new IntVector(sceneRect.left, sceneRect.top);
        sprite.size = new IntVector(sceneRect.width(), sceneRect.height());
        //sprite.size = screenSize;
    }

    @Override
    public void dispose() {
        sprite.dispose();
    }
}
