package com.atasoft.flangeassist.fragments.ropevalues;

/**
 * Created by ataboo on 2016-05-11.
 */
public class MixedFraction {
    private int whole;
    private int numerator;
    private int denominator;
    private float decimal;

    public MixedFraction(float decimalValue){
        decimal = decimalValue;
        denominator = 16;

        int improperNumerator = Math.round(decimalValue * (float) denominator);
        whole = improperNumerator / denominator;
        numerator = improperNumerator % denominator;

        if(numerator > 0) {
            while (numerator % 2 == 0) {
                numerator /= 2;
                denominator /= 2;
            }
        }
    }

    @Override
    public String toString(){
        if(whole > 0){
            if(numerator > 0){
                return String.format("%d - %d/%d\"", whole, numerator, denominator);
            } else {
                return String.format("%d\"", whole);
            }
        } else {
            return String.format("%d/%d\"", numerator, denominator);
        }
    }

    public float getDecimal(){
        return decimal;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof MixedFraction)){
            return false;
        }
        MixedFraction mixedFrac = (MixedFraction) obj;

        return  whole == mixedFrac.whole &&
                numerator == mixedFrac.numerator &&
                denominator == mixedFrac.denominator;
    }


}
