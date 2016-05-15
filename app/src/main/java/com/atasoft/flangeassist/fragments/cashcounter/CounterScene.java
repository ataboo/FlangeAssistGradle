package com.atasoft.flangeassist.fragments.cashcounter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.CounterAnim;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.IntVector;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by ataboo on 2016-05-14.
 */
public abstract class CounterScene {
    public enum Scene{
        PULP_MILL;

        public CounterScene makeScene(Context context, IntVector screenSize){
            switch (this){
                default: case PULP_MILL:
                    return new PulpMillScene(context, screenSize);
            }
        }
    }

    protected ArrayList<CounterAnim> animations = new ArrayList<>();

    public abstract void addFineAnim(long startTime, float earnings);

    public abstract void addCoarseAnim(long startTime, float earnings);

    public abstract void screenResize(IntVector size);

    public abstract void draw(long time, Canvas canvas, Paint paint);

    public abstract void dispose();

    protected void disposeTextures(Bitmap[] bitmaps){
        for(Bitmap bitmap: bitmaps){
            bitmap.recycle();
        }
    }

    protected static Bitmap[] getBitmapsFromSeries(Context context, String dirPath, String rootName) {
        AssetManager assetManager = context.getAssets();

        ArrayList<Bitmap> bitmapList = new ArrayList<>();

        try {
            String[] files = assetManager.list(dirPath);
            for (String file: files) {
                if(!file.contains(rootName)){
                    Log.w("AtaSprite", String.format("File: %s in %s does not contain rootName: %s", file, dirPath, rootName));
                    continue;
                }

                Log.w("AtaSprite", "Opening: " + file);

                Bitmap texture = getBitmapFromAssets(assetManager, dirPath + "/" + file);

                if(texture != null){
                    bitmapList.add(texture);
                } else {
                    Log.e("AtaSprite", "Returned null when attempting to open " + dirPath + file);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        return bitmapList.toArray(new Bitmap[bitmapList.size()]);
    }

    protected static Bitmap getBitmapFromAssets(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        Bitmap bitmap = null;
        try{
            bitmap = getBitmapFromAssets(assetManager, filePath);
        } catch (IOException e){
            e.printStackTrace();
        }

        return bitmap;
    }

    protected static Bitmap getBitmapFromAssets(AssetManager assetManager, String filePath) throws IOException{
        InputStream istr;
        Bitmap bitmap = null;

        istr = assetManager.open(filePath);
        bitmap = BitmapFactory.decodeStream(istr);

        return bitmap;
    }
}
