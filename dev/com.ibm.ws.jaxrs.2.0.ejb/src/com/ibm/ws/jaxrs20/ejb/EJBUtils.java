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
package com.ibm.ws.jaxrs20.ejb;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class EJBUtils {
    public static String methodToString(Method method) {
        StringBuffer returnValue = new StringBuffer(method.getName());
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> clazz : parameterTypes) {
            returnValue.append("|").append(clazz.getName());
        }
        return returnValue.toString();

    }

    public static boolean matchMethod(Method left, Method right) {
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
}
