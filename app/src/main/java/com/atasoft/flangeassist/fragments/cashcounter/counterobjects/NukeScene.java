package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.content.Context;
import android.graphics.Bitmap;

import com.atasoft.flangeassist.fragments.cashcounter.CashCounter2;

/**
 * Created by ataboo on 2016-05-17.
 */
public class NukeScene extends CounterScene {
    private Context context;

    private static final String TEXT_ELECTRIC = "nuke_electric_textures";
    private static final String TEXT_SMOKE = "nuke_smoke_textures";
    private static final String TEXT_ROCK = "nuke_rock_textures";
    private static final String TEXT_BLAST = "nuke_blast_textures";

    private Bitmap[] electricTextures;
    private Bitmap[] smokeTextures;
    private Bitmap[] rockTextures;
    private Bitmap[] blastTextures;

    public NukeScene(Context context, IntVector screenSize){
        this.context = context;
        scene = Scene.NUKE_PLANT;

        sceneRatio = 720f/1024f;

        initTextures();

        //make animations

        screenResize(screenSize);
    }

    private void initTextures(){
        // Stored textures in MainActivity to prevent reloading on screen size change.
        if (!textureBox.contains(TEXT_ELECTRIC)) {
            textureBox.dispose();
            textureBox.addTextureSeries(TEXT_ELECTRIC, getBitmapsFromSeries(context, "counter_anim/nuke_electric", "Electric_"));
            textureBox.addTextureSeries(TEXT_BLAST, getBitmapsFromSeries(context, "counter_anim/nuke_blast", "Blast_"));
            textureBox.addTextureSeries(TEXT_ROCK, getBitmapsFromSeries(context, "counter_anim/nuke_blast", "Rock_"));
            textureBox.addTextureSeries(TEXT_SMOKE, getBitmapsFromSeries(context, "counter_anim/nuke_smoke", "Smoke_"));
        }
        electricTextures = textureBox.getTextureSet(TEXT_ELECTRIC);
        blastTextures = textureBox.getTextureSet(TEXT_BLAST);
        rockTextures = textureBox.getTextureSet(TEXT_ROCK);
        smokeTextures = textureBox.getTextureSet(TEXT_SMOKE);

        backgroundTexture = getBitmapFromAssets(context, "counter_anim/NukePlantBackground.png");
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
        OnceAnimation rockRoll = new OnceAnimation(rockTextures, 80, new AtaVector(0, 0), new AtaVector(1, 1), 0);
        OnceAnimation blast = new OnceAnimation(blastTextures, 80, new AtaVector(0, 0), new AtaVector(1, 1), 0);

        AnimationSequence blastAnim = new AnimationSequence(new OnceAnimation[]{rockRoll, blast}, startTime);

        animations.add(blastAnim);
    }
}
