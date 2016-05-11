package com.atasoft.flangeassist.fragments.ropevalues;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ataboo on 2016-05-11.
 */
public class RopeSize {
    private float diameter;

    public RopeSize(float decimalInchDiameter){
        diameter = decimalInchDiameter;
    }

    public static RopeSize[] makeRopeSizes(float startSize, float endSize){
        if (endSize < startSize) {
            throw new NumberFormatException("endSize cannot be greater than startSize.");
        }

        ArrayList<RopeSize> sizes = new ArrayList<RopeSize>();

        float ropeSize = startSize;
        while (ropeSize <= endSize){
            RopeSize ropeSizeObj = new RopeSize(ropeSize);
            sizes.add(ropeSizeObj);
            ropeSize += 1f/16f;
            Log.w("RopeSize", String.format("Added %s from %.3f", ropeSizeObj, ropeSize));
        }

        return (RopeSize[]) sizes.toArray();
    }

    public float getBreakStrength(RopeType type){
        return type.getBreakFactor() * diameter * diameter;
    }

    public float getWorkLoadLimit(RopeType type, float safetyFactor){
        float breakStrength = getBreakStrength(type);
        return breakStrength / safetyFactor;
    }
}
