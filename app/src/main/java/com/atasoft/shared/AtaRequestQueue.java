package com.atasoft.shared;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/**
 * Singleton implementation of Volley's Request Queue.
 */
public class AtaRequestQueue {
    private static AtaRequestQueue mInstance;
    private RequestQueue mRequestQueue;

    private AtaRequestQueue(Context context) {
        mRequestQueue = getRequestQueue(context);
    }

    public static synchronized AtaRequestQueue getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new AtaRequestQueue(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, Context context) {
        getRequestQueue(context).add(req);
    }
}