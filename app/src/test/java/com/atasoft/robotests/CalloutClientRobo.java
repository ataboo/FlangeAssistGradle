package com.atasoft.robotests;


import com.atasoft.flangeassist.fragments.callout.daters.client.CalloutClient;

import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;

import static org.junit.Assert.*;

public class CalloutClientRobo extends RoboTestCase {
    @Mock
    CalloutClient mockClient;

    @Test
    public void canReadConfig() {
        HashMap<String, String> config = (new CalloutClient()).getConfig(activity);

        assertNotNull(config.get("callout-endpoint"));
        assertNotNull(config.get("callout-token"));
    }
}
