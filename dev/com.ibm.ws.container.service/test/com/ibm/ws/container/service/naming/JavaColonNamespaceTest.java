/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.naming;

import org.junit.Assert;
import org.junit.Test;

public class JavaColonNamespaceTest {
    @Test
    public void testMatch() {
        for (NamingConstants.JavaColonNamespace namespace : NamingConstants.JavaColonNamespace.values()) {
            Assert.assertEquals(namespace.toString(), namespace, NamingConstants.JavaColonNamespace.match(namespace.prefix() + "x"));
        }
    }

    @Test
    public void testUnprefix() {
        for (NamingConstants.JavaColonNamespace namespace : NamingConstants.JavaColonNamespace.values()) {
            Assert.assertEquals(namespace.toString(), "x", namespace.unprefix(namespace.prefix() + "x"));
        }
    }

    @Test
    public void testIsComp() {
        for (NamingConstants.JavaColonNamespace namespace : NamingConstants.JavaColonNamespace.values()) {
            Assert.assertEquals(namespace.prefix().startsWith("java:comp/"), namespace.isComp());
        }
    }

    @Test
    public void testFromName() {
        for (NamingConstants.JavaColonNamespace namespace : NamingConstants.JavaColonNamespace.values()) {
            Assert.assertEquals(namespace.toString(), namespace, NamingConstants.JavaColonNamespace.fromName(namespace.qualifiedName()));
        }

        for (NamingConstants.JavaColonNamespace namespace : NamingConstants.JavaColonNamespace.values()) {
            NamingConstants.JavaColonNamespace ns = NamingConstants.JavaColonNamespace.fromName(namespace.prefix());
            Assert.assertNull("Expected null when calling fromName(" + namespace.prefix() + "), but got " + ns, ns);
        }
    }
}
