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
package com.ibm.websphere.security.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * Simple test to verify exception behaviour.
 */
public class CredentialDestroyedExceptionTest {

    /**
     * Test method for {@link com.ibm.websphere.security.auth.CredentialDestroyedException#CredentialDestroyedException(java.lang.String)}.
     */
    @Test
    public void constructorString() {
        assertNotNull("Constructor(String) should succeed",
                      new CredentialDestroyedException("msg"));
    }

    /**
     * Test method for {@link com.ibm.websphere.security.auth.CredentialDestroyedException#CredentialDestroyedException(java.lang.Throwable)}.
     */
    @Test
    public void constructorCause() {
        Exception cause = new Exception("Cause");
        CredentialDestroyedException ex = new CredentialDestroyedException(cause);
        assertNotNull("Constructor(Throwable) should succeed", ex);
        assertSame("Cause should be set", cause, ex.getCause());
    }

    /**
     * Test method for {@link com.ibm.websphere.security.auth.CredentialDestroyedException#CredentialDestroyedException(java.lang.String, java.lang.Throwable)}.
     */
    @Test
    public void constructorStringCause() {
        Exception cause = new Exception("Cause");
        CredentialDestroyedException ex = new CredentialDestroyedException("msg", cause);
        assertNotNull("Constructor(String,Throwable) should succeed", ex);
        assertEquals("Should see expected message", "msg", ex.getMessage());
        assertSame("Cause should be set", cause, ex.getCause());
    }

}
