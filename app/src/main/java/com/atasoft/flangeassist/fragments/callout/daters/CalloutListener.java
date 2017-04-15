package com.atasoft.flangeassist.fragments.callout.daters;


public interface CalloutListener {
    void onSuccess(CalloutResponse response);
    void onFail();
}
