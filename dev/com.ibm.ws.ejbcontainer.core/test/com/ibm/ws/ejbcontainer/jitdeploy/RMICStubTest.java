/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.jitdeploy;

import org.junit.Test;

public class RMICStubTest
                extends AbstractStubTestBase
{
    @Override
    protected boolean isRMIC() {
        return true;
    }

    @Override
    protected int[] getRMICCompatible()
    {
        return RMIC_RMIC_COMPATIBLE;
    }

    @Override
    protected byte[] getStubBytes(Class<?> remoteInterface, String stubClassName, int rmicCompatible)
    {
        return getRMICBytes(remoteInterface, stubClassName);
    }

    @Override
    @Test
    public void testMutableIds()
    {
        super.testMutableIds();
    }

    @Override
    @Test
    public void testPrimitive()
                    throws Exception
    {
        super.testPrimitive();
    }

    @Override
    @Test
    public void testClass()
                    throws Exception
    {
        super.testClass();
    }

    @Override
    @Test
    public void testInterface()
                    throws Exception
    {
        super.testInterface();
    }

    @Override
    protected boolean isExtendsCORBAObjectSupported()
    {
        // TODO: rmic generates uncompilable source for ExtendsCORBAObject.
        return false;
    }

    @Override
    @Test
    public void testExceptionMangling()
                    throws Exception
    {
        super.testExceptionMangling();
    }
}
