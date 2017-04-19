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
package com.ibm.ws.serialization.internal;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.ws.serialization.DeserializationObjectResolver;

public class DeserializationContextImplTest {
    @Test
    public void testResolveObject() throws Exception {
        DeserializationContextImpl context = new DeserializationContextImpl(new SerializationServiceImpl());
        Assert.assertFalse(context.isResolveObjectNeeded());
        Assert.assertSame(context, context.resolveObject(context));
    }

    @Test
    public void testResolveObjectWithResolver() throws Exception {
        DeserializationContextImpl context = new DeserializationContextImpl(new SerializationServiceImpl());
        context.addObjectResolver(new DeserializationObjectResolver() {
            @Override
            public Object resolveObject(@Sensitive Object object) {
                if ((Integer) object < 2) {
                    return (Integer) object + 1;
                }
                return null;
            }
        });
        context.addObjectResolver(new DeserializationObjectResolver() {
            @Override
            public Object resolveObject(@Sensitive Object object) throws IOException {
                if ((Integer) object < 2) {
                    throw new IllegalStateException();
                }
                if (((Integer) object).equals(3)) {
                    throw new IOException();
                }
                return null;
            }
        });
        Assert.assertTrue(context.isResolveObjectNeeded());
        Assert.assertEquals(1, context.resolveObject(0));
        Assert.assertEquals(2, context.resolveObject(1));
        Assert.assertEquals(2, context.resolveObject(2));
        try {
            context.resolveObject(3);
            Assert.fail("expected IOException");
        } catch (IOException e) {
        }
    }
}
