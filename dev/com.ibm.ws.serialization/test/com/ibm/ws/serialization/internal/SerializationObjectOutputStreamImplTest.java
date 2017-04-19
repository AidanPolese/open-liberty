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
package com.ibm.ws.serialization.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;

public class SerializationObjectOutputStreamImplTest {
    private static Object deserialize(ByteArrayOutputStream baos) throws IOException, ClassNotFoundException {
        return new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject();
    }

    @Test
    public void testReplaceObject() throws Exception {
        final Object[] replace = new Object[1];
        SerializationContextImpl context = new SerializationContextImpl(new SerializationServiceImpl()) {
            @Override
            public boolean isReplaceObjectNeeded() {
                return replace[0] != null;
            }

            @Override
            public Object replaceObject(Object object) {
                if (!isReplaceObjectNeeded()) {
                    throw new UnsupportedOperationException();
                }
                return replace[0];
            }
        };

        replace[0] = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new SerializationObjectOutputStreamImpl(baos, context);
        oos.writeObject(0);
        oos.close();
        Assert.assertEquals(0, deserialize(baos));

        replace[0] = 1;
        baos = new ByteArrayOutputStream();
        oos = new SerializationObjectOutputStreamImpl(baos, context);
        oos.writeObject(0);
        oos.close();
        Assert.assertEquals(1, deserialize(baos));
    }
}
