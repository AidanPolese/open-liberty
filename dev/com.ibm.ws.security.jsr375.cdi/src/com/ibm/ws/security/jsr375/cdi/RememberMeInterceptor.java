/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.security.jsr375.cdi;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.security.enterprise.authentication.mechanism.http.RememberMe;

/**
 * TODO: Determine how it intercepts the HttpAuthenticationMechanism and calls the RememberMeIdentityStore bean provided by the application
 */
@RememberMe
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE)
public class RememberMeInterceptor {

    // TODO: Add @AroundInvoke method
}
