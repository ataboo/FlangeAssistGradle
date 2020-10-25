package com.atasoft.flangeassist.fragments.paycalc;

import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import com.atasoft.flangeassist.fragments.paycalc.strategy.ITaxStrategy;
import com.atasoft.flangeassist.fragments.paycalc.strategy.StrategyRepo;

import java.math.BigDecimal;

//----Tax Manager holds tax and wage values by province and year---------
public class TaxManager {

    public static final String defaultWageName = "Journeyperson";

    private TaxStatHolder fedStats;
    private TaxStatHolder provStats;

    private AssetManager assets;

    public static final String[] yearStrings = TaxYear.getYearStrings();

    private final StrategyRepo _strategyRepo;

    private static final int BD_PRECISION = 5;
    private static final int BD_ROUNDING = BigDecimal.ROUND_HALF_EVEN;

    public TaxManager(String provName, AssetManager assets) {
        this.assets = assets;
        this._strategyRepo = new StrategyRepo(assets);

        fedStats = new TaxStatHolder(Province.FED, assets);
        getStatType(Province.getProvFromName(provName));
    }

	public WageRate[] getWageRates(String province) {
		return _strategyRepo.get(province).GetWages();
	}

	//Returns [fed, prov, cpp, ei]
	public float[] getTaxes(float gross, float dues, int year, Province prov) {
		BigDecimal provTax;

        BigDecimal anGross = new BigDecimal(gross).setScale(BD_PRECISION, BD_ROUNDING).multiply(new BigDecimal(52));
        BigDecimal anGrossTaxable = new BigDecimal(gross - dues).setScale(BD_PRECISION, BD_ROUNDING).multiply(new BigDecimal(52));

        if (prov == Province.FED) {
            provTax = BigDecimal.ZERO;
        } else {
            ITaxStrategy provStrategy = _strategyRepo.get(prov);
            provTax = provStrategy.CalculateTax(anGross, year);
        }
//        switch(prov){
//            case BC:
//                provTax = getBCTax(anGrossTaxable, year);
//                break;
//            case AB:
//                provTax = getABTax(anGrossTaxable, year);
//                break;
//            case SK:
//                provTax = getSKTax(anGrossTaxable, year);
//                break;
//            case ON:
//                provTax = getONTax(anGrossTaxable, year);
//                break;
//            case MB:
//                provTax = getMBTax(anGrossTaxable, year);
//                break;
//            case QC:
//                provTax = getQCTax(anGrossTaxable, year);
//                break;
//            case NB:
//                provTax = getNBTax(anGrossTaxable, year);
//                break;
//            case NS:
//                provTax = getNSTax(anGrossTaxable, year, false);
//                break;
//            case CB:
//                provTax = getNSTax(anGrossTaxable, year, true);
//                break;
//            case PE:
//                provTax = getPEITax(anGrossTaxable, year);
//                break;
//            case NL:
//                provTax = getNLTax(anGrossTaxable, year);
//                break;
//            default:  //No Provincial Tax (FED)
//                provTax = BigDecimal.ZERO;
//                break;
//        }

        BigDecimal[] cppEi;

        if (prov == Province.QC) {
            cppEi = getQppQpip(anGross, year);
            cppEi[1] = cppEi[1].add(cppEi[2]);
        } else {
            cppEi = getCppEi(anGross, year);
        }

        BigDecimal fiftyTwo = new BigDecimal(52);

        BigDecimal fedTaxDec = prov == Province.QC ? getFedTax(anGrossTaxable, getStatType(Province.QC), year) :
                getFedTax(anGrossTaxable, year);

        if(fedTaxDec.compareTo(BigDecimal.ZERO) < 0) fedTaxDec = BigDecimal.ZERO;
        if(provTax.compareTo(BigDecimal.ZERO) < 0) provTax = BigDecimal.ZERO;

        float fedTaxPeriod = fedTaxDec.divide(fiftyTwo, 2, BD_ROUNDING).floatValue();
        float provTaxPeriod = provTax.divide(fiftyTwo, 2, BD_ROUNDING).floatValue();
        float cppPeriod = cppEi[0].divide(fiftyTwo, 2, BD_ROUNDING).floatValue();
        float eiPeriod = cppEi[1].divide(fiftyTwo, 2, BD_ROUNDING).floatValue();

        return new float[]{fedTaxPeriod, provTaxPeriod, cppPeriod, eiPeriod};
	}

    public float[] getTaxes(float gross, float dues, String year, String province){
        //Log.w("TaxManager", "Ran get Taxes... again.");
        return getTaxes(gross, dues, getYearIndexFromName(year), Province.getProvFromName(province));
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

    public TaxStatHolder getProvStats(String province) {
        return getStatType(province);
    }

	private BigDecimal[] getCppEi(BigDecimal anGross, int year){
		//[cpp rate, exemption, ei rate]
		float cppRate = fedStats.cppEi[year][0];
		float cppExempt = fedStats.cppEi[year][1];
		float eiRate = fedStats.cppEi[year][2];

        BigDecimal cppRet = anGross.subtract(new BigDecimal(cppExempt)).
                multiply(new BigDecimal(cppRate)).setScale(BD_PRECISION, BD_ROUNDING);
        if(cppRet.compareTo(BigDecimal.ZERO) < 0) cppRet = BigDecimal.ZERO;

        BigDecimal eiRet = anGross.multiply(new BigDecimal(eiRate)).setScale(BD_PRECISION, BD_ROUNDING);
        if(eiRet.compareTo(BigDecimal.ZERO) < 0) eiRet = BigDecimal.ZERO;

		return new BigDecimal[]{cppRet, eiRet};
	}

    private BigDecimal[] getQppQpip(BigDecimal anGross, int year){
        TaxStatHolder qcStats = getStatType(Province.QC);
        float cppExempt = fedStats.cppEi[year][1];

        float gross = anGross.floatValue();

        float qppDed = (gross - cppExempt) * qcStats.qppRate[year];
        float qpipDed = gross * qcStats.qpipRate[year];
        float eiDed = gross * qcStats.cppEi[year][0];

        qppDed = Math.max(qppDed, 0);
        qpipDed = Math.max(qpipDed, 0);
        eiDed = Math.max(eiDed, 0);

        return new BigDecimal[]{new BigDecimal(qppDed), new BigDecimal(qpipDed), new BigDecimal(eiDed)};
    }

    // Fed tax in Quebec uses a different tax credit
    private float getQCTaxCredit(TaxStatHolder qcHolder, BigDecimal anGross, int year){
        BigDecimal[] qppQpipEi = getQppQpip(anGross, year);

        float qppCont = qppQpipEi[0].floatValue();
        float qpipCont = qppQpipEi[1].floatValue();
        float eiCont = qppQpipEi[2].floatValue();

        float maxQppCont = qcHolder.qppMax[year];
        float maxQpipCont = qcHolder.qpipMax[year];
        float maxEiCont = qcHolder.cppEi[year][1];
        float claimAmount = fedStats.claimAmount[year];

        qppCont = Math.min(maxQppCont, qppCont);
        qpipCont = Math.min(maxQpipCont, qpipCont);
        eiCont = Math.min(eiCont, maxEiCont);

        return (claimAmount + qppCont + qpipCont + eiCont) * fedStats.rates[year][0];
    }

    private BigDecimal getFedTax(BigDecimal anGross, int year){
        float[] bracket = fedStats.brackets[year];
        float anGrossFloat = anGross.floatValue();
        int taxIndex = bracketGrossIndex(anGrossFloat, bracket);
        //Log.w("TaxManager", "taxIndex: " + taxIndex + " rate is: " + fedStats.rates[year][taxIndex]);

        return anGross.multiply(BigDecimal.valueOf(fedStats.rates[year][taxIndex])).
                subtract(getTaxCredit(fedStats, anGross, year)).
                subtract(BigDecimal.valueOf(fedStats.constK[year][taxIndex]));
    }

    private BigDecimal getFedTax(BigDecimal anGross, TaxStatHolder qcHolder, int year){
        float[] bracket = fedStats.brackets[year];
        float anGrossFloat = anGross.floatValue();
        int taxIndex = bracketGrossIndex(anGrossFloat, bracket);

        float basicTax = anGrossFloat * fedStats.rates[year][taxIndex];
        basicTax -= getQCTaxCredit(qcHolder, anGross, year);
        basicTax -= fedStats.constK[year][taxIndex];
        basicTax -= basicTax * 0.165;
        basicTax = basicTax > 0 ? basicTax : 0;

        return BigDecimal.valueOf(basicTax);
    }

	private  BigDecimal getBCTax(BigDecimal anGrossDec, int year){
		TaxStatHolder bcStats = getStatType(Province.BC);
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
        TaxStatHolder abStats = getStatType(Province.AB);

        return getStandardProvincialTax(anGross, year, abStats);
	}

    private BigDecimal getSKTax(BigDecimal anGrossDec, int year){
        TaxStatHolder skStats = getStatType(Province.SK);
        return getStandardProvincialTax(anGrossDec, year, skStats);
    }

    private BigDecimal getQCTax(BigDecimal anGrossDec, int year){
        TaxStatHolder qcStats = getStatType(Province.QC);

        float taxableAnnual = anGrossDec.floatValue() - qcStats.empDeduction[year];
        int taxIndex = bracketGrossIndex(taxableAnnual, qcStats.brackets[year]);

        float taxPayable = taxableAnnual * qcStats.rates[year][taxIndex];
        taxPayable -= qcStats.constK[year][taxIndex];
        taxPayable += getQCHealthPrem(taxableAnnual, qcStats, year);
        taxPayable -= qcStats.claimAmount[year] * qcStats.rates[year][0];

        return BigDecimal.valueOf(taxPayable);
    }

    private float getQCHealthPrem(float anGross, TaxStatHolder qcStats, int year){
        //HealthPrem discontinued in 2017.
        if (year >= 4) {
            return 0;
        }

        int healthIndex = TaxManager.bracketGrossIndex(anGross, qcStats.healthBracket[year]);
        if(healthIndex == 0) return 0;

        float addFlat = healthIndex == 1 ? 0 : qcStats.healthAmount[year][healthIndex - 1];

        float healthCalc = (anGross - qcStats.healthBracket[year][healthIndex]) *
                qcStats.healthRate[year][healthIndex] + addFlat;
        float healthFlat = qcStats.healthAmount[year][healthIndex];

        return healthCalc < healthFlat ? healthCalc : healthFlat;
    }

	private BigDecimal getONTax(BigDecimal anGrossDec, int year){
        TaxStatHolder onStats = getStatType(Province.ON);

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
        return getStandardProvincialTax(anGrossDec, year, getStatType(Province.NB));
    }

    private BigDecimal getMBTax(BigDecimal anGrossDec, int year){
        return getStandardProvincialTax(anGrossDec, year, getStatType(Province.MB));
    }

    private BigDecimal getNSTax(BigDecimal anGrossDec, int year, boolean cbFlag) {
        //So it doesn't have to jump between stat types
        TaxStatHolder provStats = cbFlag ? getStatType(Province.CB): getStatType(Province.NS);

        //get tax rate from bracket chart
        int brackIndex = bracketGrossIndex(anGrossDec.floatValue(), provStats.brackets[year]);

        BigDecimal taxDec = anGrossDec.multiply((BigDecimal.valueOf((provStats.rates[year][brackIndex]))));
        taxDec = taxDec.subtract((BigDecimal.valueOf(provStats.constK[year][brackIndex])));
        taxDec = taxDec.subtract(getNsTaxCredit(provStats, anGrossDec, year));
        return taxDec;
    }

    private BigDecimal getPEITax(BigDecimal anGrossDec, int year) {
        TaxStatHolder peStats = getStatType(Province.PE);
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
        TaxStatHolder nlStats = getStatType(Province.NL);
        BigDecimal provTax = getStandardProvincialTax(anGrossDec, year, nlStats);

        if (year > 4) {
            provTax = provTax.add(this.getNLLevy(anGrossDec, year, nlStats));
        }

        return provTax;
    }

    private BigDecimal getNLLevy(BigDecimal anGrossDec, int year, TaxStatHolder nlStats) {
        int yearIndex = year - 5;

        int brackIndex = bracketGrossIndex(anGrossDec.floatValue(), nlStats.levyBrackets[yearIndex]);
        float baseVal = nlStats.levyBase[yearIndex][brackIndex];
        float calcVal = (anGrossDec.floatValue() - nlStats.levyBrackets[yearIndex][brackIndex]) * nlStats.levyRate[yearIndex];
        if (brackIndex > 0) {
            calcVal += nlStats.levyBase[yearIndex][brackIndex - 1];
        }
        float levy = Math.min(baseVal, calcVal);

        return BigDecimal.valueOf(levy);
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


    private TaxStatHolder getStatType(Province prov){
        if(provStats == null){
            provStats = new TaxStatHolder(prov, assets);
        } else{
            if(provStats.prov != prov)
                provStats = new TaxStatHolder(prov, assets);
        }
        return provStats;
    }

    private TaxStatHolder getStatType(String province){
        return getStatType(Province.getProvFromName(province));
    }

    public static int getYearIndexFromName(String yearName){
        for(int i=0; i<yearStrings.length; i++){
            if(yearName.equals(yearStrings[i])) return i;
        }
        return yearStrings.length - 1;
    }
    
    public static boolean validatePrefs(SharedPreferences prefs){
        String[] activeProvinces = Province.getActiveProvinceNames();
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
        return result;
    }

    private BigDecimal getNsTaxCredit(TaxStatHolder stats, BigDecimal anGross, int year) {
        //taxCred == (cpp contribution + ei contribution + claimAmount) / lowest bracket

        //T4032 example doesn't account for cpp exemption but CRA calculator does.
        BigDecimal[] cppEiDec = getCppEi(anGross, year);

        //cap cpp component at max contribution
        cppEiDec[0] = (cppEiDec[0].compareTo(BigDecimal.valueOf(fedStats.cppEi[year][3])) > 0) ?
                BigDecimal.valueOf(fedStats.cppEi[year][3]) : cppEiDec[0];
        //cap ei component at max contribution
        cppEiDec[1] = (cppEiDec[1].compareTo(BigDecimal.valueOf(fedStats.cppEi[year][4])) > 0) ?
                BigDecimal.valueOf(fedStats.cppEi[year][4]) : cppEiDec[1];

        float claimAmount = stats.claimAmount[year];
        // In 2018, claim amount is based on gross.
        if (year > 4) {
            float flGross = anGross.floatValue();
            if (flGross > stats.claimNs[0] && flGross < stats.claimNs[1]) {
                claimAmount = stats.claimNs[2] - ((flGross - stats.claimNs[0]) * stats.claimNs[3]);
            }
        }

        return BigDecimal.valueOf(claimAmount)
                .add(cppEiDec[0])
                .add(cppEiDec[1])
                .multiply(BigDecimal.valueOf(stats.rates[year][0]));
    }
}
