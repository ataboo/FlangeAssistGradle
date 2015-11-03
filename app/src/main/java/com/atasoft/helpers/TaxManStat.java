package com.atasoft.helpers;

//Forked from taxManager for possible JSON Integration


import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;

//TODO: Lose static Integers for String calls

//----Tax Manager holds tax and wage values by province and year---------
public class TaxManStat {
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

    private JSONObject masterObj;
    public static final String LOG_KEY = "TaxManStat";

    //can order outputs too
    public static final int[] activeProvinces = {PROV_BC, PROV_AB, PROV_MB, PROV_ON, PROV_NB, PROV_NS, PROV_CB, PROV_PE};

    public static final String[] provinceNames = {"British Columbia", "Alberta", "Saskatchewan",
            "Manitoba", "Ontario", "Quebec", "New Brunswick", "Nova Scotia (Mainland)", "Nova Scotia (Cape Breton)", "Prince Edward Island",
            "Newfoundland", "Federal"};

    public static final String[] yearStrings = {"2013", "2014", "2015"};

    private static final int bdPrecision = 5;
    private static final int bdRounding = BigDecimal.ROUND_HALF_EVEN;

    public static class FieldKeys{
        public static final String rates = "rates";
        public static final String brackets = "brackets";
        public static final String constK = "constK";
        public static final String taxReduction = "taxReduction";
        public static final String healthPrem = "healthPrem";
        public static final String surtax = "surtax";
        public static final String wageRates = "wageRates";
        public static final String wageNames = "wageNames";
        public static final String surName = "surName";
        public static final String defaultWageIndex = "defaultWageIndex";
        public static final String vacRate = "vacRate";
        public static final String claimAmount = "claimAmount";

    }

	//Used as a container for tables for each type of tax
	public class TaxStats{
		public double[][] rates;
		public double[][] brackets;
		public double[][] constK;
		public double[][] taxReduction;
		public double[][] healthPrem;
		public double[][] surtax;
		public double[] wageRates;
		public String[] wageNames;
        public String surName;
		public double defaultWageIndex;  //tacked on end of wageRates in place of custom value
		public double[] vacRate;
        public double[] claimAmount;

        public TaxStats(int type, JSONObject masterObj) {
            if(masterObj == null){
                Log.e(LOG_KEY, "masterObj is null");
                return;
            }

            try {
                JSONObject provObj = masterObj.getJSONObject(provinceNames[type]);
                this.brackets = getMultiArray(provObj.getJSONObject(FieldKeys.brackets), yearStrings);
                Log.w(LOG_KEY, "brackets 1,1 is: " + Double.toString(this.brackets[1][1]));

            } catch (Exception e){
                e.printStackTrace();
            }
        }

        private double[][] getMultiArray(JSONObject jObj, String[] yearNames) throws JSONException{
            double[][] retArr = new double[yearNames.length][];

            for(int i=0; i < yearNames.length; i++){
                JSONArray jArr = jObj.getJSONArray(yearNames[i]);
                double[] innerArr = new double[jArr.length()];
                for(int j=0; j<jArr.length(); j++){
                    innerArr[j] = jArr.getDouble(j);
                }

                retArr[i] = innerArr;
            }
            return retArr;
        }
	}

	private static final double[][] cppEi = {
        {0.0495, 3500, 0.0188, 2356.20, 891.12},
        {0.0495, 3500, 0.0188, 2425.50, 913.68},
        {0.0495, 3500, 0.0188, 2479.95, 930.60}
        };

    public TaxManStat(String provinceName) {
        //lightStatValidation();

        this.masterObj = JsonPuller.loadJSON("TaxVals.json");
        this.fedStats = new TaxStats(FED, masterObj);
        int provIndex = getProvinceIndexFromName(provinceName);
        getStatType(provIndex); //generates taxStatsHold for province
    }

    private void lightStatValidation() {
        for(int provInt: activeProvinces) {
            TaxStats provStats = new TaxStats(provInt, masterObj);
            Log.w("TaxManager", "Validating " + provinceNames[provInt]);

            int yearCount = yearStrings.length;
            if(provStats.claimAmount != null) {
                if (provStats.claimAmount.length != yearCount) {
                    throw new RuntimeException("TaxStatsError: claimAmount array length mismatch in " + provinceNames[provInt]);
                }
            }


            for(int i = 0; i<yearCount; i++) {
                 if(provStats.brackets != null) {
                     if (provStats.brackets[i].length != provStats.rates[i].length ||
                             provStats.brackets[i].length != provStats.constK[i].length) {
                         throw new RuntimeException(String.format(
                                 "TaxStatsError: Bracket/Rate/ConstK array length mismatch in %s", provinceNames[provInt]));
                     }
                 }
                 if(provStats.wageRates != null) {
                    if(provStats.wageRates.length != provStats.wageNames.length) {
                        throw new RuntimeException(String.format(
                                "TaxStatsError: wageRates/wageNames array length mismatch in %s", provinceNames[provInt]));
                    }
                    if(provStats.vacRate.length != 1 && provStats.vacRate.length != provStats.wageRates.length){
                        throw new RuntimeException("TaxStatsError: vacRate should have a length of 1 or wageRates.length in " + provinceNames[provInt]);
                    }
                 }

            }
        }

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

	private BigDecimal getBCTax(BigDecimal anGrossDec, int year){
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

    private TaxStats fedStats;
    private TaxStats bcHoldStats;
    private TaxStats abHoldStats;
    private TaxStats mbHoldStats;
    private TaxStats onHoldStats;
    private TaxStats nbHoldStats;
    private TaxStats nsHoldStats;
    private TaxStats cbHoldStats;
    private TaxStats peHoldStats;
    private TaxStats getStatType(int province){
        switch(province){
            case PROV_BC:
                bcHoldStats = (bcHoldStats == null) ? new TaxStats(province, masterObj) : bcHoldStats;
                return bcHoldStats;
            case PROV_AB:
                abHoldStats = (abHoldStats == null) ? new TaxStats(province, masterObj) : abHoldStats;
                return abHoldStats;
            case PROV_ON:
                onHoldStats = (onHoldStats == null) ? new TaxStats(province, masterObj) : onHoldStats;
                return onHoldStats;
            case PROV_MB:
                mbHoldStats = (mbHoldStats == null) ? new TaxStats(province, masterObj) : mbHoldStats;
                return mbHoldStats;
            case PROV_NB:
                nbHoldStats = (nbHoldStats == null) ? new TaxStats(province, masterObj) : nbHoldStats;
                return nbHoldStats;
            case PROV_NS:
                nsHoldStats = (nsHoldStats == null) ? new TaxStats(province, masterObj) : nsHoldStats;
                return nsHoldStats;
            case PROV_CB:
                cbHoldStats = (cbHoldStats == null) ? new TaxStats(province, masterObj) : cbHoldStats;
                return cbHoldStats;
            case PROV_PE:
                peHoldStats = (peHoldStats == null) ? new TaxStats(province, masterObj) : peHoldStats;
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
                cppEiDec[i] = BigDecimal.valueOf(cppEi[year][i + 3]);
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

    public HashMap<String, TaxStats> getTaxStatsList(){
       HashMap<String, TaxStats> taxList = new HashMap<String, TaxStats>();
        taxList.put(provinceNames[FED], fedStats);
        for(int provInt: activeProvinces) {
            taxList.put(provinceNames[provInt], getStatType(provInt));
            Log.w("TaxManager", "Added " + provinceNames[provInt] + " to taxList");
        }
        return taxList;
    }
}
