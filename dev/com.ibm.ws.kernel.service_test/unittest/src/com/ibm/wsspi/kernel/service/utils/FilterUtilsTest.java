/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.kernel.service.utils;

import org.junit.Assert;
import org.junit.Test;

public class FilterUtilsTest {

    @Test
    public void testCreatePropertyFilter() {
        Assert.assertEquals("(name=value)", FilterUtils.createPropertyFilter("name", "value"));
        Assert.assertEquals("(name=\\\\value)", FilterUtils.createPropertyFilter("name", "\\value"));
        Assert.assertEquals("(name=value\\\\)", FilterUtils.createPropertyFilter("name", "value\\"));
        Assert.assertEquals("(name=v\\\\a\\*l\\(u\\)e)", FilterUtils.createPropertyFilter("name", "v\\a*l(u)e"));
    }

}
