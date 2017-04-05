/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2008, 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine;

import java.util.HashMap;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.ejs.util.Util;

/**
 * Injection utility methods.
 */
public class InjectionUtil
{
    private static final String CLASS_NAME = InjectionUtil.class.getName();

    private final static TraceComponent tc = Tr.register(CLASS_NAME,
                                                         InjectionConfigConstants.traceString,
                                                         InjectionConfigConstants.messageFile);

    /**
     * Creates an instance of the target class, and performs all
     * dependency injection present in the specified injection
     * target map. <p>
     *
     * An InjectionException will be thrown if the specified
     * injection cannot be performed. <p>
     *
     * @param targetClass Class of the object to be created.
     * @param injectionTargetMap Map of injection targets, per class.
     *
     * @return an injected instance of the target class.
     *
     * @exception InjectionException occurs when the specified dependency
     *                injection has either not been configured properly, or
     *                for any other reason cannot be completed.
     * @exception IllegalAccessException if the class or its nullary constructor
     *                is not accessible.
     * @exception InstantiationException if this Class represents an abstract
     *                class, an interface, an array class, a primitive type,
     *                or void; or if the class has no nullary constructor; or
     *                if the instantiation fails for some other reason.
     * @exception ExceptionInInitializerError if the initialization provoked
     *                by this method fails.
     * @exception SecurityException if a security manager, s, is present and
     *                any of the following conditions is met:
     *                <ul>
     *                <li> invocation of s.checkMemberAccess(this, Member.PUBLIC)
     *                denies creation of new instances of this class.
     *                <li> the caller's class loader is not the same as or an
     *                ancestor of the class loader for the current class and
     *                invocation of s.checkPackageAccess() denies access to
     *                the package of this class.
     *                </ul>
     **/
    public static Object createInjectedInstance
                    (Class<?> targetClass,
                     HashMap<Class<?>, InjectionTarget[]> injectionTargetMap)
                                    throws InjectionException,
                                    IllegalAccessException,
                                    InstantiationException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "createInjectedInstance : " + targetClass + ", " +
                         (injectionTargetMap == null ? "null"
                                         : Util.identity(injectionTargetMap.get(targetClass))));

        InjectionTarget[] targets = null;
        Object instance = targetClass.newInstance();

        if (injectionTargetMap != null)
        {
            targets = injectionTargetMap.get(targetClass);

            if (targets != null)
            {
                InjectionEngine injectionEngine = InjectionEngineAccessor.getInstance();
                for (InjectionTarget injectionTarget : targets)
                {
                    injectionEngine.inject(instance, injectionTarget, null);
                }
            }
        }

        return instance;
    }

    // d643203
    /**
     * Creates an instance of the target class, and performs all
     * dependency injection present in the specified injection
     * target map. <p>
     *
     * An InjectionException will be thrown if the specified
     * injection cannot be performed. <p>
     *
     * @param targetClass Class of the object to be created.
     * @param injectionTargets Array of <code>InjectionTarget</code> instances,
     *            representing the injection targets visible to the
     *            specified Class.
     *
     * @return an injected instance of the target class.
     *
     * @exception InjectionException occurs when the specified dependency
     *                injection has either not been configured properly, or
     *                for any other reason cannot be completed.
     * @exception IllegalAccessException if the class or its nullary constructor
     *                is not accessible.
     * @exception InstantiationException if this Class represents an abstract
     *                class, an interface, an array class, a primitive type,
     *                or void; or if the class has no nullary constructor; or
     *                if the instantiation fails for some other reason.
     * @exception ExceptionInInitializerError if the initialization provoked
     *                by this method fails.
     * @exception SecurityException if a security manager, s, is present and
     *                any of the following conditions is met:
     *                <ul>
     *                <li> invocation of s.checkMemberAccess(this, Member.PUBLIC)
     *                denies creation of new instances of this class.
     *                <li> the caller's class loader is not the same as or an
     *                ancestor of the class loader for the current class and
     *                invocation of s.checkPackageAccess() denies access to
     *                the package of this class.
     *                </ul>
     **/
    public static Object createInjectedInstance(Class<?> targetClass,
                                                InjectionTarget[] injectionTargets)
                    throws InjectionException,
                    IllegalAccessException,
                    InstantiationException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "createInjectedInstance : " + targetClass + ", " + Util.identity(injectionTargets));

        Object instance = targetClass.newInstance();

        if (injectionTargets != null)
        {
            InjectionEngine injectionEngine = InjectionEngineAccessor.getInstance();
            for (InjectionTarget injectionTarget : injectionTargets)
            {
                injectionEngine.inject(instance, injectionTarget, null);
            }
        }

        return instance;
    }

    /**
     * D408351
     * Creates a new InjectionException with the specified message. It init's
     * the cause to the specified Throwable. This method attempts to determine
     * which InjectionException subclass to return based on the passed-in
     * Throwable.
     * <br/>
     *
     * @param t - the cause of this InjectionException
     * @param message - the message for the returned InjectionException
     * @return an instance of InjectionException or a specific subclass as determined by t
     */
    public static InjectionException checkForRecursiveException(Throwable t, String message)
    {
        InjectionException iex;
        RecursiveInjectionException.RecursionDetection rd = RecursiveInjectionException.detectRecursiveInjection(t);
        switch (rd) {
            case Recursive:
                iex = new RecursiveInjectionException(message);
                ((RecursiveInjectionException) iex).ivLogged = false;
                break;
            case RecursiveAlreadyLogged:
                iex = new RecursiveInjectionException(message);
                ((RecursiveInjectionException) iex).ivLogged = true;
                break;
            case NotRecursive:
            default:
                iex = new InjectionException(message, t);
        }
        return iex;
    }
}
