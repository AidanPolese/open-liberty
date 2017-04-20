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
package com.ibm.ws.serialization.internal;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.ws.serialization.SerializationObjectReplacer;

public class SerializationContextImplTest {
    @Test
    public void testReplaceObject() {
        SerializationContextImpl context = new SerializationContextImpl(new SerializationServiceImpl());
        Assert.assertFalse(context.isReplaceObjectNeeded());
        Assert.assertSame(context, context.replaceObject(context));
    }

    @Test
    public void testReplaceObjectWithResolver() {
        SerializationContextImpl context = new SerializationContextImpl(new SerializationServiceImpl());
        context.addObjectReplacer(new SerializationObjectReplacer() {
            @Override
            public Object replaceObject(@Sensitive Object object) {
                if ((Integer) object < 2) {
                    return (Integer) object + 1;
                }
                return null;
            }
        });
        context.addObjectReplacer(new SerializationObjectReplacer() {
            @Override
            public Object replaceObject(@Sensitive Object object) {
                if ((Integer) object < 2) {
                    throw new IllegalStateException();
                }
                return null;
            }
        });
        Assert.assertTrue(context.isReplaceObjectNeeded());
        Assert.assertEquals(1, context.replaceObject(0));
        Assert.assertEquals(2, context.replaceObject(1));
        Assert.assertEquals(2, context.replaceObject(2));
    }
}
