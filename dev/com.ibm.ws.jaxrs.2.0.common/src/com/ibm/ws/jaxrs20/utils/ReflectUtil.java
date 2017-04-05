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
package com.ibm.ws.jaxrs20.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;

/**
 *
 */

public class ReflectUtil {

    @FFDCIgnore(value = { ClassNotFoundException.class })
    public static Class<?> loadClass(ClassLoader cl, String className) {
        if (cl == null)
            return null;

        Class<?> c = null;
        try {
            c = cl.loadClass(className);
        } catch (ClassNotFoundException e) {

        }

        return c;
    }

    @FFDCIgnore(value = { NoSuchMethodException.class, SecurityException.class })
    public static Method getMethod(Class<?> c, String methodName, Class<?>[] paramTypes) {
        if (c == null || methodName == null) {
            return null;
        }

        Method m = null;
        try {
            m = c.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {

        } catch (SecurityException e) {

        }
        return m;
    }

    @FFDCIgnore(value = { IllegalAccessException.class, IllegalArgumentException.class, InvocationTargetException.class })
    public static Object invoke(Method m, Object instance, Object[] args) throws Throwable {

        Object res = null;
        try {
            res = m.invoke(instance, args);
        } catch (IllegalAccessException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (InvocationTargetException e) {
            //ignore
            throw e.getCause();
        }

        return res;
    }
}
