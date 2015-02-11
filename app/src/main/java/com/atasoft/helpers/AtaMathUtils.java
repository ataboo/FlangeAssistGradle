package com.atasoft.helpers;

public class AtaMathUtils
{
	public static float bracketFloat(float value, float lower, float upper){
        if(upper < lower) return 0f;
		if(value > upper) return upper;
		if(value < lower) return lower;
		return value;}
	
	public static double bracketDouble(double value, double lower, double upper){
		if(upper < lower) return 0d;
		if(value > upper) return upper;
		if(value < lower) return lower;
		return value;
	}

    //Attempts to parse float between floor and ceiling values.  Returns 0 on invalid.
    public static float bracketFloat(String str, float floor, float ceiling) throws NumberFormatException{
        float val = 0f;
        try{
            val = Float.parseFloat(str);
        } catch(NumberFormatException e){
            e.printStackTrace();
        }
        val = bracketFloat(val, floor, ceiling);
        return val;
    }

    public static int bracketInt(int value, int floor, int ceiling){
        if(ceiling < floor) return 0;
        if(value > ceiling) return ceiling;
        if(value < floor) return floor;
        return value;
    }

    public static int bracketInt(String str, int floor, int ceiling) throws NumberFormatException{
        int val = 0;
        try{
            val = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return bracketInt(val, floor, ceiling);
    }
}
