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

import org.junit.Test;

public class RMICTieTest
                extends AbstractTieTestBase
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
    protected Class<?> defineTieClass(Class<?> targetClass, Class<?> remoteInterface, int rmicCompatible, TestClassLoader loader)
    {
        String tieClassName = JIT_Tie.getTieClassName(targetClass.getName());
        return loader.defineClass(tieClassName, getRMICBytes(targetClass, tieClassName));
    }

    @Override
    @Test
    public void testMutableIds()
    {
        super.testMutableIds();
    }

    @Override
    @Test
    public void testExceptionMangling()
    {
        super.testExceptionMangling();
    }
}
