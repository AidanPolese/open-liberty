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
package com.ibm.ws.jaxws.utils;

import junit.framework.Assert;

import org.junit.Test;

/**
 *
 */
public class StringUtilsTest {

    @Test
    public void testIsEmpty() {
        Assert.assertTrue("Zero-length string should be empty", StringUtils.isEmpty(""));
        Assert.assertTrue("null string reference should be empty", StringUtils.isEmpty(null));
        Assert.assertTrue("The string contains only spaces should be empty", StringUtils.isEmpty(" "));
        Assert.assertTrue("The string only contains line control should be empty", StringUtils.isEmpty("\r\n"));
        Assert.assertFalse("StringUtils.isEmpty(\"a\") should return false", StringUtils.isEmpty("a"));
        Assert.assertFalse("StringUtils.isEmpty(\" a \") should return false", StringUtils.isEmpty(" a "));
    }

}
