package com.atasoft.tests;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.atasoft.flangeassist.MainActivity;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.IntVector;
import com.atasoft.flangeassist.fragments.paycalc.TaxManager;
import com.atasoft.helpers.AtaMathUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by ataboo on 2016-05-12.
 */
public class UnitTests extends InstrumentationTestCase {
    /*
    public void testTest() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
    */

    public static final String TEST_2016 = "ToolboxGrid - test_2016.csv";
    public static final float valFailThreshold = .01f;

    public void testTax() throws Exception {

        TaxManager.Prov[] testProvs = {TaxManager.Prov.FED, TaxManager.Prov.QC, TaxManager.Prov.BC, TaxManager.Prov.AB,
                TaxManager.Prov.SK, TaxManager.Prov.MB, TaxManager.Prov.ON, TaxManager.Prov.QC, TaxManager.Prov.NB,
                TaxManager.Prov.NS, TaxManager.Prov.PE, TaxManager.Prov.NL};

        BufferedReader br;
        String line;
        ArrayList<float[]> testLines = new ArrayList<float[]>();

        String yearName = "";
        String[] legendRow;

        try {
            InputStream inStr =  getInstrumentation().getTargetContext().getResources().getAssets().open(TEST_2016);
            br = new BufferedReader(new InputStreamReader(inStr));

            line = br.readLine();
            assertNotNull(line);
            legendRow = line.split(",");



            yearName = legendRow[0];

            while ((line = br.readLine()) != null) {
                testLines.add(parseLine(line));
            }
            br.close();
        } catch (IOException e) {
            Log.e("TaxManCSV", "IOException for fileName: " + TEST_2016);
            e.printStackTrace();
        }

        TaxManager taxMan = new TaxManager(TaxManager.Prov.FED.getName(),
                getInstrumentation().getTargetContext().getResources().getAssets());

        int provIndex = 0;
        for(TaxManager.Prov prov: testProvs){

            for(float[] testLine: testLines){
                float incomeVal = testLine[0];
                float[] calcTax = taxMan.getTaxes(incomeVal, 0f, TaxManager.getYearIndexFromName(yearName), prov);
                float testTax = provIndex == 0 || provIndex == 1 ? calcTax[0]: calcTax[1];

                float tableTax = testLine[provIndex + 1];

                float difference = Math.abs(testTax - tableTax);

                Log.v("Tests", String.format("prov %s in %s is out by %.2f on test val of %.0f",
                        prov.getSurname(), yearName, difference, incomeVal));

                assertTrue(difference < valFailThreshold);
            }
            provIndex++;
        }
    }

    private float[] parseLine(String line){
        String[] lineSplit = line.split(",");
        float[] floats = new float[lineSplit.length];

        try {
            int i=0;
            for (String valString : lineSplit) {
                floats[i++] = (Float.parseFloat(valString));
            }
        } catch (NumberFormatException e){
            e.printStackTrace();
        }

        return floats;

    }

    public void testLerps(){
        IntVector first = new IntVector(5, 5);
        IntVector second = new IntVector(-5, -5);

        IntVector result = first.lerpTowards(second, 0.5f);

        assertTrue(result.equals(new IntVector(0,0)));

        first = new IntVector(50,50);
        second = new IntVector(50, -50);

        result = first.lerpTowards(second, 0.7f);

        assertTrue(result.equals(new IntVector(50, -20)));

        first = new IntVector(-50,0);
        second = new IntVector(50, -100);

        result = first.lerpTowards(second, 0.25f);

        assertTrue(result.equals(new IntVector(-25, -25)));

        assertTrue(AtaMathUtils.lerpFloat(-100f, 100f, 1f) - 100f < 0.001);

        assertTrue(AtaMathUtils.lerpFloat(-100f, 100f, 0.25f) - -50f < 0.001);
    }


    /*
    public void testBracketCount() {
        //assertTrue(false);
    }
    */


}
