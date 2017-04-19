/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.injection;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.ws.injectionengine.osgi.internal.ResAutoLinkReferenceFactoryImpl;

public class ResAutoLinkReferenceFactoryImplTest {
    @Test
    public void testGetBindingName() {
        Assert.assertEquals("x", ResAutoLinkReferenceFactoryImpl.getBindingName("x"));
        Assert.assertEquals("x/y", ResAutoLinkReferenceFactoryImpl.getBindingName("x/y"));
        Assert.assertEquals("", ResAutoLinkReferenceFactoryImpl.getBindingName("java:"));
        Assert.assertEquals("x", ResAutoLinkReferenceFactoryImpl.getBindingName("java:x"));
        Assert.assertEquals("", ResAutoLinkReferenceFactoryImpl.getBindingName("java:x/"));
        Assert.assertEquals("y", ResAutoLinkReferenceFactoryImpl.getBindingName("java:x/y"));
        Assert.assertEquals("y/z", ResAutoLinkReferenceFactoryImpl.getBindingName("java:x/y/z"));
        Assert.assertEquals("env", ResAutoLinkReferenceFactoryImpl.getBindingName("java:x/env"));
        Assert.assertEquals("", ResAutoLinkReferenceFactoryImpl.getBindingName("java:x/env/"));
        Assert.assertEquals("y", ResAutoLinkReferenceFactoryImpl.getBindingName("java:x/env/y"));
        Assert.assertEquals("y/z", ResAutoLinkReferenceFactoryImpl.getBindingName("java:x/env/y/z"));
    }
}
