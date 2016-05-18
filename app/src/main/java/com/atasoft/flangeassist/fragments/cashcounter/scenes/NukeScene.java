package com.atasoft.flangeassist.fragments.cashcounter.scenes;

import android.content.Context;
import android.graphics.Bitmap;

import com.atasoft.flangeassist.fragments.cashcounter.CashCounter2;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.AnimationSequence;
import com.atasoft.utilities.AtaVector;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.EarningText;
import com.atasoft.utilities.IntVector;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.OnceAnimation;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.OnceText;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.RepeatAnim;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.StaticAnim;

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

        StaticAnim backAnim = new StaticAnim(backgroundTexture);
        RepeatAnim smokeAnim = new RepeatAnim(smokeTextures, 70, new AtaVector(116f/1024, 0f / 720f),
                new AtaVector(514f / 1024f, 156f / 720f));
        earningText = new EarningText(new AtaVector(0.63f, 0.22f), 0f, CashCounter2.EarningType.OFF_SHIFT);

        animations.add(backAnim);
        animations.add(smokeAnim);
        animations.add(earningText);

        screenResize(screenSize);
    }

    private void initTextures(){
        // Stored textures in MainActivity to prevent reloading on screen size change.
        if (!textureBox.contains(TEXT_ELECTRIC)) {
            textureBox.dispose();
            textureBox.addTextureSeries(TEXT_ELECTRIC, getBitmapsFromSeries(context, "counter_anim/nuke_electric", "NukeElectric_"));
            textureBox.addTextureSeries(TEXT_BLAST, getBitmapsFromSeries(context, "counter_anim/nuke_blast", "NukeBlast_"));
            textureBox.addTextureSeries(TEXT_ROCK, getBitmapsFromSeries(context, "counter_anim/nuke_blast", "NukeRock_"));
            textureBox.addTextureSeries(TEXT_SMOKE, getBitmapsFromSeries(context, "counter_anim/nuke_smoke", "NukeSmoke_"));
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

        OnceAnimation electricAnim = new OnceAnimation(electricTextures, 50, new AtaVector(230f/1024f, 100f/720f),
                new AtaVector(126f/1024f, 174f/720f), startTime - 800);

        electricAnim.resize(sceneRect);

        OnceText electricText = new OnceText(String.format("%d¢", getCents(earnings)), new AtaVector(293f / 1024f, 150f/720f),
                new AtaVector(293f / 1024f, 80f/720f), startTime - 500, 2000);
        electricText.resize(sceneRect);

        animations.add(electricAnim);
        animations.add(electricText);
    }

    @Override
    public void addCoarseAnim(long startTime, float earnings) {
        OnceAnimation rockRoll = new OnceAnimation(rockTextures, 80, new AtaVector(0, 328f/720f), new AtaVector(728f/1024f, 392f/720f), 0);
        rockRoll.resize(sceneRect);
        OnceAnimation blast = new OnceAnimation(blastTextures, 80, new AtaVector(0, 0), new AtaVector(1, 1), 0);
        blast.resize(sceneRect);
        AnimationSequence blastAnim = new AnimationSequence(new OnceAnimation[]{rockRoll, blast}, startTime);

        animations.add(blastAnim);
    }
}
