/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.jitdeploy;

import org.junit.Assert;
import org.junit.Test;

public class JIT_TieTest extends AbstractJIT_TieTest {
    @Test
    public void testGetTieClassName() {
        Assert.assertEquals("_Impl_Tie", JIT_Tie.getTieClassName("Impl"));
        Assert.assertEquals("Cls$_Impl_Tie", JIT_Tie.getTieClassName("Cls$Impl"));
        Assert.assertEquals("pkg._Impl_Tie", JIT_Tie.getTieClassName("pkg.Impl"));
        Assert.assertEquals("pkg.Cls$_Impl_Tie", JIT_Tie.getTieClassName("pkg.Cls$Impl"));
    }

    @Override
    protected boolean isPortableServer() {
        return false;
    }
}
