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
package com.ibm.ws.ejbcontainer.jitdeploy;

import org.junit.Assert;
import org.junit.Test;

public class CORBA_UtilsTest {
    @Test
    public void testGetRemoteTypeId() {
        Assert.assertEquals("RMI:java.lang.Runnable:0000000000000000", CORBA_Utils.getRemoteTypeId(Runnable.class));
        Assert.assertEquals("RMI:java.lang.Runnable:0000000000000000", CORBA_Utils.getRemoteTypeId(Runnable.class.getName()));
    }
}
