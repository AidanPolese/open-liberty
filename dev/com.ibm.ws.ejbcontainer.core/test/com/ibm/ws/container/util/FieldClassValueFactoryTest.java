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
package com.ibm.ws.container.util;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.ws.ejbcontainer.util.FieldClassValue;
import com.ibm.ws.ejbcontainer.util.FieldClassValueFactory;

public class FieldClassValueFactoryTest {
    private static class TestClass {
        @SuppressWarnings("unused")
        private String value;
    }

    @Test
    public void test() throws Exception {
        FieldClassValue cv = FieldClassValueFactory.create("value");
        Assert.assertEquals(TestClass.class.getDeclaredField("value"), cv.get(TestClass.class));
        // Again to test caching if any.
        Assert.assertEquals(TestClass.class.getDeclaredField("value"), cv.get(TestClass.class));
    }
}
