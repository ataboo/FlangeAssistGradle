package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


/**
 * Created by ataboo on 2016-05-14.
 */
public class AtaSprite {
    public Bitmap texture;
    public Rect textureSize;
    public IntVector size = new IntVector(800, 600);
    public IntVector position = IntVector.zero();

    public AtaSprite(Bitmap texture){
        if(texture == null){
            throw new Error("AtaSprite can not be passed a null texture.");
        }

        this.texture = texture;
        textureSize = new Rect(0,0, texture.getWidth(), texture.getHeight());
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(texture, textureSize, new Rect(position.x, position.y, position.x + size.x, position.y + size.y), paint);
    }

    public void dispose(){
        texture = null;
    }
}
