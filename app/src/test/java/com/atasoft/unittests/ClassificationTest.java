package com.atasoft.unittests;

import com.atasoft.flangeassist.fragments.callout.daters.hire.Classification;
import com.atasoft.flangeassist.fragments.callout.daters.hire.HireOrder;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ClassificationTest extends TestCase {

    @Test
    public void matchesKeywords() {
        Classification welderClass = Classification.B_WELDER;

        HireOrder order = new HireOrder() {
            @Override
            public String getClassification() {
                return "J WELDER BW";
            }

            @Override
            public Classification[] getClassTags() {
                return new Classification[0];
            }

            @Override
            public boolean matchesTags(Classification[] tags) {
                return false;
            }
        };

        Assert.assertTrue(welderClass.matches(order));
    }

}
