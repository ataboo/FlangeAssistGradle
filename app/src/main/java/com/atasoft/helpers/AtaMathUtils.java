package com.atasoft.helpers;

import android.util.Log;

public class AtaMathUtils
{
	public static float clampFloat(float value, float lower, float upper) {
        if (upper < lower) {
            float newLower = upper;
            upper = lower;
            lower = newLower;
        }
        return value > upper ? upper : value < lower ? lower : value;
    }
	
	public static double clampDouble(double value, double lower, double upper){
		if(upper < lower) return 0d;
		if(value > upper) return upper;
		if(value < lower) return lower;
		return value;
	}

    //Attempts to parse float between floor and ceiling values.  Returns 0 on invalid.
    public static float clampFloat(String str, float floor, float ceiling) throws NumberFormatException{
        float val = 0f;
        try{
            val = Float.parseFloat(str);
        } catch(NumberFormatException e){
            e.printStackTrace();
        }
        val = clampFloat(val, floor, ceiling);
        return val;
    }

    /*
    public static int clampInt(int value, int floor, int ceiling){
        if(ceiling < floor) return 0;
        if(value > ceiling) return ceiling;
        if(value < floor) return floor;
        return value;
    }
    */

    public static int clampInt(int val, int floor, int ceiling){
        int newFloor = ceiling > floor ? floor: ceiling;
        ceiling = ceiling > floor ? ceiling : floor;

        return val > ceiling ? ceiling : val < floor ? floor : val;
    }

    public static int clampInt(String str, int floor, int ceiling) throws NumberFormatException{
        int val = 0;
        try{
            val = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return clampInt(val, floor, ceiling);
    }

    public static double[] roundDoubles(double[] inArr){
        for(int i=0; i<inArr.length; i++){
            inArr[i] = Math.round(inArr[i] * 100d)/100d;
        }
        return inArr;
    }

    public static float parseFloat(String strIn) throws NumberFormatException{
        try{
            return Float.parseFloat(strIn);
        } catch (NumberFormatException e){
            Log.e("CashCounter", "Error parsing Float");
            return 0f;
        }
    }

    public static float lerpFloat(float start, float target, float progress){
        float dif = Math.abs(target - start);
        return start > target ? start - dif * progress : start + dif * progress;
    }
}
