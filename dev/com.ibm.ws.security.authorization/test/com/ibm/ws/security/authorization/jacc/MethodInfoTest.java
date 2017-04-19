/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.authorization.jacc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import test.common.SharedOutputManager;

/**
 *
 */
public class MethodInfoTest {

    static SharedOutputManager outputMgr = SharedOutputManager.getInstance();
    @Rule
    public TestRule outputRule = outputMgr;

    /**
     * Tests getMethodName
     * Expected result: get the expected string.
     */
    @Test
    public void getMethodNameNormal() {
        String mn = "methodName";
        String min = "methodInterfaceName";
        List<String> pl = null;
        MethodInfo mi = new MethodInfo(mn, min, pl);
        assertEquals(mn, mi.getMethodName());
    }

    /**
     * Tests getMethodInterfaceName
     * Expected result: get the expected string.
     */
    @Test
    public void getMethodInterfaceNameNormal() {
        String mn = "methodName";
        String min = "methodInterfaceName";
        List<String> pl = null;
        MethodInfo mi = new MethodInfo(mn, min, pl);
        assertEquals(min, mi.getMethodInterfaceName());
    }

    /**
     * Tests getParamList
     * Expected result: get the null object.
     */
    @Test
    public void getParamListNull() {
        String mn = "methodName";
        String min = "methodInterfaceName";
        List<String> pl = null;
        MethodInfo mi = new MethodInfo(mn, min, pl);
        assertNull(mi.getParamList());
    }

    /**
     * Tests getParamList
     * Expected result: get the expected object.
     */
    @Test
    public void getParamListNormal() {
        String mn = "methodName";
        String min = "methodInterfaceName";
        List<String> pl = new ArrayList<String>();
        pl.add("com.ibm.class1");
        pl.add("com.ibm.class2");
        MethodInfo mi = new MethodInfo(mn, min, pl);
        assertEquals(pl, mi.getParamList());
    }

    /**
     * Tests toString
     * Expected result: get the expected result
     */
    @Test
    public void toStringNormalParamList() {
        String mn = "methodName";
        String min = "methodInterfaceName";
        List<String> pl = new ArrayList<String>();
        String i1 = "class1";
        String i2 = "class2";
        pl.add(i1);
        pl.add(i2);
        String output = "method : " + mn + " interface : " + min + " parameters : " + i1 + ", " + i2 + ", ";

        MethodInfo mi = new MethodInfo(mn, min, pl);
        assertEquals(output, mi.toString());
    }

    /**
     * Tests toString
     * Expected result: get the expected result
     */
    @Test
    public void toStringNullParamList() {
        String mn = "methodName";
        String min = "methodInterfaceName";
        List<String> pl = null;
        String output = "method : " + mn + " interface : " + min + " parameters : null";
        MethodInfo mi = new MethodInfo(mn, min, pl);
        assertEquals(output, mi.toString());
    }

}
