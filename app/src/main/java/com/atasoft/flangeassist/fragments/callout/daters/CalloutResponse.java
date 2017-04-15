package com.atasoft.flangeassist.fragments.callout.daters;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalloutResponse {
    final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);

    public CalloutJob[] jobs;
    public Date lastPull;

    CalloutResponse(String response) {
        ArrayList<CalloutJob> newJobs = new ArrayList<>();

        try {
            JSONObject rootObj = new JSONObject(response);
            JSONArray jobsArray = rootObj.getJSONArray("data");

            lastPull = DATE_FORMAT.parse(rootObj.getString("pull_time"));

            for(int i=0; i<jobsArray.length(); i++) {
                newJobs.add(new CalloutJob(jobsArray.getJSONObject(i)));
            }

            jobs = newJobs.toArray(new CalloutJob[newJobs.size()]);
        } catch (JSONException|ParseException e) {
            Log.e("FlangeAssist", "Malformed callout response.");
            e.printStackTrace();
            jobs = new CalloutJob[0];
            lastPull = new Date(0);
        }
    }

    @Override
    public String toString() {
        return "CalloutResponse{" +
                "jobs=" + Arrays.toString(jobs) +
                ", lastPull=" + lastPull +
                '}';
    }
}
