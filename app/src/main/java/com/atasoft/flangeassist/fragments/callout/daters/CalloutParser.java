package com.atasoft.flangeassist.fragments.callout.daters;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.atasoft.flangeassist.fragments.callout.daters.CalloutClient;

/**
 * Created by ataboo on 4/14/2017.
 */

public class CalloutParser {
    private CalloutClient calloutClient;

    public CalloutParser() {
        calloutClient = new CalloutClient();
    }

    /**
     * Inject client for unit testing.
     *
     * @param client Client can be injected for unit testing.
     */
    public CalloutParser(CalloutClient client) {
        this.calloutClient = client;
    }

    public void parseCallout(Context ctx, final CalloutListener listener)
    {
        calloutClient.getCallout(ctx, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onSuccess(new CalloutResponse(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FlangeAssistGradle", "Failed volley response with: " + error.toString());
                listener.onFail();
            }
        });
    }

}
