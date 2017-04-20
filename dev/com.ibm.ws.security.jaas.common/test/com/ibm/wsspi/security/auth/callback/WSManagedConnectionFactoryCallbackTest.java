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

import javax.resource.spi.ManagedConnectionFactory;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

public class WSManagedConnectionFactoryCallbackTest {

    private static final String HINT = "Target ManagedConnectionFactory: ";

    private final Mockery mockery = new JUnit4Mockery();
    private ManagedConnectionFactory managedConnectionFactory;

    @Before
    public void setUp() {
        managedConnectionFactory = mockery.mock(ManagedConnectionFactory.class);
    }

    /*
     * Note that the interface's method is getManagedConnectionFacotry and it has to be retained
     * for compatibility with tWAS.
     */
    @Test
    public void testGetManagedConnectionFacotry() {
        WSManagedConnectionFactoryCallback callback = new WSManagedConnectionFactoryCallback(HINT);

        callback.setManagedConnectionFactory(managedConnectionFactory);
        assertEquals("The ManagedConnectionFactory must be set in the callback.", managedConnectionFactory, callback.getManagedConnectionFacotry());
    }
}
