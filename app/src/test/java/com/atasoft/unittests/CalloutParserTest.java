package com.atasoft.unittests;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.atasoft.flangeassist.fragments.callout.daters.client.CalloutClient;
import com.atasoft.flangeassist.fragments.callout.daters.client.CalloutListener;
import com.atasoft.flangeassist.fragments.callout.daters.client.CalloutParser;
import com.atasoft.flangeassist.fragments.callout.daters.CalloutResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

import org.junit.Assert;

/**
 * Created by ataboo on 4/14/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class CalloutParserTest {
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
            response.onResponse(loadTestCallout("callout_test.json"));

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

    private String loadTestCallout(String filename) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            while((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
