package com.atasoft.flangeassist.fragments.paycalc;


import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;

//----Tax Manager holds tax and wage values by province and year---------
public class TaxManager {

    //Display name, surname, is usable to calc, index
    public enum Prov {
        BC ("British Columbia", "BC - ", true, 0),
        AB ("Alberta", "AB - ", true, 1),
        SK ("Saskatchewan", "SK - ", true, 2),
        MB ("Manitoba", "MB - ", true, 3),
        ON ("Ontario", "ON - ", true, 4),
        QC ("Quebec", "QC - ", true, 5),
        NB ("New Brunswick", "NB - ", true, 6),
        NS ("Nova Scotia", "NS - ", true, 7),
        CB ("Cape Breton", "CB - ", true, 8),
        PE ("Prince Edward Island", "PE - ", true, 9),
        NL ("Newfoundland", "NL - ", true, 10),
        FED ("Federal", "FED - ", false, 11);

        private String displayName;
        private boolean isActive;
        private int index;
        private String surname;
        Prov(String name, String surName, boolean active, int index){
            this.displayName = name;
            this.isActive = active;
            this.index = index;
            this.surname = surName;
        }

        public String getName(){return displayName;}

        public int getIndex(){
            return index;
        }

        public String getSurname(){return surname;}

        public static String[] getActiveProvinceNames(){
            ArrayList<String> nameList = new ArrayList<>();
            for(Prov p: Prov.values()){
                if(p.isActive)
                    nameList.add(p.displayName);
            }
            String[] retArr = new String[nameList.size()];
            nameList.toArray(retArr);
            return retArr;
        }

        public static Prov getProvFromName(String provName){
            for(Prov prov: Prov.values()){
                if(provName.equals(prov.getName())) return prov;
            }
            Log.e("TaxManager", "Couldn't find province matching: "+ provName + " returned Fed.");
            return Prov.FED;
        }
    }
    public enum TaxYear {
        TY_2013 ("2013", 0),
        TY_2014 ("2014", 1),
        TY_2015 ("2015", 2),
        TY_2016 ("2016", 3);

        private String name;
        private int index;

        TaxYear(String name, int index){
            this.name = name;
            this.index = index;
        }

        public String getName(){
            return name;
        }

        public int getIndex(){
            return index;
        }

        public static String[] getYearStrings(){
            TaxYear[] values = TaxYear.values();
            String[] yearStrings = new String[values.length];

            for(int i=0; i<values.length; i++){
                yearStrings[i] = values[i].getName();
            }

            return yearStrings;
        }
    }

    public static final String defaultWageName = "Journeyperson";

    private TaxStatHolder fedStats;
    private TaxStatHolder provStats;

    private AssetManager assets;

    public static final String[] yearStrings = TaxYear.getYearStrings();

    private static final int bdPrecision = 5;
    private static final int bdRounding = BigDecimal.ROUND_HALF_EVEN;

    public TaxManager(String provName, AssetManager assets) {
        this.assets = assets;

        fedStats = new TaxStatHolder(Prov.FED, assets);
        getStatType(Prov.getProvFromName(provName));
    }

	public String[] getWageNames(String province) {
		TaxStatHolder stats = getStatType(province);

        String[] wageNames = stats.wageNames;

        String surName = stats.surName;
		String[] retString = new String[wageNames.length + 1];
		for(int i=0; i < wageNames.length; i++){
			retString[i] = surName + wageNames[i];
		}
		retString[retString.length-1] = "Custom";
		return retString;
	}

	public float[] getWageRates(String province){
        TaxStatHolder activeRate = getStatType(province);

        //Log.w("TaxManager", String.format("getting wage rate for: %s.", province));

        float[] wageRates = activeRate.wageRates;
        float custVal = activeRate.defaultWageIndex;
		float[] retDoub = new float[wageRates.length + 1];
        System.arraycopy(wageRates, 0, retDoub, 0, wageRates.length);
		retDoub[retDoub.length-1] = custVal;
       // Log.w("TaxManager", "Packaged " + custVal + " as custVal");
		return retDoub;
	}

	//Returns [fed, prov, cpp, ei]
	public float[] getTaxes(float gross, float dues, int year, Prov prov) {
		BigDecimal provTax;

        BigDecimal anGross = new BigDecimal(gross).setScale(bdPrecision, bdRounding).multiply(new BigDecimal(52));
        BigDecimal anGrossTaxable = new BigDecimal(gross - dues).setScale(bdPrecision, bdRounding).multiply(new BigDecimal(52));
        switch(prov){
            case BC:
                provTax = getBCTax(anGrossTaxable, year);
                break;
            case AB:
                provTax = getABTax(anGrossTaxable, year);
                break;
            case SK:
                provTax = getSKTax(anGrossTaxable, year);
                break;
            case ON:
                provTax = getONTax(anGrossTaxable, year);
                break;
            case MB:
                provTax = getMBTax(anGrossTaxable, year);
                break;
            case QC:
                provTax = getQCTax(anGrossTaxable, year);
                break;
            case NB:
                provTax = getNBTax(anGrossTaxable, year);
                break;
            case NS:
                provTax = getNSTax(anGrossTaxable, year, false);
                break;
            case CB:
                provTax = getNSTax(anGrossTaxable, year, true);
                break;
            case PE:
                provTax = getPEITax(anGrossTaxable, year);
                break;
            case NL:
                provTax = getNLTax(anGrossTaxable, year);
                break;
            default:  //No Provincial Tax (FED)
                provTax = BigDecimal.ZERO;
                break;
        }

        BigDecimal[] cppEiDec = getCppEi(anGross, year);

        if(prov == Prov.QC) {
            cppEiDec = getQppQpip(anGross, year);
        }

        BigDecimal fiftyTwo = new BigDecimal(52);

        BigDecimal fedTaxDec = getFedTax(anGrossTaxable, year);
        // x.compare(y) ==  -1:(x<y), 0:(x==y), 1:(x>y)
        if(fedTaxDec.compareTo(BigDecimal.ZERO) < 0) fedTaxDec = BigDecimal.ZERO;
        if(provTax.compareTo(BigDecimal.ZERO) < 0) provTax = BigDecimal.ZERO;

        float fedTax = fedTaxDec.divide(fiftyTwo, 2, bdRounding).floatValue();
        float provTaxFl = provTax.divide(fiftyTwo, 2, bdRounding).floatValue();
        float cppFl = cppEiDec[0].divide(fiftyTwo, 2, bdRounding).floatValue();
        float eiFl = cppEiDec[1].divide(fiftyTwo, 2, bdRounding).floatValue();

        return new float[]{fedTax, provTaxFl, cppFl, eiFl};
	}

    public float[] getTaxes(float gross, float dues, String year, String province){
        //Log.w("TaxManager", "Ran get Taxes... again.");
        return getTaxes(gross, dues, getYearIndexFromName(year), Prov.getProvFromName(province));
    }

    public float getVacationRate(String province){
        TaxStatHolder stats = getStatType(province);

        return stats.vacRate;
    }

    public float getNightPremium(String province){
        TaxStatHolder stats = getStatType(province);

        return stats.nightPremiumRate;
    }

    public boolean getNightOT(String province){
        TaxStatHolder stats = getStatType(province);

        return stats.nightOT;
    }

    public boolean getDoubleOT(String province){
        TaxStatHolder stats = getStatType(province);

        return stats.doubleOT;
    }

    public float getMonthlyDues(String province){
        TaxStatHolder stats = getStatType(province);
        return stats.monthDuesRate;
    }

    public float getFieldDues(String province){
        TaxStatHolder stats = getStatType(province);
        return stats.fieldDuesRate;
    }

	private BigDecimal[] getCppEi(BigDecimal anGross, int year){
		//[cpp rate, exemption, ei rate]
		float cppRate = fedStats.cppEi[year][0];
		float cppExempt = fedStats.cppEi[year][1];
		float eiRate = fedStats.cppEi[year][2];

        BigDecimal cppRet = anGross.subtract(new BigDecimal(cppExempt)).
                multiply(new BigDecimal(cppRate)).setScale(bdPrecision, bdRounding);
        if(cppRet.compareTo(BigDecimal.ZERO) < 0) cppRet = BigDecimal.ZERO;

        BigDecimal eiRet = anGross.multiply(new BigDecimal(eiRate)).setScale(bdPrecision, bdRounding);
        if(eiRet.compareTo(BigDecimal.ZERO) < 0) eiRet = BigDecimal.ZERO;

		return new BigDecimal[]{cppRet, eiRet};
	}

    private BigDecimal[] getQppQpip(BigDecimal anGross, int year){
        TaxStatHolder qcStats = getStatType(Prov.QC);
        float cppExempt = fedStats.cppEi[year][1];

        float gross = anGross.floatValue();

        float qppDed = (gross - cppExempt) * qcStats.qppRate[year];
        float qpipDed = gross * qcStats.qpipRate[year];

        qppDed = qppDed > 0 ? qppDed : 0;
        qpipDed = qpipDed > 0 ? qpipDed : 0;

        return new BigDecimal[]{new BigDecimal(qppDed), new BigDecimal(qpipDed)};
    }

    private BigDecimal getFedTax(BigDecimal anGross, int year){
        float[] bracket = fedStats.brackets[year];
        float anGrossfloat = anGross.floatValue();
        int taxIndex = bracketGrossIndex(anGrossfloat, bracket);
        //Log.w("TaxManager", "taxIndex: " + taxIndex + " rate is: " + fedStats.rates[year][taxIndex]);

        return anGross.multiply(BigDecimal.valueOf(fedStats.rates[year][taxIndex])).
                subtract(getTaxCredit(fedStats, anGross, year)).
                subtract(BigDecimal.valueOf(fedStats.constK[year][taxIndex]));
    }

	private  BigDecimal getBCTax(BigDecimal anGrossDec, int year){
		TaxStatHolder bcStats = getStatType(Prov.BC);
        if(bcStats == null){
            Log.e("TaxManager", "Failed to get bcStats, received null.");
            return BigDecimal.ZERO;
        }
        BigDecimal taxDec = getStandardProvincialTax(anGrossDec, year, bcStats);

        //BC Tax Reduction
        float[] redTable = bcStats.taxReduction[year];	//[bracket, credit, drop rate]
        float taxRedLowerBracker = redTable[0];
        float taxRedFlatAmount = redTable[2];
        float taxRedRate = redTable[3];
        float diff = anGrossDec.floatValue() - taxRedLowerBracker;

        float taxRed = (diff < 0) ? taxRedFlatAmount : taxRedFlatAmount - taxRedRate * diff;
        if(taxRed < 0) taxRed = 0;

        return taxDec.subtract(BigDecimal.valueOf(taxRed));
    }

	private BigDecimal getABTax(BigDecimal anGross, int year){
        TaxStatHolder abStats = getStatType(Prov.AB);

        return getStandardProvincialTax(anGross, year, abStats);
	}

    private BigDecimal getSKTax(BigDecimal anGrossDec, int year){
        TaxStatHolder skStats = getStatType(Prov.SK);
        return getStandardProvincialTax(anGrossDec, year, skStats);
    }

    private BigDecimal getQCTax(BigDecimal anGrossDec, int year){
        TaxStatHolder qcStats = getStatType(Prov.QC);

        float annualTax = cumulativeBracketTax(anGrossDec, qcStats, year);
        annualTax -= (qcStats.claimAmount[year]) * 0.20;

        return new BigDecimal(annualTax);
    }

    private float cumulativeBracketTax(BigDecimal anGross, TaxStatHolder taxStatHolder, int year){
        float gross = anGross.floatValue();
        float[] bracket = taxStatHolder.brackets[year];
        float[] rates = taxStatHolder.rates[year];

        float taxPayable = 0f;
        for(int i=0; i< bracket.length - 1; i++){
            float roofBracket = bracket[i+1];
            float floorBracket = bracket[i];

            if(gross > roofBracket){
                taxPayable += (roofBracket - floorBracket) * rates[i];

                if(i == bracket.length -2){
                    Log.w("TaxManager", String.format("i == %d, bracket.length-1 == %d", i, bracket.length - 1));

                    taxPayable += (gross - roofBracket) * rates[i+1];
                    return taxPayable;
                }
            } else {
                taxPayable += (gross - floorBracket) * rates[i];
                return taxPayable;
            }
        }

        Log.e("TaxManager", "cumulativeBracketTax should not have fell through the for loop.");
        return 0f;
    }

	private BigDecimal getONTax(BigDecimal anGrossDec, int year){
        TaxStatHolder onStats = getStatType(Prov.ON);

        float anGross = anGrossDec.floatValue();
        float[] bracket = onStats.brackets[year];
        int taxIndex = bracketGrossIndex(anGross, bracket);

		float rate = onStats.rates[year][taxIndex];  //Rate and constant will share same index
		float constK = onStats.constK[year][taxIndex];
        BigDecimal taxPayable = anGrossDec.multiply(BigDecimal.valueOf(rate))
                .subtract(BigDecimal.valueOf(constK))
                .subtract(getTaxCredit(onStats, anGrossDec, year));
		float taxTotal = taxPayable.floatValue();

        //TODO: wade into ontarioSpecific to convert to BigDecimal
		taxTotal = ontarioSpecific(anGross, year, taxTotal, onStats);	//includes health premium, surcharge
		return BigDecimal.valueOf(taxTotal);
	}

    //Your tax law is terrible.
	private static float ontarioSpecific(float anGross, int year, float taxPayable, TaxStatHolder onStats){
		//apply surtax
		float[] surBracket = {onStats.surtax[year][0], onStats.surtax[year][1]};
		float[] surRate = {onStats.surtax[year][2], onStats.surtax[year][3]};
		float surTax = (taxPayable < surBracket[0]) ? 0 :
			(taxPayable < surBracket[1] ? surRate[0] * (taxPayable - surBracket[0]) :
			surRate[0] * (taxPayable - surBracket[0]) + (taxPayable - surBracket[1]) * surRate[1]);
		taxPayable += surTax;
		//calc health premium
		float[] healthBracket = onStats.healthBracket[year];
		float[] healthRate = onStats.healthRate[year];
		float[] healthConst = onStats.healthAmount[year];
		float rateAmount = 0f;
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
		float persAmount = onStats.taxReduction[year][0] * 2 - taxPayable;
		taxPayable -= persAmount < 0 ? 0 : persAmount;

		//tax reduction relied on tax payable before health premium
		taxPayable += rateAmount;
		return taxPayable;
	}

    private BigDecimal getNBTax(BigDecimal anGrossDec, int year) {
        return getStandardProvincialTax(anGrossDec, year, getStatType(Prov.NB));
    }

    private BigDecimal getMBTax(BigDecimal anGrossDec, int year){
        return getStandardProvincialTax(anGrossDec, year, getStatType(Prov.MB));
    }

    private BigDecimal getNSTax(BigDecimal anGrossDec, int year, boolean cbFlag) {
        //So it doesn't have to jump between stat types
        TaxStatHolder provStats = cbFlag ? getStatType(Prov.CB): getStatType(Prov.NS);
        return getStandardProvincialTax(anGrossDec, year, provStats);
    }

    private BigDecimal getPEITax(BigDecimal anGrossDec, int year) {
        TaxStatHolder peStats = getStatType(Prov.PE);
        BigDecimal taxDec = getStandardProvincialTax(anGrossDec, year, peStats);

        //Surtax adds additional 10% to provtax over 12500
        float[] surtaxVals = peStats.surtax[year]; //[cap (12500), rate (0.10)]
        if(taxDec.compareTo(BigDecimal.valueOf(surtaxVals[0])) > 0) {
            BigDecimal surDec = taxDec.subtract(BigDecimal.valueOf(surtaxVals[0]));
            surDec = surDec.multiply(BigDecimal.valueOf(surtaxVals[1]));
            taxDec = taxDec.add(surDec);
        }

        return taxDec;
    }

    private BigDecimal getNLTax(BigDecimal anGrossDec, int year){
        TaxStatHolder nlStats = getStatType(Prov.NL);
        return getStandardProvincialTax(anGrossDec, year, nlStats);
    }

    private BigDecimal getStandardProvincialTax(BigDecimal anGrossDec, int year, TaxStatHolder provStats) {
        //get tax rate from bracket chart
        int brackIndex = bracketGrossIndex(anGrossDec.floatValue(), provStats.brackets[year]);

        BigDecimal taxDec = anGrossDec.multiply((BigDecimal.valueOf((provStats.rates[year][brackIndex]))));
        taxDec = taxDec.subtract((BigDecimal.valueOf(provStats.constK[year][brackIndex])));
        taxDec = taxDec.subtract(getTaxCredit(provStats, anGrossDec, year));
        return taxDec;
    }


    private static int bracketGrossIndex(float gross, float[] brackets){
        gross = gross < 0 ? 0f: gross; //you never know

        for(int i=brackets.length - 1; i>0;i--){
            if(gross > brackets[i]){
                //Log.w("TaxManager", String.format("Gross: %.2f is greater than the [%d] Bracket: %.2f",
                        //gross, i-1, brackets[i-1]));
                return i;
            }
        }
        return 0; //if gross is 0
    }


    private TaxStatHolder getStatType(Prov prov){
        if(provStats == null){
            provStats = new TaxStatHolder(prov, assets);
        } else{
            if(provStats.prov != prov)
                provStats = new TaxStatHolder(prov, assets);
        }
        return provStats;
    }

    private TaxStatHolder getStatType(String province){
        return getStatType(Prov.getProvFromName(province));
    }

    public static int getYearIndexFromName(String yearName){
        for(int i=0; i<yearStrings.length; i++){
            if(yearName.equals(yearStrings[i])) return i;
        }
        return yearStrings.length - 1;
    }
    
    public static boolean validatePrefs(SharedPreferences prefs){
        String[] activeProvinces = Prov.getActiveProvinceNames();
        boolean provFlag = false;
        boolean yearFlag = false;
        String provWage = prefs.getString("list_provWageNew", "fail");
        String year = prefs.getString("list_taxYearNew", "fail");

        /*
        for(int i=0; i<activeProvinces.length; i++){
            if(provWage.equals(activeProvinces[i])) provFlag = true;
        }
        */

        for(String provString: activeProvinces){
            if(provWage.equals(provString)) provFlag = true;
        }
        for(String yearString: yearStrings){
            if(year.equals(yearString)) yearFlag = true;
        }
        if(!provFlag) Log.e("TaxManager", "SharedPreference list_provWageNew was malformed as: " + provWage);
        if(!yearFlag) Log.e("TaxManager", "SharedPreference list_taxYearNew was malformed as: " + year);
        return (provFlag && yearFlag);
    }

    private BigDecimal getTaxCredit(TaxStatHolder stats, BigDecimal anGross, int year) {
        //taxCred == (cpp contribution + ei contribution + claimAmount) / lowest bracket

        //T4032 example doesn't account for cpp exemption but CRA calculator does.
        BigDecimal[] cppEiDec = getCppEi(anGross, year);

        //Log.w("TaxManager", "Cpp before is: " + cppEiDec[0].toString() + " EI before is: " + cppEiDec[1].toString());

        //cap cpp component at max contribution
        cppEiDec[0] = (cppEiDec[0].compareTo(BigDecimal.valueOf(fedStats.cppEi[year][3])) > 0) ?
            BigDecimal.valueOf(fedStats.cppEi[year][3]) : cppEiDec[0];
        //cap ei component at max contribution
        cppEiDec[1] = (cppEiDec[1].compareTo(BigDecimal.valueOf(fedStats.cppEi[year][4])) > 0) ?
                BigDecimal.valueOf(fedStats.cppEi[year][4]) : cppEiDec[1];

        BigDecimal result =  BigDecimal.valueOf(stats.claimAmount[year])
                .add(cppEiDec[0])
                .add(cppEiDec[1])
                .multiply(BigDecimal.valueOf(stats.rates[year][0]));
        //Log.w("TaxManager", "cpp is: " + cppEiDec[0].toString() + " ei is: "+ cppEiDec[1].toString()
               // +" Tax credit is: " + result.toString());
        return result;
    }

     /*
    private void lightStatValidation() {
        for(int provInt: activeProvinces) {
            TaxStatHolder provStats = new TaxStatHolder((provInt));
            Log.w("TaxManager", "Validating " + provinceNames[provInt]);

            int yearCount = yearStrings.length;
            if(provStats.claimAmount != null) {
                if (provStats.claimAmount.length != yearCount) {
                    throw new RuntimeException("TaxStatHolderError: claimAmount array length mismatch in " + provinceNames[provInt]);
                }
            }


            for(int i = 0; i<yearCount; i++) {
                 if(provStats.brackets != null) {
                     if (provStats.brackets[i].length != provStats.rates[i].length ||
                             provStats.brackets[i].length != provStats.constK[i].length) {
                         throw new RuntimeException(String.format(
                                 "TaxStatHolderError: Bracket/Rate/ConstK array length mismatch in %s", provinceNames[provInt]));
                     }
                 }
                 if(provStats.wageRates != null) {
                    if(provStats.wageRates.length != provStats.wageNames.length) {
                        throw new RuntimeException(String.format(
                                "TaxStatHolderError: wageRates/wageNames array length mismatch in %s", provinceNames[provInt]));
                    }
                    if(provStats.vacRate.length != 1 && provStats.vacRate.length != provStats.wageRates.length){
                        throw new RuntimeException("TaxStatHolderError: vacRate should have a length of 1 or wageRates.length in " + provinceNames[provInt]);
                    }
                 }

            }
        }

    }
    */
}
