package com.atasoft.flangeassist.fragments.cashcounter;

import android.text.format.Time;
import android.util.Log;

import com.atasoft.helpers.AtaMathUtils;
import com.atasoft.flangeassist.fragments.cashcounter.CashCounter.*;

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

    private static int[] makeValsFromDouble(double earnings){
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
            shiftDuration[0]++; //adds and hour when minutes overflow
            shiftEnd[1] = shiftEnd[1] % 60;
        }
        shiftEnd[0] = shiftStart[0] + shiftDuration[0];
        shiftEnd[0] = shiftEnd[0] % 24;

        return shiftEnd;
    }

    private void updateValues(){
        float shiftLengthFloat = AtaMathUtils.bracketFloat(weekdayHours[0] + weekdayHours[1] + weekdayHours[2], 0, 24);
        shiftDuration[0] = (int) shiftLengthFloat;
        shiftDuration[1] = (int) (shiftLengthFloat - (float) shiftDuration[0]) * 60;
        this.shiftStartVal = startAtaPicker.getVals();
        int[] shiftEnd = getShiftEnd(shiftStartVal, shiftDuration);

    }

    private double getEarnings(Time timeNow, EarningAttributes earningAttributes){
        //used float for comparisons but keep into for calcs incase of rounding shenanigans
        int[] timeNowArr = {timeNow.hour, timeNow.minute, timeNow.second};

        float floatNow = getFloatTime(timeNowArr);
        float floatStart = getFloatTime(earningAttributes.shiftStart);
        int secondsIntoShift = timeNowArr[2];
        secondsIntoShift += (timeNowArr[1] - earningAttributes.shiftStart[1]) * 60;
        //already checked that it's mid shift
        if(floatNow > floatStart) {
            secondsIntoShift += (timeNowArr[0] - earningAttributes.shiftStart[0]) * 3600;
        } else {  //start-->now range stradles midnight
            secondsIntoShift += (timeNowArr[0] - earningAttributes.shiftStart[0] + 24) * 3600;
        }
        double hoursIntoShift = secondsIntoShift / 3600d;

        boolean beforeMidnight = floatNow > floatStart;

        boolean isWeekend =
                // if shift doesn't stradle midnight and its saturday or sunday now...
                (beforeMidnight && (timeNow.weekDay == Time.SATURDAY || timeNow.weekDay == Time.SUNDAY)) ||
                        // if nightshift and after midnight sunday is sat shift and monday is sun shift
                        (!beforeMidnight && (timeNow.weekDay == Time.SUNDAY || timeNow.weekDay == Time.MONDAY));

        boolean isFriday = (beforeMidnight && timeNow.weekDay == Time.FRIDAY) ||
                (!beforeMidnight && timeNow.weekDay == Time.SATURDAY);

        double[] hours = new double[3];  //single, ot, double
        if(earningAttributes.isFourTens){
            hours[2] = AtaMathUtils.bracketDouble(hoursIntoShift - 10, 0, 24);
            if(isFriday){
                hours[1] = AtaMathUtils.bracketDouble(hoursIntoShift, 0, 10);
                hours[0] = 0;
            } else {
                hours[0] = AtaMathUtils.bracketDouble(hoursIntoShift, 0, 10);
                hours[1] = 0;
            }
        } else {
            hours[0] = AtaMathUtils.bracketDouble(hoursIntoShift, 0, earningAttributes.weekdayHours[0]);
            hours[2] = AtaMathUtils.bracketDouble(hoursIntoShift - earningAttributes.weekdayHours[0] - earningAttributes.weekdayHours[1], 0, 24);
            hours[1] = AtaMathUtils.bracketDouble(hoursIntoShift - hours[0] - hours[2], 0, 24);
        }
        if((isWeekend && earningAttributes.weekendDouble) || earningAttributes.isHoliday){
            hours[0] = 0;
            hours[1] = 0;
            hours[2] = AtaMathUtils.bracketDouble(hoursIntoShift, 0, 24);
        }
        if(hours[2] > 0){
            if(earningAttributes.isHoliday){
                otIndicate(EarningType.HOLIDAY_TIME);
            } else {
                if(isWeekend){
                    otIndicate(EarningType.WEEKEND_DOUBLE);
                } else {
                    otIndicate(EarningType.DOUBLE_TIME);
                }
            }
        } else {
            if(hours[1] > 0){
                otIndicate(EarningType.OVER_TIME);
            } else {
                otIndicate(EarningType.STRAIGHT_TIME);
            }
        }
        double hoursEquivelant = 1d * hours[0] + 1.5d * hours[1] + 2d * hours[2];
        //Log.w("CashCounter",String.format("weekdayhours[0]:%.3f, weekdayhours[1]:%.3f, weekdayhours[2]:%.3f", weekdayHours[0], weekdayHours[1], weekdayHours[2]));

        //Log.w("CashCounter",String.format("hours[0]:%.3f, hours[1]:%.3f, hours[2]:%.3f, intoShift: %.3f", hours[0], hours[1], hours[2], hoursIntoShift));
        double earnings = hoursEquivelant * earningAttributes.wageRate;
        if(earningAttributes.isNights) earnings += hoursIntoShift * 3d;
        earnings = Math.floor(earnings * 100) / 100;
        return earnings;
    }

    private static float getFloatTime(int[] intTime){
        if(intTime.length < 2) return 0f;

        float retFloat = intTime[0];
        retFloat += ((float) intTime[1]) / 60;
        if(intTime.length == 3) retFloat += ((float) intTime[2]) / 3600;
        return retFloat;
    }

    private static boolean isInTimeRange(int[] rangeStart, int[] rangeEnd, int[] timeCheck){
        float floatStart = getFloatTime(rangeStart);
        float floatEnd = getFloatTime(rangeEnd);
        float floatCheck = getFloatTime(timeCheck);
        if(floatStart <= floatEnd){
            return !(floatCheck < floatStart || floatCheck > floatEnd);
        } else { //range stradles midnight
            return floatCheck > floatStart || floatCheck < floatEnd;
        }
    }
}
