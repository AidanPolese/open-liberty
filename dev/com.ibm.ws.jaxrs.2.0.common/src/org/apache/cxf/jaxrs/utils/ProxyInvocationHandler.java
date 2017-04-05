// Liberty Change for CXF Begin
/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package org.apache.cxf.jaxrs.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * This class is used to wrap message context created in service bundle
 * as the application provided message context interface.
 * 
 * @author Grant
 * 
 */
public class ProxyInvocationHandler implements InvocationHandler {

    private final Object target;

    public ProxyInvocationHandler(Object target) {
        this.target = target;
    }

    private boolean matchMethod(Method left, Method right) {
        if ((left.getName().equals(right.getName())) &&
            (left.getParameterTypes().length == right.getParameterTypes().length)) {

            Set<Class<?>> parametersLeft = new HashSet<Class<?>>();
            parametersLeft.addAll(Arrays.asList(left.getParameterTypes()));

            Set<Class<?>> parametersRight = new HashSet<Class<?>>();
            parametersRight.addAll(Arrays.asList(right.getParameterTypes()));

            return parametersLeft.equals(parametersRight);
        }
        return false;
    }

    private Method findTargetMethod(Method method) {
        Method[] methods = target.getClass().getMethods();
        for (Method currentMethod : methods) {
            if (matchMethod(currentMethod, method))
                return currentMethod;
            else
                continue;
        }
        return null;
    }

    @Override
    @Trivial
    public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {

        Method targetMethod = findTargetMethod(method);
        if (targetMethod != null) {
            return targetMethod.invoke(target, args);
        } else
            throw new IllegalArgumentException("no matched method found:" + method.toString());
    }
}
//Liberty Change for CXF End