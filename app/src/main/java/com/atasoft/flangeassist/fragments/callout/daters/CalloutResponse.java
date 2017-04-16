package com.atasoft.flangeassist.fragments.callout.daters;


import android.util.Log;
import android.util.SparseArray;

import com.atasoft.flangeassist.fragments.callout.daters.hire.ClassificationFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;

public class CalloutResponse {
    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);

    public CalloutJob[] jobs;
    public Date lastPull;

    public CalloutResponse(String response) {
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

    public void fillSparse(SparseArray<CalloutJob> sparseArray, EnumSet<ClassificationFilter> filters) {
        sparseArray.clear();

        int sparseCount = 0;
        for (int i=0; i<jobs.length; i++) {
            CalloutJob job = jobs[i];

            if (job.matchesFilters(filters)) {
                sparseArray.append(sparseCount, jobs[i]);
                sparseCount++;
            }
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
