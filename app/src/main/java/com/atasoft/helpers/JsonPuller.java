package com.atasoft.helpers;

import android.util.*;
import android.view.*;
import java.io.*;
import java.util.*;
import org.json.*;

public class JsonPuller
{
	View pView;
	public JsonPuller(View parentView) {
		this.pView = parentView;
		populateValues();
		return;
	}
	
	public static final int STUD_ARRAY_LENGTH = 4;
	public static final int RATE_ARRAY_LENGTH = 4;
	private static final String jFileName = "FlangeValues.json";
	private static final String jFileNameXL = "FlangeValuesXL.json";
	
	private boolean failFlag = false;
	private JSONObject masterObj;
	private String[] fSizes;
	private String[] fRatings;
	private String[] studSizeOrdered;
	private HashMap<String, String[]> studStats;
	private HashMap<String, String[]> fStats150;
	private HashMap<String, String[]> fStats300;
	private HashMap<String, String[]> fStats400;
	private HashMap<String, String[]> fStats600;
	private HashMap<String, String[]> fStats900;
	private HashMap<String, String[]> fStats1500;
	private HashMap<String, HashMap<String, String[]>> fStatHashes;
	
	private JSONObject masterXL;
	private String[] fSizesXL;
	private String[] fRatingsXL;
	private String[] fRatingsXXL;
	private String[] studSizesXL;
	private HashMap<String, String[]> studStatsXL;
	private HashMap<String, String[]> fStats150XL;
	private HashMap<String, String[]> fStats400XL;
	private HashMap<String, String[]> fStats900XL;
	
	public void populateValues(){
		this.masterObj = loadJSON(jFileName);
		this.masterXL = loadJSON(jFileNameXL);
		if(this.masterObj == null) {
			Log.e("JSON Puller", "masterObj is null. oops.");
			this.failFlag = true;
			return;
		}
		if(this.masterXL == null) {
			Log.e("JSON Puller", "masterXL is null. oops.");
			this.failFlag = true;
			return;
		}
		this.fStatHashes = new HashMap<String, HashMap<String, String[]>>(6);
		this.fSizes = getJSONStringArray(masterObj, "fSizes");
		this.fRatings = getJSONStringArray(masterObj, "fRatings");
		this.studSizeOrdered = getJSONStringArray(masterObj, "studSizeOrdered");
		this.studStats = makeHash(masterObj, "studSizes", studSizeOrdered, STUD_ARRAY_LENGTH);
		this.fStats150 = makeHash(masterObj, "fStats150", fSizes, RATE_ARRAY_LENGTH);
		fStatHashes.put("150", fStats150);
		this.fStats300 = makeHash(masterObj, "fStats300", fSizes, RATE_ARRAY_LENGTH);
		fStatHashes.put("300", fStats300);
		this.fStats400 = makeHash(masterObj, "fStats400", fSizes, RATE_ARRAY_LENGTH);
		fStatHashes.put("400", fStats400);
		this.fStats600 = makeHash(masterObj, "fStats600", fSizes, RATE_ARRAY_LENGTH);
		fStatHashes.put("600", fStats600);
		this.fStats900 = makeHash(masterObj, "fStats900", fSizes, RATE_ARRAY_LENGTH);
		fStatHashes.put("900", fStats900);
		this.fStats1500 = makeHash(masterObj, "fStats1500", fSizes, RATE_ARRAY_LENGTH);
		fStatHashes.put("1500", fStats1500);
		
		//XL for sizes above 24"
		this.fSizesXL = getJSONStringArray(masterXL, "fSizes");
		this.fRatingsXL = getJSONStringArray(masterXL, "fRatings");
		this.studSizesXL = getJSONStringArray(masterXL, "studSizeOrdered");
		this.studStatsXL = makeHash(masterXL, "studSizes", studSizesXL, STUD_ARRAY_LENGTH);
		this.fStats150XL = makeHash(masterXL, "fStats150", fSizesXL, RATE_ARRAY_LENGTH);
		fStatHashes.put("150XL", fStats150XL);
		this.fStats400XL = makeHash(masterXL, "fStats400", fSizesXL, RATE_ARRAY_LENGTH);
		fStatHashes.put("400XL", fStats400XL);
		this.fStats900XL = makeHash(masterXL, "fStats900", fSizesXL, RATE_ARRAY_LENGTH);
		fStatHashes.put("900XL", fStats900XL);
		
		//XXL for sizes above 48"
		//Hardcoded for simplicity's sake
		this.fRatingsXXL = new String[]{"150", "400"};
		
	}
	
	public String[] getSizesCombined() {
		if(fSizes == null || fSizesXL == null) return new String[]{"err"};
		String[] fSizeReturn = new String[fSizes.length + fSizesXL.length];
		for(int i=0; i<fSizes.length; i++){
			fSizeReturn[i] = fSizes[i];
		}
		for(int i=0; i < fSizesXL.length; i++) {
			fSizeReturn[i + fSizes.length] = fSizesXL[i];
		}
		return fSizeReturn;
	}
	
	public String[] getSizes(){
		return fSizes;
	}
	
	public String[] getSizesXL(){
		return fSizesXL;
	}

    public String[] getStudSizes(){ return studSizeOrdered; }
	
	public String[] getRates() {
		if(fRatings == null) return new String[]{"err"};
		return fRatings;
	}
	
	//Stud Diameter, Stud Size Index (not used), Stud Count, Stud Length  
	public String[] pullFlangeVal(String size, String rating){
		//Log.w("JSON Puller", "Checking: " + size + " " + rating);
		//if(failFlag) return null;
		String[] retString = new String[RATE_ARRAY_LENGTH];
		HashMap<String, String[]> fStatHash = fStatHashes.get(rating);
		retString = (String[]) fStatHash.get(size);
		return retString;
	}
	
	//Wrench size, Drift pin size, B7M torque val, B7 torque val
	public String[] pullStudVal(String studSize){
		if(failFlag) return null;
		return studStats.get(studSize);
	}
	
	public String[] getRatesXL(){
		return fRatingsXL;
	}
	
	public String[] getRatesXXL(){
		return fRatingsXXL;
	}
	
	private String[] getJSONStringArray(JSONObject parentObj, String jKey){
		try{
			JSONArray jStringArr = parentObj.getJSONArray(jKey);
			
			String[] retStr = jArrayToString(jStringArr);
			return retStr;
		} catch(JSONException jE){
			jE.printStackTrace();
			Log.e("JSON Puller", jKey + " was out to lunch.");
			return null;			
		}
	}
	
	private String[] jArrayToString(JSONArray jArray) {
		String[] retStr = new String[jArray.length()];
		try{
			for(int i = 0; i < retStr.length; i++) {
				retStr[i] = jArray.getString(i);
			}
		} catch(JSONException jE) {
			jE.printStackTrace();
			Log.e("JSON Puller", "jArrayToString out to lunch.");
			return null;
		}
		return retStr;
	}
	
	private JSONObject getJObject(String jKey, JSONObject mastObject){
		JSONObject retObj = null;
		try{
			retObj = mastObject.getJSONObject(jKey);
		} catch(JSONException jE){
			jE.printStackTrace();
			Log.e("JSON Puller", "getJObject JSON exception. looking for: " + jKey);
			return null;
		}
		return retObj;
	}
	
	private HashMap<String, String[]> makeHash(JSONObject mastObject, String arrayKey, String[] hashKey, int arrayLength){
		HashMap<String, String[]> retHash = new HashMap<String, String[]>(arrayLength);
		
		JSONObject arrayMaster = getJObject(arrayKey, mastObject);
		for(int i = 0; i < hashKey.length; i++){
			String[] fullStringArr = getJSONStringArray(arrayMaster, hashKey[i]);
			retHash.put(hashKey[i], fullStringArr);
		}
		return retHash;
	}
		
	private JSONObject loadJSON(String fileName) {
		JSONObject jObj = null;		
		try {
			String jStr = null;
			InputStream inStr = pView.getContext().getAssets().open(fileName);
			int size = inStr.available();
			byte[] buffer = new byte[size];
			inStr.read(buffer);
			inStr.close();
			jStr = new String(buffer, "UTF-8");

			jObj = new JSONObject(jStr);			
			return jObj;
		} catch (IOException ex) {
			ex.printStackTrace();
			Log.e("JSON Puller", "IOException on file load");
			return null;
		}
		catch (JSONException jE) {
			jE.printStackTrace();
			Log.e("JSON Puller", "JSONException on file load");
			return null;
		}
	}
}
