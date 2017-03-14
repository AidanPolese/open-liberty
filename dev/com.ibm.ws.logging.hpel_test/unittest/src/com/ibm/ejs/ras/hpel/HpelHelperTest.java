/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ejs.ras.hpel;

import org.junit.Assert;
import org.junit.Test;
import org.omg.CORBA.portable.UnknownException;

public class HpelHelperTest {
    @Test
    public void testThrowableToStringUnknownException() {
        UnknownException e = new UnknownException(new Throwable("original"));
        String s = HpelHelper.throwableToString(e);
        Assert.assertTrue(s, s.contains(e.originalEx.getMessage()));
    }
}
