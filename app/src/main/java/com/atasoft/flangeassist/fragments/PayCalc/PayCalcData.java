package com.atasoft.flangeassist.fragments.paycalc;

import com.atasoft.utilities.AtaMathUtils;

/**
 * Created by ataboo on 2015-12-12.
 * Holds earnings and deductions for PayChequeFragment and does some of the calculation
 * Call setMealLOABonuses, setTravelBonuses, setNightPremium, addHours before getting earnings and deductions.
 */
public class PayCalcData {
    public class DeductionHolder {
        public float cpp;
        public float ei;
        public float provTax;
        public float fedTax;
        public float fieldDuesRate;
        public float monthlyDues;
        public float addTax;

        public DeductionHolder(){
        }

        public float getFieldDues(float duesTaxable){
            return fieldDuesRate * duesTaxable;
        }

        public float getUnionDues(float duesTaxable){
            return getFieldDues(duesTaxable) + monthlyDues;
        }

        public float getTaxes() {
            return provTax + fedTax;
        }

        public float getDeductionsSum(float duesTaxable){
            return cpp + ei + getTaxes() + getUnionDues(duesTaxable) + addTax;
        }
    }

    public class EarningHolder {
        public float wageEarnings;
        public float loaBonus = 0;
        public boolean loaBonusTaxable = false;
        public float mealBonus = 0;
        public boolean mealBonusTaxable = false;
        public float dailyTravelBonus = 0;
        public boolean dailyTravelTaxable = false;
        public float weeklyTravelBonus = 0;
        public boolean weeklyTravelTaxable = false;
        public float nightShiftPremium = 0;
        public float nightShiftBonus = 0;
        public float vacationBonus = 0;

        public EarningHolder(){
        }

        public float getExempt(){
            float loa = loaBonusTaxable ? 0: loaBonus;
            float meal = mealBonusTaxable ? 0: mealBonus;
            float dailyTravel = dailyTravelTaxable ? 0: dailyTravelBonus;
            float monthlyTravel = weeklyTravelTaxable ? 0: weeklyTravelBonus;
            return loa + meal + dailyTravel + monthlyTravel;
        }

        public float getTaxable(){
            return wageEarnings + nightShiftBonus + vacationBonus + mealBonus + dailyTravelBonus +
                    weeklyTravelBonus + loaBonus - getExempt();
        }

        public float getDuesApplicableEarnings(){
            return wageEarnings + nightShiftBonus;
        }

        public float getGross(){
            return loaBonus + mealBonus + dailyTravelBonus + weeklyTravelBonus + nightShiftBonus + wageEarnings + vacationBonus;
        }
    }

    private DeductionHolder deductions = new DeductionHolder();
    private EarningHolder earnings = new EarningHolder();

    private float[] hourSum = {0,0,0};
    private float dailyTravelRate = 0;
    private String provString;
    private String yearString;
    private TaxManager taxManager;

    public PayCalcData(TaxManager taxManager, String provString, String yearString){
        this.taxManager = taxManager;
        this.provString = provString;
        this.yearString = yearString;
    }

    public void setMealLOABonuses(float mealBonus, boolean mealTaxable, float loaBonus, boolean loaTaxable){
        earnings.mealBonus = mealBonus;
        earnings.mealBonusTaxable = mealTaxable;
        earnings.loaBonus = loaBonus;
        earnings.loaBonusTaxable = loaTaxable;
    }

    public void setTravelBonuses(float dailyTravelRate, boolean dailyTravelTaxable, float weeklyTravelBonus, boolean weeklyTravelTaxable){
        this.dailyTravelRate = dailyTravelRate;
        earnings.dailyTravelTaxable = dailyTravelTaxable;
        earnings.weeklyTravelBonus = weeklyTravelBonus;
        earnings.weeklyTravelTaxable = weeklyTravelTaxable;
    }

    public void setNightPremium(float nightPremium){
        earnings.nightShiftPremium = nightPremium;
    }

    public void setDues(float fieldDuesRate, float monthlyDues){
        deductions.fieldDuesRate = fieldDuesRate;
        deductions.monthlyDues = monthlyDues;
    }

    /// Shift already split into [single, ot, double] array
    public void addHours(float[] dayHours, boolean isFourTens, boolean isNightShift, int dayIndex, boolean nightShiftOT, boolean otDouble){
        float[] splitHours = splitShiftHours(dayHours, isFourTens, dayIndex, nightShiftOT && isNightShift, otDouble);
        float hoursWorked = splitHours[0] + splitHours[1] + splitHours[2];
        if(isNightShift && !nightShiftOT){
            earnings.nightShiftBonus += earnings.nightShiftPremium * hoursWorked;
        }
        if(hoursWorked > 0){
            earnings.dailyTravelBonus += dailyTravelRate;
        }

        hourSum = new float[]{hourSum[0] + splitHours[0], hourSum[1] + splitHours[1], hourSum[2] + splitHours[2]};
    }

    public EarningHolder getEarnings(float wageRate, float vacRate){
        earnings.wageEarnings = wageRate * hourSum[0] + wageRate * hourSum[1] * 1.5f + wageRate * hourSum[2] * 2.0f;
        earnings.vacationBonus = (earnings.wageEarnings + earnings.nightShiftBonus) * vacRate;

        return earnings;
    }

    public DeductionHolder getDeductions(float addTax){
        deductions.addTax = addTax;
        float[] taxReturn = taxManager.getTaxes(earnings.getTaxable(), deductions.getUnionDues(earnings.getDuesApplicableEarnings()), yearString, provString);
        deductions.fedTax = taxReturn[0];
        deductions.provTax = taxReturn[1];
        deductions.cpp = taxReturn[2];
        deductions.ei = taxReturn[3];
        return deductions;
    }

    public float[] getHoursSum(){
        return hourSum;
    }

    // Pass sunday or saturday dayIndex for holiday.
    /// Split hours for a specific shift length into [single, ot, double].
    float[] splitShiftHours(float shiftHours[], boolean isFourTens, int dayIndex, boolean nightOTActive, boolean otDouble){
        // Shift is already split and overwrites other settings.
        if(shiftHours.length == 3){
            return shiftHours;
        }
        if(shiftHours.length !=1){
            throw new Error("PayCalcData didn't expect shift hours of length: " + shiftHours.length);
        }

        // Weekend or holiday all double time.
        // Passed weekend dayIndex for holiday.
        if(dayIndex == 0 || dayIndex == 6){
            return new float[]{0,0,shiftHours[0]};
        }

        float[] splitHours;

        if (isFourTens){
            splitHours = splitShiftFourTens(shiftHours[0], dayIndex);
        } else {
            splitHours = splitShiftFiveEights(shiftHours[0]);
        }

        // All Overtime is Double Time
        if(otDouble){
            splitHours = makeOTDouble(splitHours);
        }

        // All straight time is Overtime
        if(nightOTActive){
            splitHours = makeStraightOT(splitHours);
        }

        return splitHours;
    }

    float[] splitShiftFourTens(float shiftHours, int dayIndex){
        // Monday to Thursday: 10 hours straight double after that.
        float straight = AtaMathUtils.clampFloat(shiftHours, 0, 10);
        float overTime = 0;
        float doubleTime = AtaMathUtils.clampFloat(shiftHours - 10, 0, Float.MAX_VALUE);

        // Friday: overtime first 10 double after that.
        if(dayIndex == 5) {
            overTime = straight;
            straight = 0;
        }

        return new float[]{straight, overTime, doubleTime};
    }

    float[] splitShiftFiveEights(float shiftHours){
        float straight = AtaMathUtils.clampFloat(shiftHours, 0, 8);
        float overTime = AtaMathUtils.clampFloat(shiftHours - 8, 0, 2);
        float doubleTime = AtaMathUtils.clampFloat(shiftHours - 10, 0, Float.MAX_VALUE);

        return new float[]{straight, overTime, doubleTime};
    }

    float[] makeStraightOT(float[] hours){
        return new float[]{0, hours[0], hours[1] + hours[2]};
    }


    float[] makeOTDouble(float[] hours){
        return new float[]{hours[0], 0, hours[1] + hours[2]};
    }
}
