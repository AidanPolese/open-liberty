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
package com.ibm.ejs.container;

import java.security.AccessController;

import com.ibm.ejs.util.Util;
import com.ibm.ejs.util.dopriv.GetContextClassLoaderPrivileged;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.util.dopriv.GetClassLoaderPrivileged;

/**
 * Runtime support for debug code inserted into JIT-deployed classes.
 */
public class JIT_Debug
{
    private static final TraceComponent tc = Tr.register(JIT_Debug.class,
                                                         "JITDeployRuntime",
                                                         "com.ibm.ejs.container.container");

    /**
     * Called by JIT-deployed code that is performing a checkcast. For the
     * convenience of generated code, the input value is returned directly.
     *
     * @param value the value being cast
     * @param object the object performing the cast
     * @param valueClassName the expected class name
     * @return value
     */
    public static Object checkCast(Object value, Object object, String valueClassName)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            ClassLoader valueLoader = value == null ? null :
                            AccessController.doPrivileged(new GetClassLoaderPrivileged(value.getClass()));
            ClassLoader contextLoader =
                            AccessController.doPrivileged(new GetContextClassLoaderPrivileged());
            ClassLoader objectLoader =
                            AccessController.doPrivileged(new GetClassLoaderPrivileged(object.getClass()));

            ClassLoader objectValueLoader;
            try
            {
                Class<?> objectValueClass = Class.forName(valueClassName, false, objectLoader);
                objectValueLoader = objectValueClass == null ? null :
                                AccessController.doPrivileged(new GetClassLoaderPrivileged(objectValueClass));
            } catch (Throwable t)
            {
                Tr.debug(tc, "checkCast: failed to load " + valueClassName, t);
                objectValueLoader = null;
            }

            Tr.debug(tc, "checkCast: value=" + Util.identity(value) +
                         ", valueClassName=" + valueClassName,
                     ", valueLoader=" + Util.identity(valueLoader) +
                                     ", contextLoader=" + Util.identity(contextLoader) +
                                     ", object=" + Util.identity(object) +
                                     ", objectLoader=" + Util.identity(objectLoader) +
                                     ", objectValueLoader=" + Util.identity(objectValueLoader));
        }

        return value;
    }
}
