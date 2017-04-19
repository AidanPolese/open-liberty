/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jmx.connector.server.rest.helpers;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import test.common.SharedOutputManager;

/**
 * Test for {@link com.ibm.ws.jmx.connector.server.rest.helpers.POJOHelper }
 */
public class POJOHelperTest {

    static SharedOutputManager outputMgr = SharedOutputManager.getInstance();
    @Rule
    public TestRule managerRule = outputMgr;

    private POJOHelper pojoHelper;

    @Before
    public void setUp() {
        pojoHelper = new POJOHelper();
    }

    @After
    public void tearDown() {
        pojoHelper = null;
    }

    /**
     * Test method for {@link com.ibm.ws.jmx.connector.server.rest.helpers.POJOHelper#getPOJOObject()}.
     */
    @Test
    public void testGetPOJOObject() throws Exception {
        final String json = "{\"pojoExample\":{\"value\":\"valueStructure\",\"type\":\"typeStructure\"";
        String creds = pojoHelper.getPOJOObject();
        assertTrue("POJO does not contain JSON objects", creds.contains(json));
    }

}
