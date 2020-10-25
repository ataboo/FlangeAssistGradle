package com.atasoft.flangeassist.fragments.paycalc.strategy;

import com.atasoft.flangeassist.fragments.paycalc.Province;
import com.atasoft.flangeassist.fragments.paycalc.TaxStatHolder;
import com.atasoft.flangeassist.fragments.paycalc.WageRate;

import java.math.BigDecimal;

public class AlbertaTaxStrategy implements ITaxStrategy {
    private final TaxStatHolder _taxStats;
    private final CommonStrategy _commonStrategy;

    public AlbertaTaxStrategy(TaxStatHolder abStats, CommonStrategy commonStrategy) {
        _taxStats = abStats;
        _commonStrategy = commonStrategy;
    }

    @Override
    public Province GetProvince() {
        return Province.AB;
    }

    @Override
    public BigDecimal CalculateTax(BigDecimal annualGross, int year) {
        return _commonStrategy.getStandardProvincialTax(annualGross, year, _taxStats);
    }

    @Override
    public WageRate[] GetWages() {
        return _taxStats.wageRates;
    }

    @Override
    public int GetDefaultWageIdx() {
        return _taxStats.defaultWageIndex;
    }
}
