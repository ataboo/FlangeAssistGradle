package com.atasoft.helpers;


import android.content.SharedPreferences;
import android.util.Log;

import java.math.BigDecimal;

//TODO: transition to BigDecimal, Lose static Integers for String calls

//----Tax Manager holds tax and wage values by province and year---------
public class TaxManager {
	//Tax Years
	public static final int TY_2013 = 0;
	public static final int TY_2014 = 1;
    public static final int TY_2015 = 2;

	//Provinces
	public static final int PROV_BC = 0;
    public static final int PROV_AB = 1;
    public static final int PROV_SK = 2;
    public static final int PROV_MB = 3;
    public static final int PROV_ON = 4;
    public static final int PROV_QC = 5;
    public static final int PROV_NB = 6;
    public static final int PROV_NS = 7;
    public static final int PROV_CB = 8;  //Cape Breton has different wage agreement
    public static final int PROV_PE = 9;
    public static final int PROV_NL = 10;
    public static final int FED = 11;

    //can order outputs too
    public static final int[] activeProvinces = {PROV_BC, PROV_AB, PROV_MB, PROV_ON, PROV_NB, PROV_NS, PROV_CB, PROV_PE};

    public static final String[] provinceNames = {"British Columbia", "Alberta", "Saskatchewan",
            "Manitoba", "Ontario", "Quebec", "New Brunswick", "Nova Scotia (Mainland)", "Nova Scotia (Cape Breton)", "Prince Edward Island",
            "Newfoundland", "Federal"};

    public static final String[] yearStrings = {"2013", "2014", "2015"};

    private static final int bdPrecision = 5;
    private static final int bdRounding = BigDecimal.ROUND_HALF_EVEN;
	
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
                            {0, 37568, 75138, 86268, 104754.01, 150000},
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

                    //May 4, 2014
                    this.wageRates = new double[]{
                            21.75, 24.91, 26.88, 28.86, 30.84, 32.81, 35.58, 39.53, 44.67, 46.64
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
                    //Updated November 2014
                    this.wageRates = new double[]{
                            32.24, 25.25, 32.24, 39.24, 43.15, 43.90, 47.05, 49.40, 51.40
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
                    this.wageRates = new double[]{
                            24.81, 19.37, 24.81, 30.25, 33.87, 35.37, 39.12, 42.87
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
                            26.05, 21.92, 26.05, 30.19, 34.32, 38.46, 40.46, 43.46, 45.46
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
                    this.wageRates = new double[]{
                            25.51, 19.83, 25.51, 31.18, 34.97, 36.17, 39.72, 41.47
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
                    this.wageRates = new double[]{
                            24.60, 19.10, 24.60, 30.10, 33.76, 34.96, 37.01, 39.26
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
                    this.wageRates = new double[]{
                            26.63, 20.73, 26.63, 32.53, 36.47, 37.67, 39.47, 41.22
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
                            24.60, 19.10, 24.60, 30.10, 33.76, 34.96, 37.01, 39.26
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

    public TaxManager(String provinceName) {
        int provIndex = getProvinceIndexFromName(provinceName);
        getStatType(provIndex); //generates taxStatsHold for province
    }

	public String[] getWageNames(String province) {
		TaxStats stats = getStatType(province);

        String[] wageNames = stats.wageNames;
        String surName = stats.surName;
		String[] retString = new String[wageNames.length + 1];
		for(int i=0; i < wageNames.length; i++){
			retString[i] = surName + wageNames[i];
		}
		retString[retString.length-1] = "Custom";
		return retString;
	}

	public double[] getWageRates(String province){
        TaxStats activeRate = getStatType(province);  //returns ex: bcStats

        double[] wageRates = activeRate.wageRates;
        double custVal = activeRate.defaultWageIndex;
		double[] retDoub = new double[wageRates.length + 1];
        System.arraycopy(wageRates, 0, retDoub, 0, wageRates.length);
		retDoub[retDoub.length-1] = custVal;
       // Log.w("TaxManager", "Packaged " + custVal + " as custVal");
		return retDoub;
	}

    public static String[] getActiveProvinceStrings(){
        String[] retArr = new String[activeProvinces.length];
        for(int i=0; i<activeProvinces.length; i++){
            retArr[i] = provinceNames[activeProvinces[i]];
        }
        return retArr;
    }


	//Returns [fed, prov, cpp, ei]
	public double[] getTaxes(double gross, int year, int province) {
		BigDecimal provTax;
		double anGross = gross * 52;

        //double fedTax = getFedTax(anGross, year);

        BigDecimal anGrossDec = new BigDecimal(gross)
                .setScale(bdPrecision, bdRounding)
                .multiply(new BigDecimal(52));
        switch(province){
            case PROV_BC:
                provTax = getBCTax(anGrossDec, year);
                break;
            case PROV_AB:
                provTax = getABTax(anGrossDec, year);
                break;
            case PROV_ON:
                provTax = getONTax(anGrossDec, year);
                break;
            case PROV_MB:
                provTax = getMBTax(anGrossDec, year);
                break;
            case PROV_NB:
                provTax = getNBTax(anGrossDec, year);
                break;
            case PROV_NS:
                provTax = getNSTax(anGrossDec, year);
                break;
            case PROV_CB:
                provTax = getNSTax(anGrossDec, year);
                break;
            case PROV_PE:
                provTax = getPEITax(anGrossDec, year);
                break;
            default:  //No Provincial Tax (FED)
                provTax = BigDecimal.ZERO;
                break;
        }

        BigDecimal[] cppEiDec = getCppEi(anGrossDec, year);
        BigDecimal fiftyTwo = new BigDecimal(52);

        BigDecimal fedTaxDec = getFedTax(anGrossDec, year);
        //Log.w("TaxManager", "Fed Tax is: " + fedTaxDec.toString());
        //Log.w("TaxManager", "Prov Tax is: " + provTax.toString());
        // x.compare(y) ==  -1:(x<y), 0:(x==y), 1:(x>y)
        if(fedTaxDec.compareTo(BigDecimal.ZERO) < 0) fedTaxDec = BigDecimal.ZERO;
        if(provTax.compareTo(BigDecimal.ZERO) < 0) provTax = BigDecimal.ZERO;

        double fedTax = fedTaxDec.divide(fiftyTwo, 2, bdRounding).doubleValue();
        double provTaxDoub = provTax.divide(fiftyTwo, 2, bdRounding).doubleValue();
        double cppDoub = cppEiDec[0].divide(fiftyTwo, 2, bdRounding).doubleValue();
        double eiDoub = cppEiDec[1].divide(fiftyTwo, 2, bdRounding).doubleValue();

        return new double[]{fedTax, provTaxDoub, cppDoub, eiDoub};
	}

    public double[] getTaxes(double gross, String year, String province){
        return getTaxes(gross,
                getYearIndexFromName(year),
                getProvinceIndexFromName(province));
    }

    public double[] getVacationRate(String province){
        TaxStats stats = getStatType(province);

        return stats.vacRate;
    }

	private BigDecimal[] getCppEi(BigDecimal anGross, int year){
		//[cpp rate, exemption, ei rate]
		double cppRate = cppEi[year][0];
		double cppExempt = cppEi[year][1];
		double eiRate = cppEi[year][2];

        BigDecimal cppRet = anGross.subtract(new BigDecimal(cppExempt)).
                multiply(new BigDecimal(cppRate)).setScale(bdPrecision, bdRounding);
        if(cppRet.compareTo(BigDecimal.ZERO) < 0) cppRet = BigDecimal.ZERO;

        BigDecimal eiRet = anGross.multiply(new BigDecimal(eiRate)).
                setScale(bdPrecision, bdRounding);
		return new BigDecimal[]{cppRet, eiRet};
	}

    private BigDecimal getFedTax(BigDecimal anGross, int year){
        double[] bracket = fedStats.brackets[year];
        double anGrossDouble = anGross.doubleValue();
        int taxIndex = bracketGrossIndex(anGrossDouble, bracket);
        //Log.w("TaxManager", "taxIndex: " + taxIndex + " rate is: " + fedStats.rates[year][taxIndex]);

        return anGross.multiply(BigDecimal.valueOf(fedStats.rates[year][taxIndex])).
                subtract(getTaxCredit(fedStats, anGross, year)).
                subtract(BigDecimal.valueOf(fedStats.constK[year][taxIndex]));
    }

	private  BigDecimal getBCTax(BigDecimal anGrossDec, int year){
		TaxStats bcStats = getStatType(PROV_BC);

        BigDecimal taxDec = getStandardProvincialTax(anGrossDec, year, bcStats);

        //BC Tax Reduction
        double[] redTable = bcStats.taxReduction[year];	//[bracket, credit, drop rate]
        double diff = anGrossDec.doubleValue() - redTable[0];
        double taxRed = (diff < 0) ? redTable[1] : redTable[1] - redTable[2] * diff;
        if(taxRed < 0) taxRed = 0;

        return taxDec.subtract(BigDecimal.valueOf(taxRed));
    }

	private BigDecimal getABTax(BigDecimal anGross, int year){
        TaxStats abStats = getStatType(PROV_AB);

        BigDecimal taxDec =  anGross.multiply(BigDecimal.valueOf(abStats.rates[year][0]))
                .subtract(getTaxCredit(abStats, anGross, year));
        return taxDec;
	}

	private BigDecimal getONTax(BigDecimal anGrossDec, int year){
        TaxStats onStats = getStatType(PROV_ON);

        double anGross = anGrossDec.doubleValue();
        double[] bracket = onStats.brackets[year];
        int taxIndex = bracketGrossIndex(anGross, bracket);

        /*
        if(year == TY_2013 || year == TY_2014) {
            taxIndex = (anGross < bracket[1]) ? 0 :
                    (anGross < bracket[2] ? 1 :
                            (anGross < bracket[3] ? 2 : 3));
        } else { //5th tax bracket in 2015... dicks
            taxIndex = (anGross < bracket[1]) ? 0 :
                    (anGross < bracket[2] ? 1 :
                            (anGross < bracket[3] ? 2 :
                                    (anGross < bracket[4]) ? 3 : 4));
        }
        */

		double rate = onStats.rates[year][taxIndex];  //Rate and constant will share same index
		double constK = onStats.constK[year][taxIndex];
        BigDecimal taxPayable = anGrossDec.multiply(BigDecimal.valueOf(rate))
                .subtract(BigDecimal.valueOf(constK))
                .subtract(getTaxCredit(onStats, anGrossDec, year));
		double taxTotal = taxPayable.doubleValue();

        //TODO: wade into ontarioSpecific to convert to BigDecimal
		taxTotal = ontarioSpecific(anGross, year, taxTotal, onStats);	//includes health premium, surcharge
		return BigDecimal.valueOf(taxTotal);
	}

    //Your tax law is terrible.
	private static double ontarioSpecific(double anGross, int year, double taxPayable, TaxStats onStats){
		//apply surtax
		double[] surBracket = {onStats.surtax[year][0], onStats.surtax[year][1]};
		double[] surRate = {onStats.surtax[year][2], onStats.surtax[year][3]};
		double surTax = (taxPayable < surBracket[0]) ? 0 :
			(taxPayable < surBracket[1] ? surRate[0] * (taxPayable - surBracket[0]) :
			surRate[0] * (taxPayable - surBracket[0]) + (taxPayable - surBracket[1]) * surRate[1]);
		taxPayable += surTax;
		//calc health premium
		double[] healthBracket = onStats.healthPrem[0];
		double[] healthRate = onStats.healthPrem[1];
		double[] healthConst = onStats.healthPrem[2];
		double rateAmount = 0d;
		if(anGross > healthBracket[0]) {
			int healthIndex = anGross < healthBracket[1] ? 0:
				(anGross < healthBracket[2] ? 1:
				(anGross < healthBracket[3] ? 2:
				(anGross < healthBracket[4] ? 3: 4)));
			rateAmount = (anGross - healthBracket[healthIndex]) * healthRate[healthIndex];
			rateAmount = healthIndex > 0 ? rateAmount + healthConst[healthIndex - 1] : rateAmount;
			rateAmount = healthConst[healthIndex] < rateAmount ? healthConst[healthIndex] : rateAmount;
		}
		//tax reduction
		double persAmount = onStats.taxReduction[year][0] * 2 - taxPayable;
		taxPayable -= persAmount < 0 ? 0 : persAmount;

		//tax reduction relied on tax payable before health premium
		taxPayable += rateAmount;
		return taxPayable;
	}

    private BigDecimal getNBTax(BigDecimal anGrossDec, int year) {
        return getStandardProvincialTax(anGrossDec, year, getStatType(PROV_NB));
    }

    private BigDecimal getMBTax(BigDecimal anGrossDec, int year){
        return getStandardProvincialTax(anGrossDec, year, getStatType(PROV_MB));
    }

    private BigDecimal getNSTax(BigDecimal anGrossDec, int year) {
        return getStandardProvincialTax(anGrossDec, year, getStatType(PROV_NS));
    }

    private BigDecimal getPEITax(BigDecimal anGrossDec, int year) {
        TaxStats peStats = getStatType(PROV_PE);
        BigDecimal taxDec = getStandardProvincialTax(anGrossDec, year, peStats);

        //Surtax adds additional 10% to provtax over 12500
        double[] surtaxVals = peStats.surtax[year]; //[cap (12500), rate (0.10)]
        if(taxDec.compareTo(BigDecimal.ZERO.valueOf(surtaxVals[0])) > 0) {
            BigDecimal surDec = taxDec.subtract(BigDecimal.valueOf(surtaxVals[0]));
            surDec = surDec.multiply(BigDecimal.valueOf(surtaxVals[1]));
            taxDec = taxDec.add(surDec);
        }

        return taxDec;
    }

    private BigDecimal getStandardProvincialTax(BigDecimal anGrossDec, int year, TaxStats provStats) {
        //get tax rate from bracket chart
        int brackIndex = bracketGrossIndex(anGrossDec.doubleValue(), provStats.brackets[year]);

        BigDecimal taxDec = anGrossDec.multiply((BigDecimal.valueOf((provStats.rates[year][brackIndex]))));
        taxDec = taxDec.subtract((BigDecimal.valueOf(provStats.constK[year][brackIndex])));
        taxDec = taxDec.subtract(getTaxCredit(provStats, anGrossDec, year));
        return taxDec;
    }

    //Should have set up earlier
    private static int bracketGrossIndex(double gross, double[] brackets){
        gross = gross < 0 ? 0d: gross; //you never know

        for(int i=brackets.length - 1; i>0;i--){
            if(gross > brackets[i]){
                //Log.w("TaxManager", String.format("Gross: %.2f is greater than the [%d] Bracket: %.2f",
                        //gross, i-1, brackets[i-1]));
                return i;
            }
        }
        return 0; //if gross is 0
    }

    private static final TaxStats fedStats = new TaxStats(FED);
    private TaxStats bcHoldStats;
    private TaxStats abHoldStats;
    private TaxStats mbHoldStats;
    private TaxStats onHoldStats;
    private TaxStats nbHoldStats;
    private TaxStats nsHoldStats;
    private TaxStats cbHoldStats;
    private TaxStats peHoldStats;
    private TaxStats getStatType(int province){
        TaxStats requestedStats;

        switch(province){
            case PROV_BC:
                bcHoldStats = (bcHoldStats == null) ? new TaxStats(province) : bcHoldStats;
                return bcHoldStats;
            case PROV_AB:
                abHoldStats = (abHoldStats == null) ? new TaxStats(province) : abHoldStats;
                return abHoldStats;
            case PROV_ON:
                onHoldStats = (onHoldStats == null) ? new TaxStats(province) : onHoldStats;
                return onHoldStats;
            case PROV_MB:
                mbHoldStats = (mbHoldStats == null) ? new TaxStats(province) : mbHoldStats;
                return mbHoldStats;
            case PROV_NB:
                nbHoldStats = (nbHoldStats == null) ? new TaxStats(province) : nbHoldStats;
                return nbHoldStats;
            case PROV_NS:
                nsHoldStats = (nsHoldStats == null) ? new TaxStats(province) : nsHoldStats;
                return nsHoldStats;
            case PROV_CB:
                cbHoldStats = (cbHoldStats == null) ? new TaxStats(province) : cbHoldStats;
                return cbHoldStats;
            case PROV_PE:
                peHoldStats = (peHoldStats == null) ? new TaxStats(province) : peHoldStats;
                return peHoldStats;
            default:
                return null;
        }
    }

    private TaxStats getStatType(String province){
        TaxStats provinceStats = getStatType(getProvinceIndexFromName(province));
        if(provinceStats == null){
            Log.e("TaxManager", "getStatType received invalid province string.  Returned default AB.");
            return getStatType(PROV_AB);
        }
        
        return provinceStats;
    }

    public static int getYearIndexFromName(String yearName){
        for(int i=0; i<yearStrings.length; i++){
            if(yearName.equals(yearStrings[i])) return i;
        }
        return yearStrings.length - 1;
    }

    public static int getProvinceIndexFromName(String provName){
        for(int i=0; i<provinceNames.length; i++){
            if(provName.equals(provinceNames[i])) return i;
        }
        return -1;
    }
    
    public static boolean validatePrefs(SharedPreferences prefs){
        boolean provFlag = false;
        boolean yearFlag = false;
        String provWage = prefs.getString("list_provWageNew", "fail");
        String year = prefs.getString("list_taxYearNew", "fail");
        for(int i=0; i<activeProvinces.length; i++){
            if(provWage.equals(provinceNames[activeProvinces[i]])) provFlag = true;
        }
        for(int i=0; i<yearStrings.length; i++){
            if(year.equals(yearStrings[i])) yearFlag = true;
        }
        if(!provFlag) Log.e("TaxManager", "SharedPreference list_provWageNew was malformed as: " + provWage);
        if(!yearFlag) Log.e("TaxManager", "SharedPreference list_taxYearNew was malformed as: " + year);
        return (provFlag && yearFlag);
    }

    private BigDecimal getTaxCredit(TaxStats stats, BigDecimal anGross, int year) {
        //taxCred == (cpp contribution + ei contribution + claimAmount) / lowest bracket

        //T4032 example doesn't account for cpp exemption but CRA calculator does.
        BigDecimal[] cppEiDec = getCppEi(anGross, year);

        //Log.w("TaxManager", "Cpp before is: " + cppEiDec[0].toString() + " EI before is: " + cppEiDec[1].toString());
        for(int i=0; i<cppEiDec.length; i++) {
            if (cppEiDec[i].compareTo(BigDecimal.valueOf(cppEi[year][i + 3])) > 0) {
                cppEiDec[i] = BigDecimal.valueOf(cppEi[year][i+3]);
            }
        }
        BigDecimal result =  BigDecimal.valueOf(stats.claimAmount[year])
                .add(cppEiDec[0])
                .add(cppEiDec[1])
                .multiply(BigDecimal.valueOf(stats.rates[year][0]));
        //Log.w("TaxManager", "cpp is: " + cppEiDec[0].toString() + " ei is: "+ cppEiDec[1].toString()
               // +" Tax credit is: " + result.toString());
        return result;
    }
}
