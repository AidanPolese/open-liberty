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

public class RMItoIDLTest
{
    @Test
    public void testGetIdlExceptionName()
    {
        Assert.assertEquals("IDL:com/example/TestErrorEx:1.0", RMItoIDL.getIdlExceptionName("com.example.TestError", false));
        Assert.assertEquals("IDL:com/example/TestErrorEx:1.0", RMItoIDL.getIdlExceptionName("com.example.TestError", true));

        Assert.assertEquals("IDL:com/example/TestEx:1.0", RMItoIDL.getIdlExceptionName("com.example.TestException", false));
        Assert.assertEquals("IDL:com/example/TestEx:1.0", RMItoIDL.getIdlExceptionName("com.example.TestException", true));

        Assert.assertEquals("IDL:com/example/exception/TestEx:1.0", RMItoIDL.getIdlExceptionName("com.example.exception.TestException", false));
        Assert.assertEquals("IDL:com/example/_exception/TestEx:1.0", RMItoIDL.getIdlExceptionName("com.example.exception.TestException", true));

        Assert.assertEquals("IDL:com/example/EXCEPTION/TestEx:1.0", RMItoIDL.getIdlExceptionName("com.example.EXCEPTION.TestException", false));
        Assert.assertEquals("IDL:com/example/_EXCEPTION/TestEx:1.0", RMItoIDL.getIdlExceptionName("com.example.EXCEPTION.TestException", true));

        Assert.assertEquals("IDL:com/example/_exception/TestEx:1.0", RMItoIDL.getIdlExceptionName("com.example._exception.TestException", false));
        Assert.assertEquals("IDL:com/example/J_exception/TestEx:1.0", RMItoIDL.getIdlExceptionName("com.example._exception.TestException", true));
    }
}
