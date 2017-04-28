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
package com.ibm.wsspi.security.auth.callback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class WSMappingPropertiesCallbackTest {

    private static final String PROMPT = "Mapping Properties (HashMap): ";
    @SuppressWarnings("rawtypes")
    Map properties;

    @SuppressWarnings("rawtypes")
    @Before
    public void setUp() {
        properties = new HashMap();
    }

    @Test
    public void testGetProperties() {
        WSMappingPropertiesCallback callback = new WSMappingPropertiesCallback(PROMPT);
        callback.setProperties(properties);

        assertNotNull("The properties must be set in the callback.", callback.getProperties());
        assertEquals("The prompt must be set in the callback.", PROMPT, callback.getPrompt());
    }

    @Test
    public void testGetPropertiesSetInConstructor() {
        WSMappingPropertiesCallback callback = new WSMappingPropertiesCallback(PROMPT, properties);

        assertNotNull("The properties must be set in the callback.", callback.getProperties());
        assertEquals("The prompt must be set in the callback.", PROMPT, callback.getPrompt());
    }

    @Test
    public void testToString() {
        WSMappingPropertiesCallback callback = new WSMappingPropertiesCallback(PROMPT);

        assertEquals("The toString() method must return the class name.", WSMappingPropertiesCallback.class.getName(), callback.toString());
    }

}
