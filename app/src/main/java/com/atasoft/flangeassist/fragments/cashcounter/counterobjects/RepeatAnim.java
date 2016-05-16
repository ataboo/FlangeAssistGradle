package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by ataboo on 2016-05-14.
 */
public class RepeatAnim extends CounterAnim {

    public AtaSprite sprite;
    private long lastFrame = -1;
    private Bitmap[] textures;
    private int frameIndex;
    private int frameLength;
    private AtaVector posFactor;
    private AtaVector sizeFactor;

    public RepeatAnim(Bitmap[] textures, int frameLength, AtaVector posFactor, AtaVector sizeFactor){
        this.textures = textures;
        this.posFactor = posFactor;
        this.sizeFactor = sizeFactor;
        this.frameIndex = textures.length - 1;
        this.frameLength = frameLength;
        this.sprite = new AtaSprite(textures[frameIndex]);
    }

    @Override
    public void draw(long timeMillis, Canvas canvas, Paint paint) {
        if (lastFrame < 0){
            lastFrame = timeMillis;
        }

        if(timeMillis > lastFrame + frameLength){
            frameIndex = nextIndex(textures, frameIndex);
            lastFrame += frameLength;
        }

        sprite.texture = textures[frameIndex];

        if(sprite.texture.isRecycled()){
            return;
        }

        sprite.draw(canvas, paint);

    }

    @Override
    public void resize(Rect sceneRect){
        sprite.size = new IntVector(sceneRect.width() * sizeFactor.x, sceneRect.height() * sizeFactor.y, IntVector.RoundMode.ROUND);
        sprite.position = new IntVector(sceneRect.width() * posFactor.x + sceneRect.left,
                sceneRect.top + sceneRect.height() * posFactor.y, IntVector.RoundMode.ROUND);
    }



    @Override
    public void dispose() {

    }
}
