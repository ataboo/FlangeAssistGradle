package com.atasoft.flangeassist.fragments.callout.daters;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.atasoft.flangeassist.fragments.callout.AtaRequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CalloutClient {
    private static HashMap<String, String> mConfig;

    public void getCallout(Context ctx, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        final HashMap<String, String> config = getConfig(ctx);

        StringRequest request = new StringRequest(config.get("callout-endpoint"), listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Callout-Token", config.get("callout-token"));
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        AtaRequestQueue.getInstance(ctx).addToRequestQueue(request, ctx);
    }

    public HashMap<String, String> getConfig(Context ctx) {
        if (mConfig == null) {
            try {
                JSONObject configObj = loadConfig(ctx);
                mConfig = hashConfig(configObj);
            } catch (JSONException|IOException e) {
                e.printStackTrace();
            }
        }

        return mConfig;
    }

    private JSONObject loadConfig(Context ctx) throws IOException, JSONException {
        InputStream is = ctx.getAssets().open("config.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String configText = new String(buffer);
        return new JSONObject(configText);
    }

    private HashMap<String, String> hashConfig(JSONObject configObj) throws JSONException {
        Iterator<String> iterator = configObj.keys();

        HashMap<String, String> configMap = new HashMap<>();
        while(iterator.hasNext()) {
            String key = iterator.next();
            configMap.put(key, configObj.getString(key));
        }

        return configMap;
    }
}
