package com.atasoft.helpers;

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
    public static final String csvSeperator = ",";

    //Trending so hard
    public static final String wageTag  = "#wages";
    public static final String ratesTag = "#rates";
    public static final String bracketsTag = "#brackets";
    public static final String constKTag = "#const_k";
    public static final String taxReductionTag = "#tax_red";
    public static final String healthPremTag = "#health_prem";
    public static final String surtaxTag = "#surtax";
    public static final String claimAmountTag = "#claim_amount";
    public static final String cppEiTag = "#cpp_ei";
    public static final String vacRateTag = "#vac_rate";

    //TODO: change public stats to getters and add null checks;
    public TaxManager.Prov prov = TaxManager.Prov.FED;
    public float[][] brackets;
    public float[][] rates;
    public float[][] constK;
    public float[][] taxReduction;
    public float[][] healthPrem;
    public float[][] surtax;
    public float[] claimAmount;
    public float[] wageRates;
    public String[] wageNames;
    public float[][] cppEi;

    public float vacRate = 0f;
    public String surName = "fail";
    public int defaultWageIndex = 0;  //tacked on end of wageRates inplace of custom value

    private ArrayList<String[]> wageTableList = new ArrayList<>();
    private ArrayList<String[]> bracketsList = new ArrayList<>();
    private ArrayList<String[]> rateList = new ArrayList<>();
    private ArrayList<String[]> constKList = new ArrayList<>();
    private ArrayList<String[]> taxReductionList = new ArrayList<>();
    private ArrayList<String[]> healthPremList = new ArrayList<>();
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

        //Cape Breton holds NB wage info and has no file to parse
        if(prov == TaxManager.Prov.CB) {
            capeBretonInit();
            return;
        }

        //PEI uses NS wage table but needs to parse its tax info.
        if(prov == TaxManager.Prov.PE){peiInit();}

        parseFile(getCSVFileName(prov));

        if(wageTableList.size() > 0) parseWageTable(wageTableList);

        this.brackets = listToFloatArray(bracketsList, "brackets");
        this.rates = listToFloatArray(rateList, "rates");
        this.constK = listToFloatArray(constKList, "constK");
        this.taxReduction = listToFloatArray(taxReductionList, "taxReduction");
        this.healthPrem = listToFloatArray(healthPremList, "healthPrem");
        this.surtax = listToFloatArray(surtaxList, "surtaxList");
        this.cppEi = listToFloatArray(cppEiList, "cppEiList");
    }

    private void capeBretonInit(){
        TaxStatHolder nbStats = new TaxStatHolder(TaxManager.Prov.NB);
        this.wageRates = nbStats.wageRates;
        this.wageNames = nbStats.wageNames;
        this.vacRate = nbStats.vacRate;

        TaxStatHolder nsStats = new TaxStatHolder(TaxManager.Prov.NS);
        this.brackets = nsStats.brackets;
        this.rates = nsStats.rates;
        this.constK = nsStats.constK;
        this.claimAmount = nsStats.claimAmount;
        return;
    }

    private void peiInit(){
        //parseWageTable won't overwrite because it aborts when there's no table in the csv
        TaxStatHolder nsStats = new TaxStatHolder(TaxManager.Prov.NS);
        this.wageRates = nsStats.wageRates;
        this.wageNames = nsStats.wageNames;
        this.vacRate = nsStats.vacRate;
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
        String[] lineSplit = line.split(csvSeperator);
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

        if(lineTag.equals(wageTag)){
            wageTableList.add(trimArray(lineSplit));
            return;
        }
        if(lineTag.equals(bracketsTag)){
            bracketsList.add(trimArray(lineSplit));
            return;
        }
        if(lineTag.equals(ratesTag)){
            rateList.add(trimArray(lineSplit));
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

        if(lineTag.equals(cppEiTag)){
            cppEiList.add(trimArray(lineSplit));
            return;
        }

        if(lineTag.equals(claimAmountTag)) {
            this.claimAmount = parseFloatArr(trimArray(lineSplit), claimAmountTag);
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

/*
	//Used as a container for tables for each type of tax
	public static class TaxStats{
		public double[][] rates;
		public double[][] brackets;
		public double[][] constK;
		public double[][] taxReduction;
		public double[][] healthPrem;
		public double[][] surtax;
		public double[] wageRates;
		public String[] wageNames;
        public String surName;
		public double defaultWageIndex;  //tacked on end of wageRates inplace of custom value
		public double[] vacRate;
        public double[] claimAmount;

        public TaxStats(int type) {
            switch(type) {
                //=====================================FED====================================
                case FED:
                    this.brackets = new double[][]{
                            {0, 43561, 87123, 135054},
                            {0, 43953, 87907, 136370},
                            {0, 44701, 89401, 138586}};
                    this.rates = new double[][]{
                            {0.15, 0.22, 0.26, 0.29},
                            {0.15, 0.22, 0.26, 0.29},
                            {0.15, 0.22, 0.26, 0.29}
                    };
                    this.constK = new double[][]{
                            {0, 3049, 6534, 10586},
                            {0, 3077, 6593, 10681},
                            {0, 3129, 6705, 10863}};
                    //(cpp max + ei max) * .15 [K2] + tax cred * .15 [K4]
                    //claim amount + canada employment credit
                    this.claimAmount = new double[]{11038 + 1117, 11138 + 1127, 11327 + 1146};
                    break;
                //=====================================BC====================================
                case PROV_BC:
                    this.surName = "BC - ";
                    this.brackets = new double[][]{
                            {0, 37568, 75138, 86268, 104754, 150000},
                            {0, 37606, 75213, 86354, 104858, 150000},
                            {0, 37869, 75740, 86958, 105592, 151050}};
                    this.rates = new double[][]{
                            {0.0506, 0.0770, 0.1050, 0.1229, 0.1470, 0.1680},
                            {0.0506, 0.0770, 0.1050, 0.1229, 0.1470, 0.1680},
                            {0.0506, 0.0770, 0.1050, 0.1229, 0.1470, 0.1680}};
                    this.constK = new double[][]{
                            {0, 992, 3096, 4640, 7164, 10322},
                            {0, 993, 3099, 4644, 7172, 10322},
                            {0, 1000, 3120, 4677, 7222, 10394}};

                    //(cpp max + ei max + BC1 amount) * .0506
                    this.claimAmount = new double[]{10276, 9869, 9938};

                    this.taxReduction = new double[][]{
                            {18181, 409, 0.032},  //under 18181 gets 409 over gets 409 - difference * %3.2
                            {18200, 409, 0.032},
                            {18327, 412, 0.032}};

                    //May 4, 2015
                    this.wageRates = new double[]{
                            22.60, 25.88, 27.94, 29.99, 32.04, 34.10, 36.97, 41.08, 46.42, 48.47
                    };
                    this.wageNames = new String[]{
                            "Pre-App", "First Term", "Second Term", "Third Term", "Fourth Term",
                            "Fifth Term", "Sixth Term", "Journeyman", "Foreman", "GF"
                    };
                    this.defaultWageIndex = 7d; //Journeyman
                    this.vacRate = new double[]{0.12d};

                    break;
                //=====================================AB====================================
                case PROV_AB:
                    this.surName = "AB - ";
                    this.rates = new double[][]{{0.10}, {0.10}, {0.10}};

                    //(cpp max + ei max + AB1) * 0.1
                    this.claimAmount = new double[]{17593, 17787, 18214};
                    //----------------------------------Wages AB---------------------------------
                    //Updated May 3 2015
                    this.wageRates = new double[]{
                            33.15, 26.16, 33.15, 40.15, 44.06, 44.81, 47.96, 50.31, 52.31
                    };
                    this.wageNames = new String[]{
                            "Helper", "1st Year", "2nd Year", "3rd Year", "Journeyman (S)",
                            "Journeyman (N)", "Lead Hand", "Foreman", "GF"
                    };
                    this.defaultWageIndex = 5d; //Journeyman (N)
                    this.vacRate = new double[]{0.10d};
                    break;
                //=====================================MB====================================
                case PROV_MB:
                    this.surName = "MB - ";

                    this.brackets = new double[][]{
                            {0, 31000, 67000},
                            {0, 31000, 67000},
                            {0, 31000, 67000}};
                    this.rates = new double[][]{
                            {0.108, 0.1275, 0.174},
                            {0.108, 0.1275, 0.174},
                            {0.108, 0.1275, 0.174}
                    };
                    this.constK = new double[][]{
                            {0, 605, 3720},
                            {0, 605, 3720},
                            {0, 605, 3720}};

                    this.claimAmount = new double[]{8884, 9134, 9134};

                    //-------------------------------Wage Rates---------------------------------
                    //May 3, 2015
                    this.wageRates = new double[]{
                            26.69, 21.06, 26.69, 32.32, 36.07, 37.57, 41.32, 45.07
                    };
                    this.wageNames = new String[]{
                            "Helper", "1st Year", "2nd Year", "3rd Year", "Journeyman",
                            "Ass't Foreman", "Foreman", "GF"
                    };
                    this.defaultWageIndex = 4d;
                    this.vacRate = new double[]{0.105d};
                    break;



                //=====================================ON====================================
                case PROV_ON:
                    this.surName = "ON - ";
                    this.brackets = new double[][]{
                            {0, 39723, 79448, 509000},
                            {0, 40120, 80242, 514090},
                            {0, 40922, 81847, 150000, 220000}};
                    this.rates = new double[][]{
                            {0.0505, 0.0915, 0.1116, 0.1316},
                            {0.0505, 0.0915, 0.1116, 0.1316},
                            {0.0505, 0.0915, 0.1116, 0.1216, 0.1316}};
                    this.constK = new double[][]{
                            {0, 1629, 3226, 13406},
                            {0, 1645, 3258, 13540},
                            {0, 1678, 3323, 4823, 7023}};

                    this.claimAmount = new double[]{9574, 9670, 9863};
                    this.taxReduction = new double[][]{
                            {221},  //basic personal amount
                            {223},
                            {228}
                    };
                    this.healthPrem = new double[][]{  //doesn't support years yet
                            {20000, 36000, 48000, 72000, 200000},
                            {0.06, 0.06, 0.25, 0.25, 0.25},
                            {300, 450, 600, 750, 900}
                    };
                    this.surtax = new double[][]{
                            {4289, 5489, 0.2, 0.36},
                            {4331, 5543, 0.2, 0.36},
                            {4418, 5654, 0.2, 0.36}
                    };
                    //---------------------------------Wages Ontario---------------------------------------
                    this.wageRates = new double[]{
                            27.79, 23.54, 27.79, 32.05, 36.3, 40.56, 42.56, 45.56, 47.56
                    };
                    this.wageNames = new String[]{
                            "Helper", "1st Year", "2nd Year", "3rd Year", "4th Year", "Journeyman",
                            "Ass't Foreman", "Foreman", "GF"
                    };
                    this.defaultWageIndex = 5d;
                    this.vacRate = new double[]{0.12d};
                    break;

                //=====================================NB===========================================
                case PROV_NB:
                    this.surName = "NB - ";
                    this.brackets = new double[][]{
                            {0, 38954, 77908, 126662},
                            {0, 39305, 78609, 127802},
                            {0, 39973, 79946, 129975}};
                    this.rates = new double[][]{
                            {0.0910, 0.1210, 0.1240, 0.1430},
                            {0.0968, 0.1482, 0.1652, 0.1784},
                            {0.0968, 0.1482, 0.1652, 0.1784}};
                    this.constK = new double[][]{
                            {0, 1169, 1402, 3809},
                            {0, 2020, 3357, 5044},
                            {0, 2055, 3414, 5129}};

                    //Claim code 1
                    this.claimAmount = new double[]{9388, 9472, 9633};

                    //---------------------------------Wages NB-------------------------------------
                    //July 5, 2015
                    this.wageRates = new double[]{
                            27.45, 21.56, 27.45, 33.34, 37.26, 38.46, 42.01, 43.76
                    };
                    this.vacRate = new double[] {
                            0.0230+0.0077, 0.0178+0.0059, 0.0230+0.0077, 0.0281+0.0094, 0.0315+0.0105,
                            0.0326+0.0109, 0.0357+0.0119, 0.0373+0.0124};
                    this.wageNames = new String[]{
                            "Helper", "1st Year", "2nd Year", "3rd Year", "Journeyman",
                            "Ass't Foreman", "Foreman", "GF"
                    };
                    this.defaultWageIndex = 4d;

                    break;

                //=====================================NS (Mainland)================================
                case PROV_NS: //The tax that time forgot
                    this.surName = "NS - ";
                    this.brackets = new double[][]{
                            {0, 29590, 59180, 93000, 150000},
                            {0, 29590, 59180, 93000, 150000},
                            {0, 29590, 59180, 93000, 150000}};
                    this.rates = new double[][]{
                            {0.0879, 0.1495, 0.1667, 0.1750, 0.2100},
                            {0.0879, 0.1495, 0.1667, 0.1750, 0.2100},
                            {0.0879, 0.1495, 0.1667, 0.1750, 0.2100}};
                    this.constK = new double[][]{
                            {0, 1823, 2841, 3613, 8863},
                            {0, 1823, 2841, 3613, 8863},
                            {0, 1823, 2841, 3613, 8863}};

                    //Claim code 1
                    this.claimAmount = new double[]{8481, 8481, 8481};

                    //---------------------------------Wages NS Mainland----------------------------
                    //July 5, 2015
                    this.wageRates = new double[]{
                         26.52, 20.82, 26.52, 32.22, 36.02, 37.22, 39.27, 41.52
                    };
                    this.vacRate = new double[] {
                            0.0221+0.0074, 0.0172+0.0057, 0.0221+0.0074, 0.0271+0.0090, 0.0304+0.0101,
                            0.0315+0.0105, 0.0333+0.0111, 0.0353+0.0118};
                    this.wageNames = new String[]{
                            "Helper", "1st Year", "2nd Year", "3rd Year", "Journeyman",
                            "Ass't Foreman", "Foreman", "GF"
                    };
                    this.defaultWageIndex = 4d;

                    break;
                //===================================NS (Cape Breton)===============================
                case PROV_CB:
                    this.surName = "CB - ";

                    //Tax always goes to NS function

                    //---------------------------------Wages NS Cape Breton-------------------------
                    //July 5, 2015
                    this.wageRates = new double[]{
                            28.61, 22.49, 28.61, 34.73, 38.81, 40.01, 41.81, 43.56
                    };
                    this.vacRate = new double[] {
                            0.0240+.008, 0.0187+0.0062, 0.0240+.008, 0.0293+0.0098, 0.0328+0.0109,
                            0.0339+0.0113, 0.0355+0.0118, 0.0371+0.0124};
                    this.wageNames = new String[]{
                            "Helper", "1st Year", "2nd Year", "3rd Year", "Journeyman",
                            "Ass't Foreman", "Foreman", "GF"
                    };
                    this.defaultWageIndex = 4d;
                    break;

                //=====================================PEI==========================================
                case PROV_PE:  //Another one forgotten by time
                    this.surName = "PEI - ";
                    this.brackets = new double[][]{
                            {0, 31984, 63969},
                            {0, 31984, 63969},
                            {0, 31984, 63969}};
                    this.rates = new double[][]{
                            {0.0980, 0.1380, 0.1670},
                            {0.0980, 0.1380, 0.1670},
                            {0.0980, 0.1380, 0.1670}};
                    this.constK = new double[][]{
                            {0, 1279, 3134},
                            {0, 1279, 3134},
                            {0, 1279, 3134}};

                    //Claim code 1
                    this.claimAmount = new double[]{7708, 7708, 7708};

                    //any prov tax over 12500, 10% added
                    this.surtax = new double[][]{
                            {12500, 0.10},
                            {12500, 0.10},
                            {12500, 0.10}
                    };

                    //-------------------------Wages PEI (Mainland NS)----------------------
                    this.wageRates = new double[]{
                            26.52, 20.82, 26.52, 32.22, 36.02, 37.22, 39.27, 41.52
                    };
                    this.vacRate = new double[] {
                            0.0221+0.0074, 0.0172+0.0057, 0.0221+0.0074, 0.0271+0.0090, 0.0304+0.0101,
                            0.0315+0.0105, 0.0333+0.0111, 0.0353+0.0118};
                    this.wageNames = new String[]{
                            "Helper", "1st Year", "2nd Year", "3rd Year", "Journeyman",
                            "Ass't Foreman", "Foreman", "GF"
                    };
                    this.defaultWageIndex = 4d;

                    break;
            }
        }
	}

	private static final double[][] cppEi = {
        {0.0495, 3500, 0.0188, 2356.20, 891.12},
        {0.0495, 3500, 0.0188, 2425.50, 913.68},
        {0.0495, 3500, 0.0188, 2479.95, 930.60}
        };

	*/
