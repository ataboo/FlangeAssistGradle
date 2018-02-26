package com.atasoft.flangeassist.fragments.paycalc;

import android.content.res.AssetManager;
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
    private static final String defaultWageName = "Journeyperson";
    private static final String fileNameConvention = "ToolboxGrid - %s.csv";
    private static final String csvSeparator = ",";

    //Trending so hard
    private static final String wageTag  = "#wages";
    private static final String ratesTag = "#rates";
    private static final String bracketsTag = "#brackets";
    private static final String constKTag = "#const_k";
    private static final String taxReductionTag = "#tax_red";
    private static final String surtaxTag = "#surtax";
    private static final String claimAmountTag = "#claim_amount";
    private static final String claimNsTag = "#claim_ns";
    private static final String cppEiTag = "#cpp_ei";
    private static final String vacRateTag = "#vac_rate";
    private static final String healthBracketTag = "#health_brackets";
    private static final String healthRateTag = "#health_rates";
    private static final String healthAmountTag = "#health_amounts";

    private static final String fieldDuesTag = "#field_dues";
    private static final String monthDuesTag = "#month_dues";
    private static final String nightPremiumTag = "#night_prem";
    private static final String nightOTTag = "#night_ot";
    private static final String doubleOTTag = "#double_ot";

    private static final String qppRateTag = "#qpp_rate";
    private static final String qppMaxTag = "#qpp_max";
    private static final String qpipRateTag = "#qpip_rate";
    private static final String qpipMaxTag = "#qpip_max";
    private static final String empDedTag = "#emp_ded";

    private static final String hoursHolidayTag = "#pay_holiday";
    private static final String hoursWeekendTag = "#pay_weekend";
    private static final String hoursWeekdayTag = "#pay_weekday";
    private static final String hoursFTFridayTag = "#pay_ft_friday";
    private static final String hoursFTWeekdayTag = "#pay_ft_weekday";

    private static final String levyBracketsTag = "#levy_brackets";
    private static final String levyBaseTag = "#levy_base";
    private static final String levyRateTag = "#levy_rate";


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
    public float[] claimNs;
    public float[] wageRates;
    public String[] wageNames;
    public float[][] cppEi;

    public float fieldDuesRate;
    public float monthDuesRate;
    public float nightPremiumRate;
    public boolean nightOT;
    public boolean doubleOT;

    public float[] qppRate;
    public float[] qppMax;
    public float[] qpipRate;
    public float[] qpipMax;
    public float[] empDeduction;

    public float[] hoursHoliday;
    public float[] hoursWeekend;
    public float[] hoursWeekday;
    public float[] hoursFTFriday;
    public float[] hoursFTWeekday;

    public float[][] levyBrackets;
    public float[][] levyBase;
    public float[] levyRate;



    public float vacRate = 0f;
    public String surName = "fail";
    public int defaultWageIndex = 0;  //tacked on end of wageRates inplace of custom value

    private ArrayList<String[]> wageTableList = new ArrayList<>();
    private ArrayList<String[]> bracketsList = new ArrayList<>();
    private ArrayList<String[]> rateList = new ArrayList<>();
    private ArrayList<String[]> constKList = new ArrayList<>();
    private ArrayList<String[]> taxReductionList = new ArrayList<>();
    private ArrayList<String[]> healthBracketList = new ArrayList<>();
    private ArrayList<String[]> healthRateList = new ArrayList<>();
    private ArrayList<String[]> healthAmountList = new ArrayList<>();
    private ArrayList<String[]> surtaxList = new ArrayList<>();
    private ArrayList<String[]> cppEiList = new ArrayList<>();
    private ArrayList<String[]> levyBracketsList = new ArrayList<>();
    private ArrayList<String[]> levyBaseList = new ArrayList<>();

    private AssetManager assets;

    public TaxStatHolder(TaxManager.Prov prov, AssetManager assets){
        this.prov = prov;
        this.surName = prov.getSurname();

        /*  Basic check for CSVs expected
        if(prov == TaxManager.Prov.FED){
            checkForCSVs();
        }
        */

        this.assets = assets;
        parseFile(getCSVFileName(prov), assets);

        if(wageTableList.size() > 0) parseWageTable(wageTableList);

        this.brackets = listToFloatArray(bracketsList, "brackets");
        this.rates = listToFloatArray(rateList, "rates");
        this.constK = listToFloatArray(constKList, "constK");
        this.taxReduction = listToFloatArray(taxReductionList, "taxReduction");
        this.healthBracket = listToFloatArray(healthBracketList, "healthBrack");
        this.healthRate = listToFloatArray(healthRateList, "healthRate");
        this.healthAmount = listToFloatArray(healthAmountList, "healthAmount");
        this.levyBrackets = listToFloatArray(levyBracketsList, "levyBrackets");
        this.levyBase = listToFloatArray(levyBaseList, "levyBase");

        this.surtax = listToFloatArray(surtaxList, "surtaxList");
        this.cppEi = listToFloatArray(cppEiList, "cppEiList");

        //Cape Breton has it's own wage info and uses NS Tax
        if(prov == TaxManager.Prov.CB) {capeBretonInit();}

        //PEI uses NS wage table but needs to parse its tax info.
        if(prov == TaxManager.Prov.PE){peiInit();}
    }

    private void capeBretonInit(){
        copyTaxFields(this, TaxManager.Prov.NS, assets);
    }

    private void peiInit(){
        //parseWageTable won't overwrite because it aborts when there's no table in the csv
        copyWageFields(this, TaxManager.Prov.NS, assets);
    }

    private static void copyWageFields(TaxStatHolder receiver, TaxManager.Prov wageProv, AssetManager assets){
        TaxStatHolder wageStats = new TaxStatHolder(wageProv, assets);
        receiver.wageNames = wageStats.wageNames;
        receiver.wageRates = wageStats.wageRates;
        receiver.vacRate = wageStats.vacRate;
        receiver.fieldDuesRate = wageStats.fieldDuesRate;
        receiver.monthDuesRate = wageStats.monthDuesRate;
        receiver.nightOT = wageStats.nightOT;
        receiver.doubleOT = wageStats.doubleOT;
        receiver.nightPremiumRate = wageStats.nightPremiumRate;

        receiver.hoursWeekday = wageStats.hoursWeekday;
        receiver.hoursHoliday = wageStats.hoursHoliday;
        receiver.hoursWeekend = wageStats.hoursWeekend;
        receiver.hoursFTFriday = wageStats.hoursFTFriday;
        receiver.hoursFTWeekday = wageStats.hoursFTWeekday;
    }

    private static void copyTaxFields(TaxStatHolder receiver, TaxManager.Prov taxProv, AssetManager assets){
        TaxStatHolder taxStats = new TaxStatHolder(taxProv, assets);
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
            list.clear();
            return null;
        }

        String[][] retArr = new String[list.size()][];
        for(int i=0; i<retArr.length; i++){
            retArr[i] = list.get(i);
        }
        list.clear();
        return retArr;
    }

    private float[][] listToFloatArray(ArrayList<String[]> list, String errorName) {
        if(list.size() == 0){
            list.clear();
            return null;
        }
        float[][] retArr = new float[list.size()][];
        for(int i=0; i < retArr.length; i++){
            retArr[i] = parseFloatArr(list.get(i), errorName);
        }
        list.clear();
        return retArr;
    }
    
    private boolean parseFile(String fileName, AssetManager assets){
        BufferedReader br;
        String line;

        try {
            InputStream inStr = assets.open(fileName);
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
            healthBracketList.add(lineTrim);
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
        if(lineTag.equals(levyBracketsTag)) {
            levyBracketsList.add(lineTrim);
        }
        if(lineTag.equals(levyBaseTag)) {
            levyBaseList.add(lineTrim);
        }

        // Single Row Arrays
        if(lineTag.equals(claimAmountTag)) {
            this.claimAmount = parseFloatArr(lineTrim, claimAmountTag);
            return;
        }

        if(lineTag.equals(claimNsTag)) {
            this.claimNs = parseFloatArr(lineTrim, claimNsTag);
        }

        if(lineTag.equals(qppRateTag)){
            this.qppRate = parseFloatArr(lineTrim, qppRateTag);
            return;
        }
        if(lineTag.equals(qppMaxTag)){
            this.qppMax = parseFloatArr(lineTrim, qppMaxTag);
            return;
        }
        if(lineTag.equals(qpipRateTag)){
            this.qpipRate = parseFloatArr(lineTrim, qpipRateTag);
            return;
        }
        if(lineTag.equals(qpipMaxTag)){
            this.qpipMax = parseFloatArr(lineTrim, qpipMaxTag);
        }
        if(lineTag.equals(empDedTag)){
            this.empDeduction = parseFloatArr(lineTrim, empDedTag);
        }

        if(lineTag.equals(hoursWeekdayTag)) {
            this.hoursWeekday = parseFloatArr(lineTrim, hoursWeekdayTag);
        }
        if(lineTag.equals(hoursWeekendTag)) {
            this.hoursWeekend = parseFloatArr(lineTrim, hoursWeekendTag);
        }
        if(lineTag.equals(hoursHolidayTag)) {
            this.hoursHoliday = parseFloatArr(lineTrim, hoursHolidayTag);
        }
        if(lineTag.equals(hoursFTFridayTag)) {
            this.hoursFTFriday = parseFloatArr(lineTrim, hoursFTFridayTag);
        }
        if(lineTag.equals(hoursFTWeekdayTag)) {
            this.hoursFTWeekday = parseFloatArr(lineTrim, hoursFTWeekdayTag);
        }

        // Single Values
        if (lineTag.equals(fieldDuesTag)) {
            this.fieldDuesRate = parseFloatVal(lineTrim, fieldDuesTag);
            return;
        }
        if (lineTag.equals(monthDuesTag)) {
            this.monthDuesRate = parseFloatVal(lineTrim, monthDuesTag);
            return;
        }
        if(lineTag.equals(nightPremiumTag)){
            this.nightPremiumRate = parseFloatVal(lineTrim, nightPremiumTag);
            return;
        }
        if(lineTag.equals(nightOTTag)){
            this.nightOT = parseBoolVal(lineTrim, nightOTTag);
            return;
        }
        if(lineTag.equals(doubleOTTag)){
            this.doubleOT = parseBoolVal(lineTrim, doubleOTTag);
            //return;
        }

        if(lineTag.equals(levyRateTag)) {
            this.levyRate = parseFloatArr(lineTrim, levyRateTag);
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
