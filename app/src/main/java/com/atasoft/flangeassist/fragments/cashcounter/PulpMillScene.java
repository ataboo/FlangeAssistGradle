package com.atasoft.flangeassist.fragments.cashcounter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.AtaVector;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.CounterAnim;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.IntVector;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.RepeatAnim;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.StaticAnim;

/**
 * Created by ataboo on 2016-05-14.
 */
public class PulpMillScene extends CounterScene {
    private Context context;

    Bitmap[] rollTextures;
    Bitmap[] beltTextures;
    Bitmap[] timberTextures;
    Bitmap backgroundTexture;

    StaticAnim backAnim;
    RepeatAnim beltAnim;

    public PulpMillScene(Context context, IntVector screenSize){
        this.context = context;

        initTextures();

        backAnim = new StaticAnim(backgroundTexture, screenSize);
        beltAnim = new RepeatAnim(beltTextures, 50, new AtaVector(0, 510f/720f), new AtaVector(460f/1024f, 160f/720f), screenSize);

        animations.add(backAnim);
        animations.add(beltAnim);

        screenResize(screenSize);

    }

    private void initTextures(){
        //rollTextures = getBitmapsFromSeries(context, "counter_anim/pulp_roll", "Roll_");
        beltTextures = getBitmapsFromSeries(context, "counter_anim/pulp_belt", "Belt_");
       // timberTextures = getBitmapsFromSeries(context, "counter_anim/pulp_timber", "Timber_");
        backgroundTexture = getBitmapFromAssets(context, "counter_anim/PulpMillBackground.png");
    }


    @Override
    public void addFineAnim(long startTime, float earnings) {

    }

    @Override
    public void addCoarseAnim(long startTime, float earnings) {

    }

    @Override
    public void screenResize(IntVector size) {
        for(CounterAnim anim: animations){
            anim.resize(size);
        }
    }

    @Override
    public void draw(long time, Canvas canvas, Paint paint) {
        for(CounterAnim anim: animations){
            anim.draw(time, canvas, paint);
        }
    }

    @Override
    public void dispose(){
        for(CounterAnim anim: animations){
            anim.dispose();
        }

        disposeTextures(rollTextures);
        disposeTextures(beltTextures);
        disposeTextures(timberTextures);
        backgroundTexture.recycle();
    }
}
