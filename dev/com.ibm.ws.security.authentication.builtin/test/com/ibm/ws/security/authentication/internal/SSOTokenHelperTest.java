/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.authentication.internal;

import static org.junit.Assert.assertEquals;

import javax.security.auth.Subject;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

import com.ibm.wsspi.security.token.SingleSignonToken;

/**
 *
 */
public class SSOTokenHelperTest {
    private final Mockery mockery = new JUnit4Mockery();

    /**
     * Test method for {@link com.ibm.ws.security.authentication.internal.SSOTokenHelper#getSSOToken(javax.security.auth.Subject)}.
     */
    @Test
    public void getSSOToken() throws Exception {
        SingleSignonToken ssoToken = mockery.mock(SingleSignonToken.class);

        Subject subject = new Subject();
        subject.getPrivateCredentials().add(ssoToken);

        assertEquals("The SSO token must be obtained from the subject.",
                     ssoToken, SSOTokenHelper.getSSOToken(subject));
    }

}
