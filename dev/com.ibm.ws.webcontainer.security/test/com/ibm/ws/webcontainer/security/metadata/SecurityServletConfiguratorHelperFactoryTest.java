package com.ibm.ws.webcontainer.security.metadata;

import static org.junit.Assert.assertNotNull;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.ibm.ws.container.service.config.ServletConfigurator;
import com.ibm.ws.webcontainer.security.metadata.SecurityServletConfiguratorHelperFactory;

/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

/**
 *
 */
public class SecurityServletConfiguratorHelperFactoryTest {
    private final Mockery mockery = new JUnit4Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    final ServletConfigurator configurator = mockery.mock(ServletConfigurator.class);

    /**
     * Test method for
     * {@link com.ibm.ws.webcontainer.security.metadata.SecurityServletConfiguratorHelperFactory#createConfiguratorHelper(com.ibm.ws.container.service.config.ServletConfigurator)}
     * .
     */
    @Test
    public void testCreateConfiguratorHelper() {
        SecurityServletConfiguratorHelperFactory configHelperFactory = new SecurityServletConfiguratorHelperFactory();
        assertNotNull("The config helper factory should create a config helper instance that is not null", configHelperFactory.createConfiguratorHelper(configurator));
    }
}
