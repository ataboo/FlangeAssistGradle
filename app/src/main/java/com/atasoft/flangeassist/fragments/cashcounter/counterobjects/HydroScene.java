package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.content.Context;
import android.graphics.Bitmap;

import com.atasoft.flangeassist.fragments.cashcounter.CashCounter2;

/**
 * Created by ataboo on 2016-05-17.
 */
public class HydroScene extends CounterScene {
    StaticAnim backAnim;
    RepeatAnim beltAnim;

    private static final String TEXT_ELECTRIC = "electric_textures";
    private static final String TEXT_SALMON = "salmon_textures";
    private static final String TEXT_WATER = "water_textures";

    private Bitmap[] electricTextures;
    private Bitmap[] salmonTextures;
    private Bitmap[] waterTextures;


    public HydroScene(Context context, IntVector screenSize) {
        this.context = context;

        scene = Scene.HYDRO_DAM;

        sceneRatio = 720f/1024f;

        initTextures();
        backAnim = new StaticAnim(backgroundTexture);
        beltAnim = new RepeatAnim(waterTextures, 50, new AtaVector(0, 510f / 720f), new AtaVector(460f / 1024f, 160f / 720f));
        earningText = new EarningText(new AtaVector(0.5f, 0.20f), 0f, CashCounter2.EarningType.OFF_SHIFT);

        animations.add(backAnim);
        animations.add(beltAnim);
        animations.add(earningText);

        screenResize(screenSize);
    }

    private void initTextures() {
        // Stored textures in MainActivity to prevent reloading on screen size change.
        if (!textureBox.contains(TEXT_ELECTRIC)) {
            textureBox.dispose();
            textureBox.addTextureSeries(TEXT_ELECTRIC, getBitmapsFromSeries(context, "counter_anim/hydro_electric", "Electric_"));
            textureBox.addTextureSeries(TEXT_SALMON, getBitmapsFromSeries(context, "counter_anim/hydro_salmon", "Salmon_"));
            textureBox.addTextureSeries(TEXT_WATER, getBitmapsFromSeries(context, "counter_anim/hydro_water", "Water_"));
        }
        waterTextures = textureBox.getTextureSet(TEXT_WATER);
        salmonTextures = textureBox.getTextureSet(TEXT_SALMON);
        electricTextures = textureBox.getTextureSet(TEXT_ELECTRIC);

        backgroundTexture = getBitmapFromAssets(context, "counter_anim/HydroDamBackground.png");
    }

    @Override
    public void addFineAnim(long startTime, float earnings, CashCounter2.EarningType earningType) {
        super.addFineAnim(startTime, earnings, earningType);

        OnceAnimation electricAnim = new OnceAnimation(electricTextures, 50, new AtaVector(760f/1024f, 400f/720f),
                new AtaVector(264f/1024f, 204f/720f), startTime - 300);

        electricAnim.resize(sceneRect);

        OnceText electricText = new OnceText(String.format("%d¢", getCents(earnings)), new AtaVector(0.74f, 0.68f), new AtaVector(0.87f, 0.30f), startTime + 100, 2000);
        electricText.resize(sceneRect);

        animations.add(electricAnim);
        animations.add(electricText);
    }

    @Override
    public void addCoarseAnim(long startTime, float earnings) {
        OnceAnimation salmonAnim = new OnceAnimation(salmonTextures, 70, new AtaVector(0f, 0f),
                new AtaVector(436f / 1024f, 642f / 720f), startTime);
        salmonAnim.resize(sceneRect);
        animations.add(salmonAnim);
    }


}
