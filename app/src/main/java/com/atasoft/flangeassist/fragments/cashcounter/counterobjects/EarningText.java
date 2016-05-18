package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.atasoft.flangeassist.fragments.cashcounter.CashCounter2;
import com.atasoft.utilities.AtaMathUtils;
import com.atasoft.utilities.AtaVector;
import com.atasoft.utilities.IntVector;

/**
 * Created by ataboo on 2016-05-15.
 */
public class EarningText extends CounterAnim {
    private static float FONT_NORMAL = 96f;
    private static float FONT_BIG = 110f;
    private static int ANIM_DURATION = 200;

    private float earnings;
    private float newEarnings;
    private AtaVector posFactor;
    private long nextUpdate = -1;
    private IntVector position = IntVector.zero();
    private IntVector typePos = IntVector.zero();
    private CashCounter2.EarningType earningType;



    public EarningText(AtaVector posFactor, float earnings, CashCounter2.EarningType earningType){
        this.earnings = earnings;
        this.posFactor = posFactor;
        this.earningType = earningType;

    }

    public void animateEarnings(float newEarnings, long updateTime, CashCounter2.EarningType earningType){
        this.nextUpdate = updateTime;
        this.newEarnings = newEarnings;
        this.earningType = earningType;
    }

    public void setEarnings(float earnings, CashCounter2.EarningType earningType){
        this.earnings = this.newEarnings = earnings;
        this.nextUpdate = -1;
        this.earningType = earningType;
    }

    public float getEarnings(){
        return earnings;
    }

    public void setPosFactor(AtaVector posFactor){
        this.posFactor = posFactor;
    }


    @Override
    public void draw(long timeMillis, Canvas canvas, Paint paint) {
        float fontSize = FONT_NORMAL;


        if(nextUpdate >= timeMillis && nextUpdate - timeMillis < ANIM_DURATION){
            float progress = (float) (nextUpdate - timeMillis) / (float) ANIM_DURATION;
            progress = 1f - AtaMathUtils.clampFloat(progress, 0f, 1f);

            if(progress < 0.5){
                fontSize = AtaMathUtils.lerpFloat(FONT_NORMAL, FONT_BIG, progress * 2f);

                //Log.w("EarningText", "First half progress:" + progress);
            } else {
                //float newProgress = 2f * (progress - 0.5f);

                fontSize = AtaMathUtils.lerpFloat(FONT_BIG, FONT_NORMAL, 2f * (progress - 0.5f));

                //Log.w("EarningText", "Second half progress:" + progress);
                if(earnings != newEarnings) {
                    earnings = newEarnings;
                    //Log.w("EarningText", "new Earning set.");
                }
            }
        }

        paint.setTextSize(fontSize);
        paint.setColor(earningType.getColor());

        canvas.drawText(String.format("$%.2f", earnings), position.x, position.y, paint);

        paint.setTextSize(fontSize * 0.60f);
        canvas.drawText(earningType.toString(), typePos.x, typePos.y, paint);

    }

    @Override
    public void resize(Rect sceneRect) {
        this.position = pointFromFactor(sceneRect, posFactor);
        this.typePos = pointFromFactor(sceneRect, posFactor.add(new AtaVector(0f, 0.125f)));
    }

    @Override
    public void dispose() {

    }
}
