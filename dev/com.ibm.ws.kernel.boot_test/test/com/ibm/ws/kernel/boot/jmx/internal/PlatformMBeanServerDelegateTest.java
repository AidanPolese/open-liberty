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
package com.ibm.ws.kernel.boot.jmx.internal;

import static org.junit.Assert.assertTrue;

import javax.management.MBeanServerDelegate;

import org.junit.Test;

/**
 *
 */
public class PlatformMBeanServerDelegateTest {

    @Test
    public void testPlatformMBeanServerDelegateAttributes() throws Exception {
        MBeanServerDelegate mBeanServerDelegate = new PlatformMBeanServerDelegate();
        assertTrue("Expected that server ID starts with WebSphere",
                   mBeanServerDelegate.getMBeanServerId().startsWith("WebSphere"));
    }
}
