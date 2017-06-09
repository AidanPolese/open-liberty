/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal.ejbdd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ibm.ws.javaee.dd.common.Description;
import com.ibm.ws.javaee.dd.ejb.Interceptor;
import com.ibm.ws.javaee.dd.ejb.Interceptors;

class InterceptorsImpl implements Interceptors {

    private final List<Interceptor> interceptors = new ArrayList<Interceptor>();

    InterceptorsImpl(String... interceptorClassNames) {
        for (String interceptorClassName : interceptorClassNames) {
            interceptors.add(new InterceptorImpl(interceptorClassName));
        }
    }

    @Override
    public List<Description> getDescriptions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Interceptor> getInterceptorList() {
        return Collections.unmodifiableList(interceptors);
    }
}
