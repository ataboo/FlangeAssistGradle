package com.atasoft.flangeassist.fragments.paycalc.strategy;

import com.atasoft.flangeassist.fragments.paycalc.Province;
import com.atasoft.flangeassist.fragments.paycalc.WageRate;

import java.math.BigDecimal;

public interface ITaxStrategy {
    public Province GetProvince();

    public BigDecimal CalculateTax(BigDecimal annualGross, int year);

    public WageRate[] GetWages();

    public int GetDefaultWageIdx();
}
