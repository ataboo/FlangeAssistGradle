package com.atasoft.flangeassist.fragments.cashcounter.scenes;

import android.content.Context;

import com.atasoft.flangeassist.fragments.cashcounter.CashCounter;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.EarningText;
import com.atasoft.utilities.AtaVector;
import com.atasoft.utilities.IntVector;

/**
 * Created by ataboo on 2016-05-17.
 */
public class BasicScene extends CounterScene {
    public BasicScene(Context context, IntVector screenSize){
        this.context = context;
        sceneRatio = 600f/800f;
        scene = Scene.BORING;

        earningText = new EarningText(new AtaVector(0.5f, 0.5f), 0f, CashCounter.EarningType.OFF_SHIFT);

        animations.add(earningText);

        screenResize(screenSize);
    }

    @Override
    public void addCoarseAnim(long startTime, float earnings) {

    }
}
