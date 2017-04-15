package com.atasoft.flangeassist.fragments.callout.daters;

import com.atasoft.flangeassist.fragments.callout.daters.hire.Manpower;
import com.atasoft.flangeassist.fragments.callout.daters.hire.Namehire;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

interface JSONObjectCallback {
    Object handle(JSONObject jsonObject) throws JSONException;
}

public class CalloutJob {
    String id;
    String jobName;
    String contractor;
    String openTo;
    String dayshift;
    String workType;
    String hours;
    String duration;
    String accomodation;
    String drugTesting;
    String comments;
    Date dateTime;
    Manpower[] manpowers;
    Namehire[] namehires;

    public CalloutJob(JSONObject jobObject) throws JSONException, ParseException {
        id = jobObject.getString("id");
        jobName = jobObject.getString("job_name");
        contractor = jobObject.getString("contractor");
        openTo = jobObject.getString("open_to");
        dayshift = jobObject.getString("dayshift");
        workType = jobObject.getString("work_type");
        hours = jobObject.getString("hours");
        duration = jobObject.getString("duration");
        accomodation = jobObject.getString("accommodation");
        drugTesting = jobObject.getString("drug_testing");
        comments = jobObject.getString("comments");
        dateTime = CalloutResponse.DATE_FORMAT.parse(jobObject.getString("date_time"));

        manpowers = jsonArrayMap(jobObject.getJSONArray("manpowers"), Manpower.class);
        namehires = jsonArrayMap(jobObject.getJSONArray("namehires"), Namehire.class);
    }

    /**
     * Convert the JSONArray into an array of objects of class T
     *
     * @param jArray JsonArray containing JSONObjects that will be used passed to T's constructor.
     * @param type Type of Object to instantiate form the JSONObjects
     * @param <T> Object Class
     * @return Array of instantiated objects.
     * @throws JSONException self explanatory.
     */
    public static <T> T[] jsonArrayMap(JSONArray jArray, Class<T> type) throws JSONException {
        ArrayList<T> outList = new ArrayList<>();

        for(int i=0; i<jArray.length(); i++) {
            try {
                outList.add(type.getConstructor(JSONObject.class).newInstance(jArray.get(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        T[] outArray = (T[]) Array.newInstance(type, outList.size());
        outList.toArray(outArray);
        return outArray;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CalloutJob{");
        sb.append("id='").append(id).append('\'');
        sb.append(", \njobName='").append(jobName).append('\'');
        sb.append(", \ncontractor='").append(contractor).append('\'');
        sb.append(", \nopenTo='").append(openTo).append('\'');
        sb.append(", \ndayshift='").append(dayshift).append('\'');
        sb.append(", \nworkType='").append(workType).append('\'');
        sb.append(", \nhours='").append(hours).append('\'');
        sb.append(", \nduration='").append(duration).append('\'');
        sb.append(", \naccomodation='").append(accomodation).append('\'');
        sb.append(", \ndrugTesting='").append(drugTesting).append('\'');
        sb.append(", \ncomments='").append(comments).append('\'');
        sb.append(", \ndateTime=").append(dateTime);
        sb.append(", \nmanpowers=").append(Arrays.toString(manpowers));
        sb.append(", \nnamehires=").append(Arrays.toString(namehires));
        sb.append('}');
        return sb.toString();
    }
}
