package com.atasoft.helpers;
import android.util.*;
import java.util.*;

//Keeps the figures and tables for the Unit Converter in Boilermaker Toolbox
public class ConvDataHold
{
	public class ConversionType{  
		public String name;
		public String[] keyNames;
		public HashMap<String, Double> conversions;
		public HashMap<String, String> unitMap;
		public ConversionType(String name, String[] keys, double[] values, String[] units){
			this.name = name;
			this.keyNames = keys;
			this.conversions = new HashMap<String, Double>(keys.length);
			this.unitMap = new HashMap<String, String>(units.length);
			if(keys.length != values.length || keys.length != units.length){
				Log.e("ConvDataHold", "Length-Value-Unit mismatch for " + name + ".");
			}
			for(int i=0; i < keys.length; i++){
				this.conversions.put(keys[i], values[i]);
				this.unitMap.put(keys[i], units[i]);
			}
		}
	}
	
	public static final String[] typeStrings = {"Length", "Pressure", "Mass", "Volume"};
	private HashMap<String, ConversionType> typeHash;
	private static final String[] lengthStrings = {"Meters", "Feet", "Inches", "Millimeter", "Centimeter", "Kilometer", "Statute Mile"};
	private static final String[] lengthUnits = {"m","ft","in","mm","cm","km","mi"};
	private static double[] lengthRates = {1d, 3.28083333d, 39.37007874d, 1000d, 100d, 0.001d, 0.00062137d};
	private static final String[] pressureStrings = {"Kilo-Pascals", "Pounds/Sq.in", "Inches Mercury", "Atmospheres", "Millibars"};
	private static final String[] pressureUnits = {"kPa","psi","inHg","atm","mBar"};
	private static double[] pressureRates = {1d, 0.145037738d, 0.296133971d, 0.009869233d, 10d};
	private static final String[] massStrings = {"Kilograms", "Pounds", "Ounces", "Short Ton (US)", "Long Ton (UK)", "Tonne", "Stone (UK)", "Carat", "Grain"};
	private static final String[] massUnits = {"kg","lbs","oz","ton(US)","ton(UK)","tonne","stone", "car", "gr"};
	private static double[] massRates = {1d, 2.204622622d, 35.27396195d, 0.001102311d, 0.000984207d, 0.001d, 0.157473044d, 5000d, 15432.3583529d};
	private static final String[] volumeStrings = {"Litres", "Barrel (oil)", "Gallons (US)", "Quarts (US)", "Pint (UK)", "Cup (US)", "Cubic Centimeter", "Cubic Foot"};
	private static final String[] volumeUnits = {"l", "BBL", "gal", "qrt", "pint", "cup", "cc", "ft3"};
	private static double[] volumeRates = {1d, 0.006289811d, 0.264172052d, 1.056688209d, 1.759753986d, 4.226752838d, 1000d, 0.35314667d};

	public ConvDataHold() {
		this.typeHash = new HashMap<String, ConversionType>(typeStrings.length);
		
		//To add another type: make ConversionType(name, keyStrings, valueStrings) and put to typeHash. Don't forget to add to typeStrings.
		ConversionType newType = new ConversionType(typeStrings[0], lengthStrings, lengthRates, lengthUnits);
		typeHash.put(newType.name, newType);
		newType = new ConversionType(typeStrings[1], pressureStrings, pressureRates, pressureUnits);
		typeHash.put(newType.name, newType);
		newType = new ConversionType(typeStrings[2], massStrings, massRates, massUnits);
		typeHash.put(newType.name, newType);
		newType = new ConversionType(typeStrings[3], volumeStrings, volumeRates, volumeUnits);
		typeHash.put(newType.name, newType);
	}
	
	public String[] getUnitNames(String typeKey){
		return typeHash.get(typeKey).keyNames;
	}
	
	public double convertValue(double inVal, String type, String unit1, String unit2){
		ConversionType unitType = typeHash.get(type);
		double factorOrig = unitType.conversions.get(unit1);
		double factorConv = unitType.conversions.get(unit2);
		return ((inVal / factorOrig) * factorConv);
	}
	
	public String getUnit(String type, String unit){
		ConversionType unitType = typeHash.get(type);
		String retString = unitType.unitMap.get(unit);
		return retString;
	}
	
	public String makeFraction(double decValue, int denom){
		String sign  = decValue < 0 ? "-" : "";
		decValue = Math.abs(decValue);
		int intVal = (int) decValue;
		int numerator = (int) Math.round((decValue - intVal) * denom);
		String fracString = lowComDen(numerator, denom);
		if(fracString == "0") {
			fracString = intVal > 0 ? sign + String.format("%d", intVal): "0";
			return fracString;
		}
		if(fracString == "1") return(Integer.toString(intVal+1));			
		
		sign = intVal > 0 ? sign + String.format("%d-", intVal): sign;
		fracString = sign + fracString;
		return fracString;
	}
	
	private String lowComDen(int numerator, int denom){
		if(numerator == 0) {
			return "0";
		}
		
		if(numerator == denom){
			return "1";
		}
		
		if(denom <= 0 || numerator > denom){
			Log.e("ConvDataHold", String.format("numerator: %d and denom: %d are invalid.", numerator, denom));
			return "Fraction Error";
		}
		while(numerator % 2 == 0 && numerator > 0){
			numerator /= 2;
			denom /= 2;
		}
		
		return String.format("%d/%d", numerator, denom);
	}
}
