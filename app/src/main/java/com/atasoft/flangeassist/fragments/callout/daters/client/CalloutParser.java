package com.atasoft.flangeassist.fragments.callout.daters.client;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.atasoft.flangeassist.fragments.callout.daters.CalloutResponse;

/**
 * Invokes the CalloutClient and generates a CalloutResponse on success.
 *
 */
public class CalloutParser {
    private CalloutClient calloutClient;

    public CalloutParser() {
        this.calloutClient = new CalloutClient();
    }

    /**
     * Convenience constructor allowing injected client.
     *
     * @param client Mocked Client can be injected for unit testing.
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
                listener.onBoth();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FlangeAssistGradle", "Failed volley response with: " + error.toString());
                listener.onFail(error);
                listener.onBoth();
            }
        });
    }

}
