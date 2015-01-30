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
}
