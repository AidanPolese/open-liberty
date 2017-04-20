/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ssl.internal;

import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class GenericSSLConfigServiceTest {
    private final Map<String, Object> dict = new Hashtable<String, Object>();

    private GenericSSLConfigService service;

    static class TestGenericSSLConfigService extends GenericSSLConfigService {}

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        service = null;

    }

    /**
     * Test method for {@link com.ibm.websphere.ssl.osgi.GenericSSLConfig#getProperties()}.
     *
     * getProperties should not return anything that is not a string value.
     */
    @Test
    public void getProperties_NotStringValue() throws Exception {
        dict.put("newKey", new Object());

        service = new TestGenericSSLConfigService();
        service.activate("test", dict);

        Map<String, Object> configMap = service.getProperties();
        assertTrue("The map should be empty", configMap.isEmpty());
    }

    /**
     * Test method for {@link com.ibm.websphere.ssl.osgi.GenericSSLConfig#getProperties()}.
     *
     * getProperties should not return an empty map if no configuration values are there
     */
    @Test
    public void getProperties_NoConfigValues() throws Exception {
        service = new TestGenericSSLConfigService();
        service.activate("test", dict);

        Map<String, Object> configMap = service.getProperties();
        assertTrue("The map should be empty", configMap.isEmpty());
    }

}
