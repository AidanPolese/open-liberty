/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.websphere.security.wim.ras_test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ibm.websphere.security.wim.ras.WIMTraceHelper;

public class WIMTraceHelperTest {

    @Test
    public void testAllBeans() {
        Customer cs = new Customer();
        cs.setFirtName("Sunil");
        cs.setLastName("George");
        cs.setAddress("EGL IBM D Block");
        cs.setPinCode(560066);

        PhoneInfo pi = new PhoneInfo();
        pi.setLandLineNo("0808237333");
        pi.setMobileNo("9879787888");
        pi.setStdCode("080");

        Geography geo = new Geography();
        geo.setCity("Bangalore");
        geo.setCountry("India");
        geo.setState("Karnataka");

        cs.setPi(pi);
        cs.setGeo(geo);

        try {
            String trace = WIMTraceHelper.trace(cs);
            assertTrue("address=EGL IBM D Block - not Found", trace.contains("address=EGL IBM D Block"));
            assertTrue("firtName=Sunil - not Found", trace.contains("firtName=Sunil"));
            assertTrue("city=Bangalore -  not Found", trace.contains("city=Bangalore"));
            assertTrue("country=India - not Found", trace.contains("country=India"));
            assertTrue("state=Karnataka - not Found", trace.contains("state=Karnataka"));
            assertTrue("lastName=George - not Found", trace.contains("lastName=George"));
            assertTrue("landLineNo=0808237333 - not Found", trace.contains("landLineNo=0808237333"));
            assertTrue("mobileNo=9879787888 - not Found", trace.contains("mobileNo=9879787888"));
            assertTrue("stdCode=080 - not Found", trace.contains("stdCode=080"));
            assertTrue("mobileNo=9879787888 - not Found", trace.contains("mobileNo=9879787888"));
            assertTrue("pinCode=560066 - not Found", trace.contains("pinCode=560066"));
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            assertEquals("Call completed successfully", true, false + " with " + errorMessage);
        }
    }

    @Test
    public void testPrintPrimitiveObjects() {
        assertEquals("[-2, 0, 2]", WIMTraceHelper.printPrimitiveArray(new short[] { -2, 0, 2 }));
        assertEquals("[false, true, false]", WIMTraceHelper.printPrimitiveArray(new boolean[] { false, true, false }));
        assertEquals("[-3.14, 0.12345, 1337.0]", WIMTraceHelper.printPrimitiveArray(new double[] { -3.14, 0.12345, 1337.0 }));
        assertEquals("[-2, 0, 2]", WIMTraceHelper.printPrimitiveArray(new byte[] { -2, 0, 2 }));
        assertEquals("[Z, &, 6]", WIMTraceHelper.printPrimitiveArray(new char[] { 'Z', '&', '6' }));
        assertEquals("[-2, 0, 2]", WIMTraceHelper.printPrimitiveArray(new int[] { -2, 0, 2 }));
        assertEquals("[-2, 0, 2]", WIMTraceHelper.printPrimitiveArray(new long[] { -2L, 0, 2L }));
        assertEquals("[-2.0E10, 0.0, 2.0E10]", WIMTraceHelper.printPrimitiveArray(new float[] { -2e10f, 0, 2e10f }));
    }
}
