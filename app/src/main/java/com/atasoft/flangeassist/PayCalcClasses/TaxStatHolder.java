package com.atasoft.flangeassist.PayCalcClasses;

import android.util.Log;

import com.atasoft.flangeassist.MainActivity;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by AtaCompy on 11/3/2015.
 * Fork of TaxManager using CSVs generated from spreadsheets
 */
public class TaxStatHolder {
    public static final String defaultWageName = "Journeyperson";
    public static final String fileNameConvention = "ToolboxGrid - %s.csv";
    public static final String csvSeparator = ",";

    //Trending so hard
    public static final String wageTag  = "#wages";
    public static final String ratesTag = "#rates";
    public static final String bracketsTag = "#brackets";
    public static final String constKTag = "#const_k";
    public static final String taxReductionTag = "#tax_red";
    public static final String surtaxTag = "#surtax";
    public static final String claimAmountTag = "#claim_amount";
    public static final String cppEiTag = "#cpp_ei";
    public static final String vacRateTag = "#vac_rate";
    public static final String healthBracketTag = "#health_brackets";
    public static final String healthRateTag = "#health_rates";
    public static final String healthAmountTag = "#health_amounts";

    public static final String fieldDuesTag = "#field_dues";
    public static final String monthDuesTag = "#month_dues";
    public static final String nightPremiumTag = "#night_prem";
    public static final String nightOTTag = "#night_ot";
    public static final String doubleOTTag = "#double_ot";


    //TODO: change public stats to getters and add null checks;
    public TaxManager.Prov prov = TaxManager.Prov.FED;
    public float[][] brackets;
    public float[][] rates;
    public float[][] constK;
    public float[][] taxReduction;
    public float[][] healthBracket;
    public float[][] healthRate;
    public float[][] healthAmount;

    public float[][] surtax;
    public float[] claimAmount;
    public float[] wageRates;
    public String[] wageNames;
    public float[][] cppEi;

    public float fieldDuesRate;
    public float monthDuesRate;
    public float nightPremiumRate;
    public boolean nightOT;
    public boolean doubleOT;

    public float vacRate = 0f;
    public String surName = "fail";
    public int defaultWageIndex = 0;  //tacked on end of wageRates inplace of custom value

    private ArrayList<String[]> wageTableList = new ArrayList<>();
    private ArrayList<String[]> bracketsList = new ArrayList<>();
    private ArrayList<String[]> rateList = new ArrayList<>();
    private ArrayList<String[]> constKList = new ArrayList<>();
    private ArrayList<String[]> taxReductionList = new ArrayList<>();
    private ArrayList<String[]> healthBrackList = new ArrayList<>();
    private ArrayList<String[]> healthRateList = new ArrayList<>();
    private ArrayList<String[]> healthAmountList = new ArrayList<>();
    private ArrayList<String[]> surtaxList = new ArrayList<>();
    private ArrayList<String[]> cppEiList = new ArrayList<>();

    public TaxStatHolder(TaxManager.Prov prov){
        this.prov = prov;
        this.surName = prov.getSurname();

        /*  Basic check for CSVs expected
        if(prov == TaxManager.Prov.FED){
            checkForCSVs();
        }
        */



        parseFile(getCSVFileName(prov));

        if(wageTableList.size() > 0) parseWageTable(wageTableList);

        this.brackets = listToFloatArray(bracketsList, "brackets");
        this.rates = listToFloatArray(rateList, "rates");
        this.constK = listToFloatArray(constKList, "constK");
        this.taxReduction = listToFloatArray(taxReductionList, "taxReduction");
        this.healthBracket = listToFloatArray(healthBrackList, "healthBrack");
        this.healthRate = listToFloatArray(healthRateList, "healthRate");
        this.healthAmount = listToFloatArray(healthAmountList, "healthAmount");

        this.surtax = listToFloatArray(surtaxList, "surtaxList");
        this.cppEi = listToFloatArray(cppEiList, "cppEiList");

        //Cape Breton has it's own wage info and uses NS Tax
        if(prov == TaxManager.Prov.CB) {capeBretonInit();}

        //PEI uses NS wage table but needs to parse its tax info.
        if(prov == TaxManager.Prov.PE){peiInit();}

        //Manitoba uses SK wage table.
        if(prov == TaxManager.Prov.MB){manitobaInit();};
    }

    private void capeBretonInit(){
        copyTaxFields(this, TaxManager.Prov.NS);
    }

    private void manitobaInit(){
        copyWageFields(this, TaxManager.Prov.SK);
    }

    private void peiInit(){
        //parseWageTable won't overwrite because it aborts when there's no table in the csv
        copyWageFields(this, TaxManager.Prov.NS);
    }

    private static void copyWageFields(TaxStatHolder receiver, TaxManager.Prov wageProv){
        TaxStatHolder wageStats = new TaxStatHolder(wageProv);
        receiver.wageNames = wageStats.wageNames;
        receiver.wageRates = wageStats.wageRates;
        receiver.vacRate = wageStats.vacRate;
        receiver.fieldDuesRate = wageStats.fieldDuesRate;
        receiver.monthDuesRate = wageStats.monthDuesRate;
        receiver.nightOT = wageStats.nightOT;
        receiver.doubleOT = wageStats.doubleOT;
        receiver.nightPremiumRate = wageStats.nightPremiumRate;
    }

    private static void copyTaxFields(TaxStatHolder receiver, TaxManager.Prov taxProv){
        TaxStatHolder taxStats = new TaxStatHolder(taxProv);
        receiver.brackets = taxStats.brackets;
        receiver.rates = taxStats.rates;
        receiver.constK = taxStats.constK;
        receiver.claimAmount = taxStats.claimAmount;
        receiver.healthAmount = taxStats.healthAmount;
        receiver.healthBracket = taxStats.healthBracket;
        receiver.healthRate = taxStats.healthRate;
        receiver.surtax = taxStats.surtax;
    }

    private void parseWageTable(ArrayList<String[]> wageTableList) {
        if (wageTableList.size() == 0){
            Log.w("TaxStatHolder", String.format("statHolder %s has no wage table.", surName));
            wageTableList.clear();
        return;
        }

        Collections.reverse(wageTableList);
        String[][] wageTable =  listToStringArray(wageTableList, "wageTable");

        this.wageRates = new float[wageTable.length];
        this.wageNames = new String[wageTable.length];

        try {
            for (int i = 0; i < wageTable.length; i++) {
                if(wageTable[i].length != 2){
                    Log.e("TaxStatHolder", surName + " wage table malformed in row: " + i);
                    return;
                }
                this.wageNames[i] = wageTable[i][0];
                this.wageRates[i] = Float.parseFloat(wageTable[i][1]);
                if(wageNames[i].equals(defaultWageName)){
                    this.defaultWageIndex = i;
                }
            }
        } catch (NumberFormatException e){
            Log.e("TaxStatHolder", "Failed to parse wage table: " + surName);
            e.printStackTrace();
        }
        wageTableList.clear();
    }

    private String[][] listToStringArray(ArrayList<String[]> list, String errorName) {
        if(list.size() == 0){
            //Log.w("TaxStatHolder", surName + ", " + errorName + " has no list.");
            list.clear();
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
        list.clear();
        return retArr;
    }

    private float[][] listToFloatArray(ArrayList<String[]> list, String errorName) {
        if(list.size() == 0){
            //Log.w("TaxStatHolder", surName + ", " + errorName + " has no list.");
            list.clear();
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
        list.clear();
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
        String[] lineSplit = line.split(csvSeparator);
        if(lineSplit.length <=1) return;
        
        String lineTag = lineSplit[0];

        if(lineTag.equals(vacRateTag)){
            try {
                this.vacRate = Float.parseFloat(lineSplit[1]);
            } catch (NumberFormatException e){
                Log.e("TaxStatHolder", "Failed to parse line: " + line + " as vacRate.");
                e.printStackTrace();
            }
            return;
        }

        String[] lineTrim = trimArray(lineSplit);

        // Multi line Arrays
        if(lineTag.equals(wageTag)){
            wageTableList.add(lineTrim);
            return;
        }
        if(lineTag.equals(bracketsTag)){
            bracketsList.add(lineTrim);
            return;
        }
        if(lineTag.equals(ratesTag)){
            rateList.add(lineTrim);
            return;
        }
        if(lineTag.equals(constKTag)){
            constKList.add(lineTrim);
            return;
        }
        if(lineTag.equals(taxReductionTag)){
            taxReductionList.add(lineTrim);
            return;
        }
        if(lineTag.equals(healthBracketTag)){
            healthBrackList.add(lineTrim);
            return;
        }
        if(lineTag.equals(healthRateTag)){
            healthRateList.add(lineTrim);
            return;
        }
        if(lineTag.equals(healthAmountTag)){
            healthAmountList.add(lineTrim);
            return;
        }
        if(lineTag.equals(surtaxTag)){
            surtaxList.add(lineTrim);
            return;
        }
        if(lineTag.equals(cppEiTag)){
            cppEiList.add(lineTrim);
            return;
        }

        // Single Row Arrays
        if(lineTag.equals(claimAmountTag)) {
            this.claimAmount = parseFloatArr(lineTrim, claimAmountTag);
        }

        // Single Values
        if (lineTag.equals(fieldDuesTag)) {
            this.fieldDuesRate = parseFloatVal(lineTrim, fieldDuesTag);
        }
        if (lineTag.equals(monthDuesTag)) {
            this.monthDuesRate = parseFloatVal(lineTrim, monthDuesTag);
        }
        if(lineTag.equals(nightPremiumTag)){
            this.nightPremiumRate = parseFloatVal(lineTrim, nightPremiumTag);
        }
        if(lineTag.equals(nightOTTag)){
            this.nightOT = parseBoolVal(lineTrim, nightOTTag);
        }
        if(lineTag.equals(doubleOTTag)){
            this.doubleOT = parseBoolVal(lineTrim, doubleOTTag);
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

    private static float parseFloatVal(String[] arr, String errorName){
        float[] parsedFloats = parseFloatArr(arr, errorName);
        if (parsedFloats.length != 1){
            Log.e("TaxStatHolder", "Failed to parse " + errorName +
                    " as single float because of size mismatch.");
            return 0f;
        }
        return parsedFloats[0];
    }

    private static boolean parseBoolVal(String[] lineStrings, String errorName){
        if (lineStrings.length != 1){
            Log.e("TaxStatHolder", "Failed to set " + errorName +
                    " as single boolean because of line size mismatch.");
            return false;
        }
        return lineStrings[0].equals("TRUE");
    }

    public static String getCSVFileName(TaxManager.Prov prov){
        return String.format(fileNameConvention, prov.getSurname().split(" ")[0]);
    }

    public static void checkForCSVs(){
        List<String> assetFileList;
        try{
            assetFileList = Arrays.asList(MainActivity.staticRef.getAssets().list(""));
        }catch(IOException e){
            e.printStackTrace();
            return;
        }

        for(TaxManager.Prov prov: TaxManager.Prov.values()){
            if(!assetFileList.contains(getCSVFileName(prov))){
                Log.w("TaxStatHolder", String.format("Couldn't find file: %s in assets.", getCSVFileName(prov)));
            }
        }
    }

}
