package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.content.Context;
import android.graphics.Bitmap;

import com.atasoft.flangeassist.fragments.cashcounter.CashCounter2;

/**
 * Created by ataboo on 2016-05-14.
 */
public class PulpMillScene extends CounterScene {
    Bitmap backgroundTexture;

    StaticAnim backAnim;
    RepeatAnim beltAnim;

    private static final String TEXT_ROLL = "roll_textures";
    private static final String TEXT_TIMBER = "timber_textures";
    private static final String TEXT_BELT = "belt_textures";

    private Bitmap[] rollTextures;
    private Bitmap[] beltTextures;
    private Bitmap[] timberTextures;



    public PulpMillScene(Context context, IntVector screenSize) {
        this.context = context;

        sceneRatio = 720f/1024f;

        initTextures();
        backAnim = new StaticAnim(backgroundTexture);
        beltAnim = new RepeatAnim(beltTextures, 50, new AtaVector(0, 510f / 720f), new AtaVector(460f / 1024f, 160f / 720f));
        earningText = new EarningText(new AtaVector(0.5f, 0.20f), 0f, CashCounter2.EarningType.STRAIGHT_TIME);

        animations.add(backAnim);
        animations.add(beltAnim);
        animations.add(earningText);

        screenResize(screenSize);
    }

    private void initTextures() {
        // Stored textures in MainActivity to prevent reloading on screen size change.
        if (!textureBox.contains(TEXT_BELT)) {
            textureBox.dispose();
            textureBox.addTextureSeries(TEXT_BELT, getBitmapsFromSeries(context, "counter_anim/pulp_belt", "Belt_"));
            textureBox.addTextureSeries(TEXT_ROLL, getBitmapsFromSeries(context, "counter_anim/pulp_roll", "Roll_"));
            textureBox.addTextureSeries(TEXT_TIMBER, getBitmapsFromSeries(context, "counter_anim/pulp_timber", "Timber_"));
        }
        beltTextures = textureBox.getTextureSet(TEXT_BELT);
        rollTextures = textureBox.getTextureSet(TEXT_ROLL);
        timberTextures = textureBox.getTextureSet(TEXT_TIMBER);

        backgroundTexture = getBitmapFromAssets(context, "counter_anim/PulpMillBackground.png");
    }

    @Override
    public void addFineAnim(long startTime, float earnings, CashCounter2.EarningType earningType) {
        super.addFineAnim(startTime, earnings, earningType);

        OnceAnimation rollAnim = new OnceAnimation(rollTextures, 50, new AtaVector(760f/1024f, 400f/720f),
                new AtaVector(264f/1024f, 204f/720f), startTime - 300);

        rollAnim.resize(sceneRect);




        OnceText rollText = new OnceText(String.format("%d¢", getCents(earnings)), new AtaVector(0.74f, 0.68f), new AtaVector(0.87f, 0.30f), startTime + 100, 2000);
        rollText.resize(sceneRect);

        animations.add(rollAnim);
        animations.add(rollText);
    }

    @Override
    public void addCoarseAnim(long startTime, float earnings) {
        OnceAnimation timberAnim = new OnceAnimation(timberTextures, 70, new AtaVector(0f, 0f),
                new AtaVector(436f / 1024f, 642f / 720f), startTime);
        timberAnim.resize(sceneRect);
        animations.add(timberAnim);
    }

    @Override
    public void dispose() {
        for (CounterAnim anim : animations) {
            anim.dispose();
        }

        backgroundTexture.recycle();
    }
}
