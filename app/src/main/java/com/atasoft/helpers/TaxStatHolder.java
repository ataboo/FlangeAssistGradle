package com.atasoft.helpers;

import android.util.Log;

import com.atasoft.flangeassist.MainActivity;


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by AtaCompy on 11/3/2015.
 * Fork of TaxManager using CSVs generated from spreadsheets
 */
public class TaxStatHolder {

    public static final String csvSeperator = ",";
    public static final String csvNB = "Toolbox_PaycalcGrid - NB.csv";

    //Trending so hard
    public static final String wageTag  = "#wages";
    public static final String bracketsTag = "#brackets";
    public static final String constKTag = "#const_k";
    public static final String taxReductionTag = "#tax_red";
    public static final String healthPremTag = "#health_prem";
    public static final String surtaxTag = "#surtax";
    public static final String claimAmountTag = "#claim_amount";
    public static final String surNameTag = "#surname";

    public float[][] brackets;
    public float[][] constK;
    public float[][] taxReduction;
    public float[][] healthPrem;
    public float[][] surtax;
    public float[] claimAmount;
    public String[][] wageTable;
    public String surName;
    public float defaultWageIndex;  //tacked on end of wageRates inplace of custom value

    private ArrayList<String[]> wageTableList = new ArrayList<String[]>();
    private ArrayList<String[]> bracketsList = new ArrayList<String[]>();
    private ArrayList<String[]>constKList = new ArrayList<String[]>();
    private ArrayList<String[]> taxReductionList = new ArrayList<String[]>();
    private ArrayList<String[]> healthPremList = new ArrayList<String[]>();
    private ArrayList<String[]> surtaxList = new ArrayList<String[]>();

    public TaxStatHolder(int provinceInt){
        parseFile(csvNB);
        Collections.reverse(wageTableList);
        this.wageTable = listToStringArray(wageTableList, "wageTable");
        this.brackets = listToFloatArray(bracketsList, "brackets");
        this.constK = listToFloatArray(constKList, "constK");
        this.taxReduction = listToFloatArray(taxReductionList, "taxReduction");
        this.healthPrem = listToFloatArray(healthPremList, "healthPrem");
        this.surtax = listToFloatArray(surtaxList, "surtaxList");

    }

    private String[][] listToStringArray(ArrayList<String[]> list, String errorName) {
        if(surName == null) this.surName = "No Surname";
        if(list.size() == 0){
            Log.w("TaxStatHolder", surName + ", " + errorName + " has no list.");
            return null;
        }

        String[][] retArr = new String[list.size()][];
        int sizeCheck = list.get(0).length;
        for(int i=0; i<retArr.length; i++){
            retArr[i] = list.get(i);
            if(retArr[i].length != sizeCheck){
                Log.w("TaxStatHolder", "Size mismatch on:  " + surName + ", " + errorName);
            }
        }
        return retArr;
    }

    private float[][] listToFloatArray(ArrayList<String[]> list, String errorName) {
        if(surName == null) this.surName = "No Surname";
        if(list.size() == 0){
            Log.w("TaxStatHolder", surName + ", " + errorName + " has no list.");
            return null;
        }
        float[][] retArr = new float[list.size()][];
        int sizeCheck = list.get(0).length;
        for(int i=0; i < retArr.length; i++){
            retArr[i] = parseFloatArr(list.get(i), errorName);
            if(retArr[i].length != sizeCheck){
                Log.w("TaxStatHolder", "Size mismatch on:  " + surName + ", " + errorName);
            }
        }
        return retArr;
    }


    private boolean parseFile(String fileName){
        BufferedReader br;
        String line;

        try {
            InputStream inStr = MainActivity.staticRef.getAssets().open(fileName);
            br = new BufferedReader(new InputStreamReader(inStr));

            while((line = br.readLine()) != null){
                processLine(line);
            }
            br.close();
        } catch(IOException e){
            Log.e("TaxManCSV", "IOException for fileName: " + fileName);
            e.printStackTrace();
        }

        return false;
    }

    private void processLine(String line){
        String[] lineSplit = line.split(csvSeperator);
        if(lineSplit.length <=1) return;
        
        String lineTag = lineSplit[0];

        if(lineTag.equals(wageTag)){
            wageTableList.add(trimArray(lineSplit));
            return;
        }
        if(lineTag.equals(bracketsTag)){
            bracketsList.add(trimArray(lineSplit));
            return;
        }
        if(lineTag.equals(constKTag)){
            constKList.add(trimArray(lineSplit));
            return;
        }
        if(lineTag.equals(taxReductionTag)){
            taxReductionList.add(trimArray(lineSplit));
            return;
        }
        if(lineTag.equals(healthPremTag)){
            healthPremList.add(trimArray(lineSplit));
            return;
        }
        if(lineTag.equals(surtaxTag)){
            surtaxList.add(trimArray(lineSplit));
            return;
        }

        if(lineTag.equals(claimAmountTag)) {
            this.claimAmount = parseFloatArr(trimArray(lineSplit), claimAmountTag);
            return;
        }
        if(lineTag.equals(surNameTag)){
            this.surName = lineSplit[1];
        }

    }

    private static String[] trimArray(String[] srcArr){
        String[] arrCopy = new String[srcArr.length -1];
        System.arraycopy(srcArr, 1, arrCopy, 0, arrCopy.length);
        return arrCopy;
    }

    private static float[] parseFloatArr(String[] arr, String errorName){
        float[] retArr = new float[arr.length];

        try {
            for (int i=0; i<arr.length; i++) {
                retArr[i] = Float.parseFloat(arr[i]);
            }
        } catch (NumberFormatException e) {
            Log.e("TaxStatHolder", "Error parsing string to float for: " + errorName);
            e.printStackTrace();
        }
        return retArr;
    }


}
