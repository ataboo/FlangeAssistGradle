package com.atasoft.flangeassist.fragments.ropevalues;

import java.util.HashMap;

/**
 * Created by ataboo on 2016-05-10.
 */
public class RopeData {
    private RopeType[] types;
    private RopeSize[] sizes;

    public RopeData(){
        types = RopeType.ROPE_TYPES;
        sizes = RopeSize.makeRopeSizes(1f/16f, 3f);
    }

}
