package com.atasoft.robotests;

import com.atasoft.flangeassist.fragments.paycalc.PayCalcData;
import com.atasoft.flangeassist.fragments.paycalc.TaxManager;
import junit.framework.Assert;
import org.junit.Test;


public class PaycalcTest extends RoboTestCase {
    @Test
    public void splitsHoursForAlbertaProperly() {
        TaxManager taxManager = new TaxManager(TaxManager.Prov.AB.getName(), activity.getAssets());
        PayCalcData payCalcData = new PayCalcData(taxManager, TaxManager.Prov.AB.getName(), "2017");
        payCalcData.addHours(new float[]{13f}, true, false, 1, false, taxManager.getProvStats(TaxManager.Prov.AB.getName()));
        payCalcData.addHours(new float[]{13f}, true, false, 5, false, taxManager.getProvStats(TaxManager.Prov.AB.getName()));
        payCalcData.addHours(new float[]{13f}, true, false, 6, false, taxManager.getProvStats(TaxManager.Prov.AB.getName()));

        float[] expected = new float[] {10, 26, 3};
        float[] hoursSum = payCalcData.getHoursSum();
        for (int i=0; i<2; i++) {
            Assert.assertEquals(expected[i], hoursSum[i]);
        }

        payCalcData = new PayCalcData(taxManager, TaxManager.Prov.AB.getName(), "2017");
        payCalcData.addHours(new float[]{13f}, false, false, 1, false, taxManager.getProvStats(TaxManager.Prov.AB.getName()));
        payCalcData.addHours(new float[]{13f}, false, false, 5, false, taxManager.getProvStats(TaxManager.Prov.AB.getName()));
        payCalcData.addHours(new float[]{13f}, false, false, 6, false, taxManager.getProvStats(TaxManager.Prov.AB.getName()));

        expected = new float[] {16f, 20, 3};
        hoursSum = payCalcData.getHoursSum();
        for (int i=0; i<2; i++) {
            Assert.assertEquals(expected[i], hoursSum[i]);
        }
    }
}
