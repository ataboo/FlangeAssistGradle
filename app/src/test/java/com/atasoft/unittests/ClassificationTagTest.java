package com.atasoft.unittests;

import com.atasoft.flangeassist.fragments.callout.daters.hire.ClassificationTag;
import com.atasoft.flangeassist.fragments.callout.daters.hire.HireOrder;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.Arrays;

@RunWith(JUnit4.class)
public class ClassificationTagTest {
    @Test
    public void matchesKeywords() {
        HireOrder order = Mockito.mock(HireOrder.class);
        Mockito.doReturn("J WELDER INOTECH").when(order).getClassification();
        boolean match = ClassificationTag.J_WELDER.matches(order);

        Mockito.doReturn("J WELDERINOTECH").when(order).getClassification();
        boolean noMatch = ClassificationTag.J_WELDER.matches(order);

        Assert.assertTrue(match);
        Assert.assertFalse(noMatch);
    }

    @Test
    public void matchesMultipleTags() {
        HireOrder order = Mockito.mock(HireOrder.class);
        Mockito.doReturn("JW BW").when(order).getClassification();

        ClassificationTag[] matchingTags = ClassificationTag.matchTags(order);

        Assert.assertEquals(2, matchingTags.length);
        Assert.assertTrue(Arrays.asList(matchingTags).contains(ClassificationTag.B_WELDER));
        Assert.assertTrue(Arrays.asList(matchingTags).contains(ClassificationTag.J_WELDER));
    }

}
