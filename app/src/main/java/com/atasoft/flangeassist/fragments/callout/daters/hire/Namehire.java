package com.atasoft.flangeassist.fragments.callout.daters.hire;


import org.json.JSONException;
import org.json.JSONObject;
import static com.atasoft.utilities.AtaStringUtils.capitalizeWord;


public class Namehire {
    public String firstName;
    public String lastName;
    public String classification;

    public Namehire(JSONObject rootObj) throws JSONException {
        classification = rootObj.getString("classification");
        lastName = capitalizeWord(rootObj.getString("last_name"));
        firstName = capitalizeWord(rootObj.getString("first_name"));
    }

    @Override
    public String toString() {
        return "Namehire{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", classification='" + classification + '\'' +
                '}';
    }
}
