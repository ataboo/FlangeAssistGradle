package com.atasoft.flangeassist.fragments.callout.daters.hire;


import com.atasoft.flangeassist.fragments.callout.table.DetailRow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.atasoft.utilities.AtaStringUtils.capitalizeWord;


public class Namehire implements HireOrder {
    private String firstName;
    private String lastName;
    private String classification;
    private ClassificationTag[] tags;

    public Namehire(JSONObject rootObj) throws JSONException {
        classification = rootObj.getString("classification");
        lastName = capitalizeWord(rootObj.getString("last_name"));
        firstName = capitalizeWord(rootObj.getString("first_name"));
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

    @Override
    public DetailRow toDetailRow() {
        int iconRes = ClassificationTag.getIconRes(this);
        return new DetailRow(DetailRow.Type.HIRE, String.format("%s %s", firstName, lastName), classification, iconRes);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Namehire{");
        sb.append("firstName='").append(firstName).append('\'');
        sb.append(", \nlastName='").append(lastName).append('\'');
        sb.append(", \nclassification='").append(classification).append('\'');
        sb.append(", \ntags=").append(Arrays.toString(tags));
        sb.append('}');
        return sb.toString();
    }
}
