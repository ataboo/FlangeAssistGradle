package com.atasoft.robotests;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.atasoft.flangeassist.fragments.callout.daters.client.CalloutClient;
import com.atasoft.flangeassist.fragments.callout.daters.client.CalloutListener;
import com.atasoft.flangeassist.fragments.callout.daters.client.CalloutParser;
import com.atasoft.flangeassist.fragments.callout.daters.CalloutResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;


public class CalloutParserRobo extends RoboTestCase {
    @Mock
    CalloutClient calloutClient;

    @Mock
    Context mockCtx;

    private Answer calloutAnswer = new Answer() {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            CalloutClient client = (CalloutClient) invocation.getMock();

            Thread.sleep(100);

            Response.Listener<String> response = (Response.Listener<String>) args[1];
            response.onResponse(loadResourceFile("callout_test_long.json"));

            return null;
        }
    };

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void parsesCallout() {
        doAnswer(calloutAnswer).when(calloutClient).getCallout(any(Context.class), any(Response.Listener.class), any(Response.ErrorListener.class));
        CalloutParser parser = new CalloutParser(calloutClient);

        parser.parseCallout(mockCtx, new CalloutListener() {
            @Override
            public void onSuccess(CalloutResponse response) {
                Assert.assertNotNull(response);
                Assert.assertEquals(32, response.jobs.length);
            }

            @Override
            public void onFail(VolleyError error) {
                Assert.fail("Callout response failed!");
            }

            @Override
            public void onBoth() {

            }
        });
    }
}
