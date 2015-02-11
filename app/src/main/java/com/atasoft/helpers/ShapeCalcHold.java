package com.atasoft.helpers;

import java.util.*;

public class ShapeCalcHold
 {
	public static final String[] SHAPE_TYPES = new String[]{
		"Cylinder", "Sphere", "Box", "Rectangle", "Circle"};

	public class ShapeObject{
		int fieldCount;
		String[] labels;
		int type;
		public ShapeObject(int type){
			this.type = type;
			switch(type){
				case 0:  //Cylinder
					this.labels = new String[]{"Radius (r):", "Height (h):"};
					break;
				case 1:  //Sphere
					this.labels = new String[]{"Radius (r):"};
					break;
				case 2:  //Box
					this.labels = new String[]{"Length (l):", "Width (w):", "Height (h)"};
					break;
				case 3:  //Rectangle
					this.labels = new String[]{"Length (l):", "Width (w):"};
					break;
				default: //Circle
					this.labels = new String[]{"Radius (r):"};
					break;
			}
			this.fieldCount = labels.length;
		}
	}
		
	public ShapeCalcHold(){
		setupValues();
	}
	
	public String[] getLabelStrings(String shapeType){
		return shapeHash.get(shapeType).labels;
	}
	
	public int getType(String typeString){
		return shapeHash.get(typeString).type;
	}
	
	public String[] getValues(String typeName, double[] vals){
		int thisType = shapeHash.get(typeName).type;
		String[] retStr = new String[2];
		double[] calcVals = new double[2];
		switch(thisType){
			case 0:  //Cylinder (r, h)
				calcVals = solveCylinder(vals[0], vals[1]);
				retStr[0] = String.format("Volume: %s units cubed.", roundDouble(calcVals[0], 4));
				retStr[1] = String.format("Surface Area: %s units squared.", roundDouble(calcVals[1], 4));
				break;
			case 1:  //Sphere (r)
				calcVals = solveSphere(vals[0]);
				retStr[0] = String.format("Volume: %s units cubed.", roundDouble(calcVals[0], 4));
				retStr[1] = String.format("Surface Area: %s units squared.", roundDouble(calcVals[1], 4));
				break;
			case 2:  //Box (l, w, h)
				calcVals = solveBox(vals[0], vals[1], vals[2]);
				retStr[0] = String.format("Volume: %s units cubed.", roundDouble(calcVals[0], 4));
				retStr[1] = String.format("Surface Area: %s units squared.", roundDouble(calcVals[1], 4));
				break;
			case 3:  //Rectangle (l, w)
				calcVals = solveRect(vals[0], vals[1]);
				retStr[0] = String.format("Perimeter: %s units.", roundDouble(calcVals[0], 4));
				retStr[1] = String.format("Surface Area: %s units squared.", roundDouble(calcVals[1], 4));
				break;
			default: //Circle (r)
				calcVals = solveCirc(vals[0]);
				retStr[0] = String.format("Perimeter: %s units.", roundDouble(calcVals[0], 4));
				retStr[1] = String.format("Surface Area: %s units squared.", roundDouble(calcVals[1], 4));
				break;
		}
		
		//3d: volume, surface area. 2d: perimeter, surface area.
		return retStr;
	}
	
	private double[] solveCylinder(double radius, double height){
		double[] retArr = new double[2];
		retArr[0] = Math.PI * radius * radius * height;
		retArr[1] = (Math.PI * radius * radius * 2) + (2 * Math.PI * radius * height);
		return retArr;
	}
	
	private double[] solveSphere(double radius){
		double[] retArr = new double[2];
		retArr[0] = 4 * Math.PI * Math.pow(radius, 3) / 3;
		retArr[1] = 4 * Math.PI * radius * radius;
		return retArr;
	}
	
	private double[] solveBox(double length, double width, double height){
		double[] retArr = new double[2];
		retArr[0] = length * width * height;
		retArr[1] = 2 * (length * width + width * height + length * height);
		return retArr;
	}
	
	private double[] solveRect(double length, double width){
		double[] retArr = new double[2];
		retArr[1] = length * width;
		retArr[0] = 2 * length + 2 * width; //perimeter
		return retArr;
	}
	
	private double[] solveCirc(double radius){
		double[] retArr = new double[2];
		retArr[1] = Math.PI * radius * radius;
		retArr[0] = 2 * Math.PI * radius * radius; //perimeter
		return retArr;
	}
	
	private HashMap<String, ShapeObject> shapeHash;
	private void setupValues(){
		this.shapeHash = new HashMap<String, ShapeObject>(SHAPE_TYPES.length);
		for(int i=0; i<SHAPE_TYPES.length; i++){
			shapeHash.put(SHAPE_TYPES[i], new ShapeObject(i));
		}
	}
	
	public boolean isThis2D(String checkName){
        return checkName.equals(SHAPE_TYPES[3]) || checkName.equals(SHAPE_TYPES[4]);
    }
	
	private static double roundDouble(double val, int decimals) {
		double factor = Math.pow(10, decimals);
		val = (Math.round(val * factor)) / factor;
		return val;
	}
	
}
