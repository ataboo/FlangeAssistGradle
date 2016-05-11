package com.atasoft.flangeassist.fragments.ropevalues;

/**
 * Created by ataboo on 2016-05-10.
 */
public class RopeType {
    public static final RopeType IWRC = new RopeType("Ind. Wire Rope Core", 45);
    public static final RopeType FIBRE_CORE = new RopeType("Fiber Core Wire Rope", 42);
    public static final RopeType GENERIC_WIRE = new RopeType("Generic Wire Rope", 40);
    public static final RopeType CHAIN = new RopeType("Steel Chain", 24);
    public static final RopeType NYLON = new RopeType("Nylon Rope", 12.5f);
    public static final RopeType POLYESTER = new RopeType("Polyester Rope", 10);
    public static final RopeType POLYPROP = new RopeType("Polypropelene Rope", 7.5f);
    public static final RopeType ETHYLENE = new RopeType("Ethylene Rope", 6.25f);
    public static final RopeType MANILLA = new RopeType("Natural Fibre Rope", 5);

    public static final RopeType[] ROPE_TYPES = {IWRC, FIBRE_CORE, GENERIC_WIRE, CHAIN, NYLON, POLYESTER, POLYPROP, ETHYLENE, MANILLA};

    private String description;
    private float breakFactor;

    public RopeType(String description, float breakFactor){
        this.description = description;
        this.breakFactor = breakFactor;
    }

    public float getWllFactor(float safetyFactor, float diameter){
        return breakFactor / safetyFactor;
    }

    public float getBreakFactor(){
        return breakFactor;
    }

    public String getDescription(){
        return description;
    }
}
