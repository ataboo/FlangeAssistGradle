package com.atasoft.flangeassist.fragments.callout.daters.hire;


public interface HireOrder {
    public String getClassification();
    public Classification[] getClassTags();
    public boolean matchesTags(Classification[] tags);
}
