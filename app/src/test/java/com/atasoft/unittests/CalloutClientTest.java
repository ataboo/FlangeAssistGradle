package com.atasoft.unittests;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.atasoft.flangeassist.fragments.callout.daters.CalloutClient;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.HashMap;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class CalloutClientTest{
    @Mock
    Context mockCtx;
    @Mock
    AssetManager assetManager;
    @Mock
    InputStream inputStream;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(assetManager).when(mockCtx).getAssets();
        String testConfig = "{\"callout_token\": \"this is token\", \"callout_endpoint\": \"endpoint\"}";
        doReturn(inputStream).when(assetManager).open(anyString());

    }

    @Test
    public void canReadConfig() {
        HashMap<String, String> config = (new CalloutClient()).getConfig(mockCtx);

        Log.i("Config dump!", config.toString());
    }
}
