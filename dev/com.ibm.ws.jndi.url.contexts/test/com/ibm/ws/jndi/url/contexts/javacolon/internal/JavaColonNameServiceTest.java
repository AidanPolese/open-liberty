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
package com.ibm.ws.jndi.url.contexts.javacolon.internal;

import javax.naming.NameClassPair;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.ws.container.service.naming.NamingConstants;

public class JavaColonNameServiceTest {
    @Test
    public void testGetObjectInstance() throws Exception {
        TestJavaColonNameService service = new TestJavaColonNameService();
        Assert.assertEquals(null, service.getObjectInstance(NamingConstants.JavaColonNamespace.MODULE, "ModuleName"));
        Assert.assertEquals(null, service.getObjectInstance(NamingConstants.JavaColonNamespace.MODULE, "AppName"));
        Assert.assertEquals(null, service.getObjectInstance(NamingConstants.JavaColonNamespace.MODULE, "env"));
        Assert.assertEquals(null, service.getObjectInstance(NamingConstants.JavaColonNamespace.APP, "ModuleName"));
        Assert.assertEquals(null, service.getObjectInstance(NamingConstants.JavaColonNamespace.APP, "AppName"));
        Assert.assertEquals(null, service.getObjectInstance(NamingConstants.JavaColonNamespace.APP, "env"));

        service.setAppName("app");
        service.setModuleName("module");
        Assert.assertEquals("module", service.getObjectInstance(NamingConstants.JavaColonNamespace.MODULE, "ModuleName"));
        Assert.assertEquals(null, service.getObjectInstance(NamingConstants.JavaColonNamespace.MODULE, "AppName"));
        Assert.assertEquals(null, service.getObjectInstance(NamingConstants.JavaColonNamespace.MODULE, "env"));
        Assert.assertEquals(null, service.getObjectInstance(NamingConstants.JavaColonNamespace.APP, "ModuleName"));
        Assert.assertEquals("app", service.getObjectInstance(NamingConstants.JavaColonNamespace.APP, "AppName"));
        Assert.assertEquals(null, service.getObjectInstance(NamingConstants.JavaColonNamespace.APP, "env"));
    }

    @Test
    public void testHasObjectWithPrefix() throws Exception {
        TestJavaColonNameService service = new TestJavaColonNameService();
        Assert.assertFalse(service.hasObjectWithPrefix(NamingConstants.JavaColonNamespace.MODULE, ""));
        Assert.assertFalse(service.hasObjectWithPrefix(NamingConstants.JavaColonNamespace.MODULE, "env"));
        Assert.assertFalse(service.hasObjectWithPrefix(NamingConstants.JavaColonNamespace.APP, ""));
        Assert.assertFalse(service.hasObjectWithPrefix(NamingConstants.JavaColonNamespace.APP, "env"));

        service.setAppName("app");
        service.setModuleName("module");
        Assert.assertTrue(service.hasObjectWithPrefix(NamingConstants.JavaColonNamespace.MODULE, ""));
        Assert.assertFalse(service.hasObjectWithPrefix(NamingConstants.JavaColonNamespace.MODULE, "env"));
        Assert.assertTrue(service.hasObjectWithPrefix(NamingConstants.JavaColonNamespace.APP, ""));
        Assert.assertFalse(service.hasObjectWithPrefix(NamingConstants.JavaColonNamespace.APP, "env"));
    }

    @Test
    public void testListInstances() throws Exception {
        TestJavaColonNameService service = new TestJavaColonNameService();
        Assert.assertEquals(NameClassPairTestHelper.newSet(),
                            NameClassPairTestHelper.newSet(service.listInstances(NamingConstants.JavaColonNamespace.MODULE, "")));
        Assert.assertEquals(NameClassPairTestHelper.newSet(),
                            NameClassPairTestHelper.newSet(service.listInstances(NamingConstants.JavaColonNamespace.MODULE, "env")));
        Assert.assertEquals(NameClassPairTestHelper.newSet(),
                            NameClassPairTestHelper.newSet(service.listInstances(NamingConstants.JavaColonNamespace.APP, "")));
        Assert.assertEquals(NameClassPairTestHelper.newSet(),
                            NameClassPairTestHelper.newSet(service.listInstances(NamingConstants.JavaColonNamespace.APP, "env")));

        service.setAppName("app");
        service.setModuleName("module");
        Assert.assertEquals(NameClassPairTestHelper.newSet(new NameClassPair("ModuleName", String.class.getName())),
                            NameClassPairTestHelper.newSet(service.listInstances(NamingConstants.JavaColonNamespace.MODULE, "")));
        Assert.assertEquals(NameClassPairTestHelper.newSet(),
                            NameClassPairTestHelper.newSet(service.listInstances(NamingConstants.JavaColonNamespace.MODULE, "env")));
        Assert.assertEquals(NameClassPairTestHelper.newSet(new NameClassPair("AppName", String.class.getName())),
                            NameClassPairTestHelper.newSet(service.listInstances(NamingConstants.JavaColonNamespace.APP, "")));
        Assert.assertEquals(NameClassPairTestHelper.newSet(),
                            NameClassPairTestHelper.newSet(service.listInstances(NamingConstants.JavaColonNamespace.APP, "env")));
    }
}
