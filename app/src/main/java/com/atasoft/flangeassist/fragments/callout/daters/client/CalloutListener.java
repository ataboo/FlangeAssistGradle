package com.atasoft.flangeassist.fragments.callout.daters.client;


import com.atasoft.flangeassist.fragments.callout.daters.CalloutResponse;

public interface CalloutListener {
    void onSuccess(CalloutResponse response);
    void onFail();
}
