package com.atasoft.flangeassist.fragments.callout.daters.client;


import com.android.volley.VolleyError;
import com.atasoft.flangeassist.fragments.callout.daters.CalloutResponse;

public interface CalloutListener {
    void onSuccess(CalloutResponse response);
    void onFail(VolleyError error);
    void onBoth();
}
