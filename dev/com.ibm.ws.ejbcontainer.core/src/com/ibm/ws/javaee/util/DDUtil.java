/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.util;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.ws.javaee.dd.ejb.NamedMethod;

/**
 * Utility methods for com.ibm.ws.javaee.dd.
 */
public class DDUtil
{
    private static final TraceComponent tc = Tr.register(DDUtil.class, "EJBContainer", "com.ibm.ejs.container.container");

    /**
     * Checks if the specified method parameters object matches the specified
     * method parameter types.
     * 
     * @param parms the method parameters object
     * @param types the method parameter types
     * @return true if the object matches the types
     */
    public static boolean methodParamsMatch(List<String> typeNames, Class<?>[] types)
    {
        if (typeNames.size() != types.length)
        {
            return false;
        }

        for (int i = 0; i < types.length; i++)
        {
            String typeName = typeNames.get(i);
            int typeNameEnd = typeName.length();

            Class<?> type = types[i];
            for (; type.isArray(); type = type.getComponentType())
            {
                if (typeNameEnd < 2 ||
                    typeName.charAt(--typeNameEnd) != ']' ||
                    typeName.charAt(--typeNameEnd) != '[')
                {
                    return false;
                }
            }

            if (!type.getName().regionMatches(0, typeName, 0, typeNameEnd))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Find the Method object for a specified NamedMethod object of a
     * Java EE 5 component (does not need to be an EJB component).
     * 
     * @param beanMethod is the WCCM NamedMethod object of the component.
     * @param allMethods is an array of all public Method objects
     *            for the component.
     * 
     * @return Method of object for the NamedMethod or null if not found.
     */
    public static Method findMethod(NamedMethod beanMethod, Method[] allMethods)
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isEntryEnabled())
            Tr.entry(tc, "findMethod");

        String methodName = beanMethod.getMethodName();
        List<String> parms = beanMethod.getMethodParamList();

        if (isTraceOn && tc.isDebugEnabled())
            Tr.debug(tc, "NamedMethod method name = " + methodName +
                         ", parameter list = " + parms);

        if (parms == null)
        {
            parms = Collections.emptyList(); // RTC100828
        }

        for (Method m : allMethods)
        {
            if (m.getName().equals(methodName) &&
                methodParamsMatch(parms, m.getParameterTypes()))
            {
                if (isTraceOn && tc.isEntryEnabled())
                    Tr.exit(tc, "returning: " + m.toGenericString());
                return m;
            }
        }

        if (isTraceOn && tc.isEntryEnabled())
            Tr.exit(tc, methodName + " not found, returning null");
        return null;
    }
}
