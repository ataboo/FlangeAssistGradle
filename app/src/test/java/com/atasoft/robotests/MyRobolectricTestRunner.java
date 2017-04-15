package com.atasoft.robotests;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Had to override RobolectricTestRunner to point it at the proper build directories.
 */
public class MyRobolectricTestRunner extends RobolectricTestRunner {
    @Override
    protected Config buildGlobalConfig() {
        return Config.Builder.defaults()
                .setPackageName("com.atasoft.flangeassist")
                .setManifest("app/build/intermediates/manifests/full/debug/AndroidManifest.xml")
                .setResourceDir("../../../res/merged/debug") // relative to manifest
                .setAssetDir("../../../../../src/main/assets") // relative to manifest
                .build();
    }

    public MyRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }
}
