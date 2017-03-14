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
package com.ibm.ws.config.xml.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.ibm.websphere.config.ConfigEvaluatorException;

/**
 *
 */
public class ConfigExpressionEvaluatorTest {

    private ConfigExpressionEvaluator getEv() {
        return new ConfigExpressionEvaluator() {

            @Override
            Object getPropertyObject(String argName) throws ConfigEvaluatorException {
                if ("array1".equals(argName))
                    return new String[] { "foo" };
                if ("array2".equals(argName))
                    return new String[] { "foo", "bar" };
                if ("array3".equals(argName))
                    return new String[] { "foo", "bar", "baz" };
                if ("intArray2".equals(argName))
                    return new int[] { 1, 2 };
                if ("string".equals(argName))
                    return "str";
                throw new IllegalArgumentException("unrecognized: " + argName);
            }

            @Override
            String getProperty(String argName) throws ConfigEvaluatorException {
                return null;
            }
        };
    }

    @Test
    public void testSumOfCount2() throws Exception {
        ConfigExpressionEvaluator ev = getEv();
        assertEquals("3", ev.evaluateExpression("count(array1)+count(array2)"));
    }

    @Test
    public void testSumOfCount3() throws Exception {
        ConfigExpressionEvaluator ev = getEv();
        assertEquals("6", ev.evaluateExpression("count(array1)+count(array2)+count(array3)"));
    }

    @Test
    public void testfilterSyntaxError() throws Exception {
        ConfigExpressionEvaluator ev = getEv();
        assertNull(ev.evaluateExpression("servicePidOrFilter(intArray2)"));
    }

    @Test
    public void testTrailingSpae() throws Exception {
        ConfigExpressionEvaluator ev = getEv();
        assertNull(ev.evaluateExpression("servicePidOrFilter(string) "));
    }

}
