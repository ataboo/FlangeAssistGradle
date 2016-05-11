package com.atasoft.flangeassist.fragments.ropevalues;

/**
 * Created by ataboo on 2016-05-11.
 */
public class RopeSafety {
    static final RopeSafety CHAIN = new RopeSafety("Chain (4x)", 4f);
    static final RopeSafety NEW = new RopeSafety("New Rope (5x)", 5);
    static final RopeSafety CHOKED = new RopeSafety("Choked Cable (6x)", 6);
    static final RopeSafety USED = new RopeSafety("Used Rope (7x)", 7);
    static final RopeSafety OLD = new RopeSafety("Older Rope (8x)", 8);
    static final RopeSafety MANNED = new RopeSafety("Men, Ropefall (10x)", 10);

    public static RopeSafety[] SAFETY_FACTORS = {CHAIN, NEW, CHOKED, USED, OLD, MANNED};

    private String description;
    private float factor;

    public RopeSafety(String description, float factor){
        this.description = description;
        this.factor = factor;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof RopeSafety)){
            return false;
        }
        RopeSafety ropeSafeObj = (RopeSafety) obj;

        return description.equals(ropeSafeObj.description) && factor == ropeSafeObj.factor;
    }

    @Override
    public String toString(){
        return description;
    }
}
