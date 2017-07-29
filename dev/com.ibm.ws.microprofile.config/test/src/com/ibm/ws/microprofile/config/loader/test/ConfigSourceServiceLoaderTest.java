/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.microprofile.config.loader.test;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.Test;

import com.ibm.ws.microprofile.config.TestUtils;

public class ConfigSourceServiceLoaderTest {

    //Service Loader is used when default Config Sources are added
    @Test
    public void testConfigSourceServiceLoader() {
        Config configA = null;
        try {
            configA = ConfigProvider.getConfig();
            Iterable<String> keys = configA.getPropertyNames();
            TestUtils.assertContains(keys, "SLKey1");
            TestUtils.assertContains(keys, "SLKey2");
            TestUtils.assertContains(keys, "SLKey3");
            TestUtils.assertContains(keys, "SLKey4");
        } finally {
            if (configA != null) {
                ConfigProviderResolver.instance().releaseConfig(configA);
            }
        }
    }

}
