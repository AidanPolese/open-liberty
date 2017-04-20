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
package com.ibm.ws.ejbcontainer.jitdeploy;

import org.junit.Assert;
import org.junit.Test;

public class JITDeployTest
{
    @Test
    public void testGetStubClassName()
    {
        Assert.assertEquals("_Intf_Stub", JIT_Stub.getStubClassName("Intf"));
        Assert.assertEquals("Cls$_Intf_Stub", JIT_Stub.getStubClassName("Cls$Intf"));
        Assert.assertEquals("pkg._Intf_Stub", JIT_Stub.getStubClassName("pkg.Intf"));
        Assert.assertEquals("pkg.Cls$_Intf_Stub", JIT_Stub.getStubClassName("pkg.Cls$Intf"));
    }
}
