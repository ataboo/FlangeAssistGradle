package com.atasoft.flangeassist.fragments.callout.daters.hire;

import com.atasoft.flangeassist.fragments.callout.table.DetailRow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class Manpower implements HireOrder {
    private String classification;
    private int count;
    private ClassificationTag[] tags;

    public Manpower(JSONObject rootObj) throws JSONException {
        classification = rootObj.getString("title");
        count = Integer.parseInt(rootObj.getString("detail"));
        tags = ClassificationTag.matchTags(this);
    }

    @Override
    public String getClassification() {
        return classification;
    }

    @Override
    public ClassificationTag[] getMatchingTags() {
        return tags;
    }

    @Override
    public boolean hasTag(ClassificationTag tag) {
        return Arrays.asList(tags).contains(tag);
    }

    public int getCount() {
        return count;
    }

    @Override
    public DetailRow toDetailRow() {
        int iconRes = ClassificationTag.getIconRes(this);
        return new DetailRow(DetailRow.Type.HIRE, classification, Integer.toString(count), iconRes);
    }

    @Override
    public String toString() {
        return "Manpower{" +
                "classification='" + classification + '\'' +
                ", count=" + count +
                '}';
    }
}
