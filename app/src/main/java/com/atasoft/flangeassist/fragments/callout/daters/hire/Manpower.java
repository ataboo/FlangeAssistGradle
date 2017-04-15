package com.atasoft.flangeassist.fragments.callout.daters.hire;

import org.json.JSONException;
import org.json.JSONObject;


public class Manpower {
    public String classification;
    public int count;

    public Manpower(JSONObject rootObj) throws JSONException {
        classification = rootObj.getString("title");
        count = Integer.parseInt(rootObj.getString("detail"));
    }

    @Override
    public String toString() {
        return "Manpower{" +
                "classification='" + classification + '\'' +
                ", count=" + count +
                '}';
    }
}
