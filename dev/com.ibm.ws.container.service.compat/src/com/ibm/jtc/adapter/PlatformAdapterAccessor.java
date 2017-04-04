/* ========================================================================
 * @(#) 1.1 SERV1/ws/code/utils/src/com/ibm/jtc/adapter/PlatformAdapterAccessor.java, WAS.runtime, WAS80.SERV1, kk1041.02 5/21/10 14:32:54 [10/22/10 00:46:49]
 *
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * ========================================================================
 *
 * HISTORY
 * ~~~~~~~
 *
 * Change ID    Author    Abstract
 * ---------    --------  ---------------------------------------------------
 * D652960      andymc    New file
 * ======================================================================== */

package com.ibm.jtc.adapter;

// Alex import static com.ibm.ffdc.Manager.Ffdc;

import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;

/**
 * The PlatformAdapterAccessor class is the factory providing access to an
 * IPlatformAdapter interface. Implementations of the IPlatformAdapter
 * interface provide private API functionality in the Java runtime that is
 * not spec-compliant. <br/>
 * The default implementation is com.ibm.jtc.adapter.DefaultPlatformAdapter.
 * A different implementation may be specified in the system property,
 * com.ibm.jtc.adapterName. Normally, WAS should expect that if the default
 * implementation will not work, that the underlying JVM should specify this
 * property.
 * 
 */
public class PlatformAdapterAccessor {
    private final static String CLASSNAME = PlatformAdapterAccessor.class.getName();
    private final static TraceComponent tc = Tr.register(CLASSNAME, "Runtime", "com.ibm.ws.runtime.runtime");

    private final static String ADAPTER_NAME_PROP = "com.ibm.jtc.adapterName";
    private static IPlatformAdapter svInstance;

    /**
     * Returns the JVM-specific implementation of IPlatformAdapter. This
     * implementation
     * will be able to provide non-standard JVM private API calls in a standard
     * way by
     * using the IPlatformAdapter interface.
     * 
     * @return JVM-specific IPlatformAdapter implementation.
     */
    public static IPlatformAdapter getInstance() {
        if (svInstance != null) {
            return svInstance;
        }

        synchronized (PlatformAdapterAccessor.class) {
            if (svInstance != null) {
                // need to check again in case one thread waited on sync block while
                // another
                // initialized the instance
                return svInstance;
            }
            String adapterClassName = null;
            try {
                adapterClassName = AccessController.doPrivileged(new PrivilegedExceptionAction<String>()
                {
                    @Override
                    public String run() throws Exception
                {
                    return System.getProperty(ADAPTER_NAME_PROP);
                }
                });

                if (adapterClassName != null) {
                    // The loaded class must be an instanceof IPlatformAdapter - if not,
                    // the following
                    // line will throw a ClassCastException indicating that the specified
                    // value of the
                    // adapterName property was not a class that implemented
                    // IPlatformAdapter.
                    // This should be a very rare occurrence.
                    Class<? extends IPlatformAdapter> clazz = (Class<? extends IPlatformAdapter>) Class.forName(adapterClassName);
                    svInstance = clazz.newInstance();
                } else {
                    // Expected on Sun/Hybrid/Java 6 VMs
                    svInstance = new DefaultPlatformAdapter();
                }
            } catch (Throwable t) {
                // this means we are in a Harmony Java 7 VM, but are unable to
                // initialize
                // the Harmony platform adapter - this is a fatal error to most WAS
                // processes - must log FFDC and error message, and throw an exception
                // TODO: NLS-ize
                Tr.error(tc, "<Need new error message> - Failed to initialize {0}", new Object[] { adapterClassName });
                // Alex Ffdc.log(t, PlatformAdapterAccessor.class, CLASSNAME, "46");
                // throw new
                // IllegalStateException("Unable to initialize VM PlatformAdapter: " +
                // adapterClassName, t);
                // in this case, we need to use the default adapter -- but expect
                // erroneous behavior...
                svInstance = new DefaultPlatformAdapter();
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "chosen adapter is: " + svInstance.getClass().getName());
        }

        return svInstance;
    }

}
