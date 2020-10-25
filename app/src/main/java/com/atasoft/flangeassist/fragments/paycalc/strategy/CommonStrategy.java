package com.atasoft.flangeassist.fragments.paycalc.strategy;

import com.atasoft.flangeassist.fragments.paycalc.TaxStatHolder;

import java.math.BigDecimal;

public class CommonStrategy {
    private static final int BD_PRECISION = 5;
    private static final int BD_ROUNDING = BigDecimal.ROUND_HALF_EVEN;
    private TaxStatHolder _fedStats;

    public CommonStrategy(TaxStatHolder fedStats) {
        _fedStats = fedStats;
    }

    public BigDecimal getStandardProvincialTax(BigDecimal anGrossDec, int year, TaxStatHolder provStats) {
        //get tax rate from bracket chart
        int bracketIndex = bracketGrossIndex(anGrossDec.floatValue(), provStats.brackets[year]);

        BigDecimal taxDec = anGrossDec.multiply((BigDecimal.valueOf((provStats.rates[year][bracketIndex]))));
        taxDec = taxDec.subtract((BigDecimal.valueOf(provStats.constK[year][bracketIndex])));
        taxDec = taxDec.subtract(getTaxCredit(provStats, anGrossDec, year));
        return taxDec;
    }

    public int bracketGrossIndex(float gross, float[] brackets){
        gross = gross < 0 ? 0f: gross;

        for(int i=brackets.length - 1; i>0;i--){
            if(gross > brackets[i]){
                return i;
            }
        }
        return 0;
    }

    public BigDecimal getTaxCredit(TaxStatHolder stats, BigDecimal anGross, int year) {
        //taxCred == (cpp contribution + ei contribution + claimAmount) / lowest bracket

        //T4032 example doesn't account for cpp exemption but CRA calculator does.
        BigDecimal[] cppEiDec = getCppEi(anGross, year);

        //cap cpp component at max contribution
        cppEiDec[0] = (cppEiDec[0].compareTo(BigDecimal.valueOf(_fedStats.cppEi[year][3])) > 0) ?
                BigDecimal.valueOf(_fedStats.cppEi[year][3]) : cppEiDec[0];
        //cap ei component at max contribution
        cppEiDec[1] = (cppEiDec[1].compareTo(BigDecimal.valueOf(_fedStats.cppEi[year][4])) > 0) ?
                BigDecimal.valueOf(_fedStats.cppEi[year][4]) : cppEiDec[1];

        return BigDecimal.valueOf(stats.claimAmount[year])
                .add(cppEiDec[0])
                .add(cppEiDec[1])
                .multiply(BigDecimal.valueOf(stats.rates[year][0]));
    }

    private BigDecimal[] getCppEi(BigDecimal anGross, int year){
        //[cpp rate, exemption, ei rate]
        float cppRate = _fedStats.cppEi[year][0];
        float cppExempt = _fedStats.cppEi[year][1];
        float eiRate = _fedStats.cppEi[year][2];

        BigDecimal cppRet = anGross.subtract(new BigDecimal(cppExempt)).
                multiply(new BigDecimal(cppRate)).setScale(BD_PRECISION, BD_ROUNDING);
        if(cppRet.compareTo(BigDecimal.ZERO) < 0) cppRet = BigDecimal.ZERO;

        BigDecimal eiRet = anGross.multiply(new BigDecimal(eiRate)).setScale(BD_PRECISION, BD_ROUNDING);
        if(eiRet.compareTo(BigDecimal.ZERO) < 0) eiRet = BigDecimal.ZERO;

        return new BigDecimal[]{cppRet, eiRet};
    }
}
