package com.atasoft.flangeassist.fragments.cashcounter.scenes;

import android.content.Context;
import android.graphics.Bitmap;

import com.atasoft.flangeassist.fragments.cashcounter.CashCounter2;
import com.atasoft.utilities.AtaVector;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.EarningText;
import com.atasoft.utilities.IntVector;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.OnceAnimation;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.OnceText;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.StaticAnim;

/**
 * Created by ataboo on 2016-05-15.
 */
public class OilDripScene extends CounterScene {
    Bitmap backgroundTexture;
    StaticAnim backAnim;

    private static final String TEXT_DRIP = "drip_textures";

    private Bitmap[] dripTextures;

    public OilDripScene(Context context, IntVector screenSize){
        this.context = context;
        sceneRatio = 600f/800f;
        scene = Scene.OIL_DRIP;

        initTextures();
        backAnim = new StaticAnim(backgroundTexture);
        earningText = new EarningText(new AtaVector(0.4f, 0.45f), 0f, CashCounter2.EarningType.OFF_SHIFT);

        animations.add(backAnim);
        animations.add(earningText);

        screenResize(screenSize);
    }

    private void initTextures(){
        // Stored textures in MainActivity to prevent reloading on screen size change.
        if (!textureBox.contains(TEXT_DRIP)) {
            textureBox.dispose();
            textureBox.addTextureSeries(TEXT_DRIP, getBitmapsFromSeries(context, "counter_anim/oil_drip", "OilDrip_"));
        }
        dripTextures = textureBox.getTextureSet(TEXT_DRIP);

        backgroundTexture = getBitmapFromAssets(context, "counter_anim/OilDripBackground.png");
    }

    @Override
    public void addFineAnim(long startTime, float earnings, CashCounter2.EarningType earningType) {
        super.addFineAnim(startTime, earnings, earningType);

        OnceAnimation dripAnim = new OnceAnimation(dripTextures, 36, new AtaVector(524f/800f, 250f/600f),
                new AtaVector(136f/800f, 350f/600f), startTime - 1200);
        dripAnim.resize(sceneRect);

        OnceText dripText = new OnceText(String.format("%d¢", getCents(earnings)), new AtaVector(0.75f, 0.95f),
                new AtaVector(0.75f, 0.7f), startTime - 200, 1200);
        dripText.resize(sceneRect);

        animations.add(dripText);
        animations.add(dripAnim);

    }

    @Override
    public void addCoarseAnim(long startTime, float earnings) {

    }
}
