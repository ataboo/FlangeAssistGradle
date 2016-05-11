package com.atasoft.flangeassist.fragments.ropevalues;

import java.util.ArrayList;

/**
 * Created by ataboo on 2016-05-10.
 */
public class RopeData {
    private RopeType[] types;
    private RopeSize[] sizes;
    private RopeSafety[] safetyFactors;

    public RopeData(){
        types = RopeType.ROPE_TYPES;
        sizes = RopeSize.makeRopeSizes(1f/16f, 3f);
        safetyFactors = RopeSafety.SAFETY_FACTORS;
    }

    public String[] getTypeStrings(){
        ArrayList<String> typeList = new ArrayList<String>();

        for (RopeType type: types) {
            typeList.add(type.toString());
        }

        // 0 length array optimized apparently.
        return typeList.toArray(new String[typeList.size()]);
    }

    public String[] getSizeStrings(){
        ArrayList<String> sizeList = new ArrayList<String>();

        for (RopeSize size: sizes) {
            sizeList.add(size.toString());
        }

        return sizeList.toArray(new String[sizeList.size()]);
    }

    public String[] getSafetyStrings(){
        ArrayList<String> safetyList = new ArrayList<String>();

        for(RopeSafety safety: safetyFactors){
            safetyList.add(safety.toString());
        }

        return safetyList.toArray(new String[safetyList.size()]);
    }

}
