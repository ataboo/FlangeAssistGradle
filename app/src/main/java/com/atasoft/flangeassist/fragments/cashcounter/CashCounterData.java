package com.atasoft.flangeassist.fragments.cashcounter;

import android.text.format.Time;
import android.util.Log;

import com.atasoft.utilities.AtaMathUtils;
import com.atasoft.flangeassist.fragments.cashcounter.CashCounter2.*;

/**
 * Created by ataboo on 2016-03-29.
 */

public class CashCounterData {
    public class EarningAttributes{
        public final int[] shiftStart;
        public final float[] weekdayHours;
        public final float wageRate;
        public final boolean isHoliday;
        public final boolean weekendDouble;
        public final boolean isNights;
        public final boolean isFourTens;

        public EarningAttributes(int[] shiftStart, float[] weekdayHours, float wageRate, boolean isHoliday,
                                 boolean isFourTens, boolean weekendDouble, boolean isNights) {
            this.shiftStart = shiftStart;
            this.weekdayHours = weekdayHours;
            this.wageRate = wageRate;
            this.isHoliday = isHoliday;
            this.isFourTens = isFourTens;
            this.weekendDouble = weekendDouble;
            this.isNights = isNights;
        }
    }

    public class EarningsReturn{
        public final double earnings;
        public final EarningType earningType;

        public EarningsReturn(double earnings, EarningType earningType){
            this.earnings = earnings;
            this.earningType = earningType;
        }
    }

    public static int[] makeValsFromDouble(double earnings){
        String valString = String.format("%.2f", earnings);
        int earnLength = valString.length();
        int[] retVals = new int[]{0,0,0,0,0,0};
        if (earnLength < 4 || earnLength > 7){
            Log.e("CashCounter", "Earning String out of Range.");
            return retVals;
        }
        retVals[5] = Character.getNumericValue(valString.charAt(earnLength - 1)); //hundreds
        retVals[4] = Character.getNumericValue(valString.charAt(earnLength - 2)); //tenths
        retVals[3] = Character.getNumericValue(valString.charAt(earnLength - 4)); //ones decimal is @ 3
        retVals[2] = (earnLength >= 5) ? Character.getNumericValue(valString.charAt(earnLength - 5)) : 0;
        retVals[1] = (earnLength >= 6) ? Character.getNumericValue(valString.charAt(earnLength - 6)) : 0;
        retVals[0] = (earnLength >= 7) ? Character.getNumericValue(valString.charAt(earnLength - 7)) : 0;

        //Log.w("CashCounter_makeCountString", String.format("retString: %s, length: %s", valString, valString.length()));
        //return retString;
        return retVals;
    }

    private int[] getShiftEnd(int[] shiftStart, int[] shiftDuration){
        int[] shiftEnd = {0,0};  //hr, min

        shiftEnd[1] = shiftStart[1] + shiftDuration[1];
        if(shiftEnd[1] >= 60){
            shiftDuration[0]++; //adds an hour when minutes overflow
            shiftEnd[1] = shiftEnd[1] % 60;
        }
        shiftEnd[0] = shiftStart[0] + shiftDuration[0];
        shiftEnd[0] = shiftEnd[0] % 24;

        return shiftEnd;
    }

    public EarningsReturn getEarnings(Time timeNow, EarningAttributes earningAttributes){
        //used float for comparisons but keep into for calcs incase of rounding shenanigans
        int[] timeNowArr = {timeNow.hour, timeNow.minute, timeNow.second};

        EarningType earningType = EarningType.OFF_SHIFT;

        float floatNow = getFloatTime(timeNowArr);
        float floatStart = getFloatTime(earningAttributes.shiftStart);
        float shiftDuration = earningAttributes.weekdayHours[0] + earningAttributes.weekdayHours[1] + earningAttributes.weekdayHours[2];
        float floatEnd = floatStart + shiftDuration;
        if (floatEnd > 24) {
            floatEnd -= 24f;
        }

        if(!isInTimeRange(floatStart, floatEnd, floatNow)){
            return new EarningsReturn(0d, EarningType.OFF_SHIFT);
        }

        boolean beforeMidnight = floatNow > floatStart;
        float hoursIntoShift = beforeMidnight ? floatNow - floatStart: floatNow - floatStart + 24f;

        boolean isWeekend =
                // if shift doesn't stradle midnight and its saturday or sunday now...
                (beforeMidnight && (timeNow.weekDay == Time.SATURDAY || timeNow.weekDay == Time.SUNDAY)) ||
                        // if nightshift and after midnight sunday is sat shift and monday is sun shift
                        (!beforeMidnight && (timeNow.weekDay == Time.SUNDAY || timeNow.weekDay == Time.MONDAY));

        boolean isFriday = (beforeMidnight && timeNow.weekDay == Time.FRIDAY) ||
                (!beforeMidnight && timeNow.weekDay == Time.SATURDAY);

        double[] hours = new double[3];  //single, ot, double
        if(earningAttributes.isFourTens){
            hours[2] = AtaMathUtils.clampDouble(hoursIntoShift - 10, 0, 24);
            if(isFriday){
                hours[1] = AtaMathUtils.clampDouble(hoursIntoShift, 0, 10);
                hours[0] = 0;
            } else {
                hours[0] = AtaMathUtils.clampDouble(hoursIntoShift, 0, 10);
                hours[1] = 0;
            }
        } else {
            hours[0] = AtaMathUtils.clampDouble(hoursIntoShift, 0, earningAttributes.weekdayHours[0]);
            hours[2] = AtaMathUtils.clampDouble(hoursIntoShift - earningAttributes.weekdayHours[0] - earningAttributes.weekdayHours[1], 0, 24);
            hours[1] = AtaMathUtils.clampDouble(hoursIntoShift - hours[0] - hours[2], 0, 24);
        }
        if((isWeekend && earningAttributes.weekendDouble) || earningAttributes.isHoliday){
            hours[0] = 0;
            hours[1] = 0;
            hours[2] = AtaMathUtils.clampDouble(hoursIntoShift, 0, 24);
        }
        if(hours[2] > 0){
            if(earningAttributes.isHoliday){
                earningType = EarningType.HOLIDAY_TIME;
            } else {
                if(isWeekend){
                    earningType = EarningType.WEEKEND_DOUBLE;
                } else {
                    earningType = EarningType.DOUBLE_TIME;
                }
            }
        } else {
            if(hours[1] > 0){
                earningType = EarningType.OVER_TIME;
            } else {
                earningType = EarningType.STRAIGHT_TIME;
            }
        }
        double hoursEquivelant = 1d * hours[0] + 1.5d * hours[1] + 2d * hours[2];
        //Log.w("CashCounter",String.format("weekdayhours[0]:%.3f, weekdayhours[1]:%.3f, weekdayhours[2]:%.3f", weekdayHours[0], weekdayHours[1], weekdayHours[2]));

        //Log.w("CashCounter",String.format("hours[0]:%.3f, hours[1]:%.3f, hours[2]:%.3f, intoShift: %.3f", hours[0], hours[1], hours[2], hoursIntoShift));
        double earnings = hoursEquivelant * earningAttributes.wageRate;
        if(earningAttributes.isNights) earnings += hoursIntoShift * 3d;
        earnings = Math.floor(earnings * 100) / 100;
        return new EarningsReturn(earnings, earningType);
    }

    private static float getFloatTime(int[] intTime){
        if(intTime.length < 2) return 0f;

        float retFloat = intTime[0];
        retFloat += ((float) intTime[1]) / 60;
        if(intTime.length == 3) retFloat += ((float) intTime[2]) / 3600;
        return retFloat;
    }

    private static boolean isInTimeRange(float rangeStart, float rangeEnd, float checkTime) {
        if(rangeStart <= rangeEnd){
            return !(checkTime < rangeStart || checkTime > rangeEnd);
        } else { //range stradles midnight
            return checkTime > rangeStart || checkTime < rangeEnd;
        }
    }
}
