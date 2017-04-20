/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.jitdeploy;

import org.junit.Assert;
import org.junit.Test;

public class JITUtilsTest
{
    @Test
    public void testGetClassConstantFieldName()
                    throws Exception
    {
        Assert.assertEquals("class$byte", JITUtils.getClassConstantFieldName(byte.class));
        Assert.assertEquals("array$B", JITUtils.getClassConstantFieldName(byte[].class));
        Assert.assertEquals("array$$B", JITUtils.getClassConstantFieldName(byte[][].class));

        Assert.assertEquals("class$java$lang$Object", JITUtils.getClassConstantFieldName(Object.class));
        Assert.assertEquals("array$Ljava$lang$Object", JITUtils.getClassConstantFieldName(Object[].class));
        Assert.assertEquals("array$$Ljava$lang$Object", JITUtils.getClassConstantFieldName(Object[][].class));
    }
}
