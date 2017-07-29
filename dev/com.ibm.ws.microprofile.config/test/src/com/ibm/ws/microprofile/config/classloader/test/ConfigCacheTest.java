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
package com.ibm.ws.microprofile.config.classloader.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.Test;

public class ConfigCacheTest {

    @Test
    public void testConfigCache() {
        Config configA = null;
        Config configB = null;
        try {
            configA = ConfigProvider.getConfig();
            configB = ConfigProvider.getConfig();
            assertTrue(configA == configB);
        } finally {
            if (configA != null) {
                ConfigProviderResolver.instance().releaseConfig(configA);
            }
            if (configB != null && configB != configA) {
                ConfigProviderResolver.instance().releaseConfig(configB);
            }
        }
    }

    @Test
    public void testSetConfig() {
        Config configA = null;
        Config configB = null;
        try {
            configA = ConfigProviderResolver.instance().getBuilder().build();
            ConfigProviderResolver.instance().registerConfig(configA, Thread.currentThread().getContextClassLoader());
            configB = ConfigProvider.getConfig();
            assertEquals(configA, configB);
        } finally {
            if (configA != null) {
                ConfigProviderResolver.instance().releaseConfig(configA);
            }
            if (configB != null && configB != configA) {
                ConfigProviderResolver.instance().releaseConfig(configB);
            }
        }
    }

    @Test
    public void testReleaseConfig() {
        Config configA = null;
        Config configB = null;
        try {
            configA = ConfigProviderResolver.instance().getBuilder().build();
            ConfigProviderResolver.instance().registerConfig(configA, Thread.currentThread().getContextClassLoader());
            ConfigProviderResolver.instance().releaseConfig(configA);
            configB = ConfigProvider.getConfig();
            assertNotSame(configA, configB);
        } finally {
            if (configA != null) {
                ConfigProviderResolver.instance().releaseConfig(configA);
            }
            if (configB != null && configB != configA) {
                ConfigProviderResolver.instance().releaseConfig(configB);
            }
        }
    }

    @Test
    public void testSetConfigException() {
        Config configA = null;
        Config configB = null;
        try {
            configA = ConfigProvider.getConfig();
            configB = ConfigProviderResolver.instance().getBuilder().build();
            ConfigProviderResolver.instance().registerConfig(configB, Thread.currentThread().getContextClassLoader());
            fail("Exception not thrown");
        } catch (IllegalStateException e) {
            //expected
        } finally {
            if (configA != null) {
                ConfigProviderResolver.instance().releaseConfig(configA);
            }
            if (configB != null && configB != configA) {
                ConfigProviderResolver.instance().releaseConfig(configB);
            }
        }
    }

    @Test
    public void testConfigClassLoader() {
        Config configA = null;
        Config configB = null;
        try {
            configA = ConfigProvider.getConfig();

            ClassLoader cl = new MyClassLoader(Thread.currentThread().getContextClassLoader());
            configB = ConfigProvider.getConfig(cl);

            assertFalse(configA == configB);
        } finally {
            if (configA != null) {
                ConfigProviderResolver.instance().releaseConfig(configA);
            }
            if (configB != null) {
                ConfigProviderResolver.instance().releaseConfig(configB);
            }
        }
    }

    private static class MyClassLoader extends ClassLoader {
        public MyClassLoader(ClassLoader parent) {
            super(parent);
        }
    }
}
