package com.atasoft.flangeassist.fragments.callout.daters.hire;

import com.atasoft.flangeassist.fragments.callout.table.DetailRow;

/**
 * Common interface for Manpowers and Namehires
 */
public interface HireOrder {
    public String getClassification();
    public ClassificationTag[] getMatchingTags();
    public boolean hasTag(ClassificationTag tag);
    public DetailRow toDetailRow();
}
