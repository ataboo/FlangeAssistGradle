package com.atasoft.flangeassist.fragments.callout.daters;

import android.util.Log;

import com.atasoft.flangeassist.fragments.callout.daters.hire.ClassificationFilter;
import com.atasoft.flangeassist.fragments.callout.daters.hire.ClassificationTag;
import com.atasoft.flangeassist.fragments.callout.daters.hire.Manpower;
import com.atasoft.flangeassist.fragments.callout.daters.hire.Namehire;
import com.atasoft.flangeassist.fragments.callout.table.DetailRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class CalloutJob {
    DetailRow id;
    DetailRow jobName;
    DetailRow contractor;
    DetailRow openTo;
    DetailRow shift;
    DetailRow workType;
    DetailRow hours;
    DetailRow duration;
    DetailRow accomodation;
    DetailRow drugTesting;
    DetailRow comments;
    Date dateTime;
    DetailRow dateTimeRow;
    Manpower[] manpowers;
    Namehire[] namehires;
    HashSet<ClassificationTag> tags = new HashSet<>();

    int manpowerCount = 0;

    public CalloutJob(JSONObject jobObject) throws JSONException, ParseException {
        id = new DetailRow(DetailRow.Type.BODY, "Job Id", jobObject.getString("id"));
        jobName = new DetailRow(DetailRow.Type.BODY, "Job Name", jobObject.getString("job_name"));
        contractor = new DetailRow(DetailRow.Type.BODY, "Contractor", jobObject.getString("contractor"));
        openTo = new DetailRow(DetailRow.Type.BODY, "Open To", jobObject.getString("open_to"));
        shift = new DetailRow(DetailRow.Type.BODY, "Shift", jobObject.getString("dayshift").equals("1") ? "Dayshift" : "Nightshift");
        workType = new DetailRow(DetailRow.Type.BODY, "Work Type", jobObject.getString("work_type"));
        hours = new DetailRow(DetailRow.Type.BODY, "Hours", jobObject.getString("hours"));
        duration = new DetailRow(DetailRow.Type.BODY, "Duration", jobObject.getString("duration"));
        accomodation = new DetailRow(DetailRow.Type.BODY, "Accommodation", jobObject.getString("accommodation"));
        drugTesting = new DetailRow(DetailRow.Type.BODY, "Drug Testing", jobObject.getString("drug_testing"));
        comments = new DetailRow(DetailRow.Type.BODY, "Comments", jobObject.getString("comments"));
        dateTime = CalloutResponse.DATE_FORMAT.parse(jobObject.getString("date_time"));
        dateTimeRow = DetailRow.makeFromDate(DetailRow.Type.BODY, "Date/Time", dateTime);

        manpowers = jsonArrayMap(jobObject.getJSONArray("manpowers"), Manpower.class);
        for (Manpower manpower: manpowers) {
            manpowerCount += manpower.getCount();
            tags.addAll(Arrays.asList(manpower.getMatchingTags()));
        }
        namehires = jsonArrayMap(jobObject.getJSONArray("namehires"), Namehire.class);
        for (Namehire namehire: namehires) {
            tags.addAll(Arrays.asList(namehire.getMatchingTags()));
        }

        Log.i("Dump", jobObject.toString());

    }

    public DetailRow getGroupRow() {
        return new DetailRow(DetailRow.Type.GROUP, id.getDetail() + " | " + jobName.getDetail(), contractor.getDetail());
    }

    public DetailRow getDetailRow(int position) {
        return getExpandedRows()[position];
    }

    public DetailRow[] getExpandedRows() {
        ArrayList<DetailRow> expandedRows = new ArrayList<>();
        if (manpowers.length > 0) {
            expandedRows.add(new DetailRow(DetailRow.Type.HEADER, "Open Positions", "("+Integer.toString(manpowerCount)+")"));
        }
        for (Manpower manpower: manpowers) {
            expandedRows.add(manpower.toDetailRow());
        }


        if (namehires.length > 0) {
            expandedRows.add(new DetailRow(DetailRow.Type.HEADER, "Name Hires", "("+Integer.toString(namehires.length)+")"));
        }
        for (Namehire namehire: namehires) {
            expandedRows.add(namehire.toDetailRow());
        }
        expandedRows.add(new DetailRow(DetailRow.Type.HEADER, "Details", ""));
        expandedRows.add(shift);
        expandedRows.add(dateTimeRow);
        expandedRows.add(openTo);
        expandedRows.add(hours);
        expandedRows.add(duration);
        expandedRows.add(workType);
        expandedRows.add(comments);
        expandedRows.add(accomodation);


        return expandedRows.toArray(new DetailRow[expandedRows.size()]);
    }

    public boolean matchesFilters(EnumSet<ClassificationFilter> filters) {
        for (ClassificationFilter filter : filters) {
            if (filter.matchesTags(this.tags)) {
                return true;
            }
        }

        return false;
    }

    public int getDetailRowCount() {
        return getExpandedRows().length;
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
        sb.append(", \nshift='").append(shift).append('\'');
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
