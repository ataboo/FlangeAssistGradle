package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.atasoft.utilities.AtaMathUtils;
import com.atasoft.utilities.AtaSprite;
import com.atasoft.utilities.AtaVector;
import com.atasoft.utilities.IntVector;

/**
 * Created by ataboo on 2016-05-15.
 */
public class OnceAnimation extends CounterAnim {
    private Bitmap[] textures;
    private int frameLength;
    private AtaVector posFactor;
    private AtaVector sizeFactor;
    private long startTime;
    private AtaSprite sprite;

    public OnceAnimation(Bitmap[] textures, int frameLength, AtaVector posFactor, AtaVector sizeFactor, long startTime){
        this.textures = textures;
        this.frameLength = frameLength;
        this.posFactor = posFactor;
        this.sizeFactor = sizeFactor;
        this.startTime = startTime;
        sprite = new AtaSprite(textures[0]);
    }

    @Override
    public void draw(long timeMillis, Canvas canvas, Paint paint) {
        if(timeMillis < startTime || isDone){
            return;
        }

        if(timeMillis > startTime + textures.length * frameLength){
            dispose();
        }

        int frameIndex = (int) (timeMillis - startTime) / frameLength;

        frameIndex = AtaMathUtils.clampInt(frameIndex, 0, textures.length - 1);

        sprite.texture = textures[frameIndex];

        if(sprite.texture.isRecycled()){
            return;
        }

        sprite.draw(canvas, paint);
    }

    @Override
    public void resize(Rect sceneRect) {
        sprite.size = new IntVector(sceneRect.width() * sizeFactor.x, sceneRect.height() * sizeFactor.y, IntVector.RoundMode.ROUND);
        sprite.position = new IntVector(sceneRect.width() * posFactor.x + sceneRect.left,
                sceneRect.top + sceneRect.height() * posFactor.y, IntVector.RoundMode.ROUND);
    }

    @Override
    public void dispose() {
        isDone = true;
    }

    public int getDuration(){
        return textures.length * frameLength;
    }

    public void setStartTime(long startTime){
        this.startTime = startTime;
    }
}
