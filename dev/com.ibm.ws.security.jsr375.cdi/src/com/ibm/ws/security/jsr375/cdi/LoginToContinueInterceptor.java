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

import java.lang.reflect.Method;

import javax.annotation.Priority;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 *
 */
@LoginToContinue
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 220)
public class LoginToContinueInterceptor {
    private static final TraceComponent tc = Tr.register(LoginToContinueInterceptor.class);
    private BeanManager beanManager = null;

    public LoginToContinueInterceptor() {
        beanManager = CDI.current().getBeanManager();
    }

    @AroundInvoke
    public Object processFormLogin(InvocationContext ic) throws Exception {
        Method method = ic.getMethod();
        if ("validateRequest".equals(method.getName())) {
        }
        Object result = ic.proceed();
        return result;
    }
}
