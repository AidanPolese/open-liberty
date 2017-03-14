/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.tcpchannel.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.ComponentContext;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.kernel.zos.AngelUtils;

/**
 * Used to determine whether the ZOSAIO native services are registered.
 */
public class ZosAio {

    /** Trace service */
    private static final TraceComponent tc = Tr.register(ZosAio.class,
                                                         TCPChannelMessageConstants.TCP_TRACE_NAME,
                                                         TCPChannelMessageConstants.TCP_BUNDLE);

    // All resources and config are available and activated for the use of AsyncIO on z/OS.
    private static boolean isZosaioEnabled = false;
    private static final Object LOCK = new Object();

    // Property to disable AsyncIO even if all other conditions are meet (ex. racf, angel running, )
    private static boolean asyncIOEnabledProperty = true;

    public static final String AIOENABLE_PROPERTYNAME = "com.ibm.ws.tcpchannel.useZosAio";

    private AngelUtils angelUtils;

    /**
     * Recovery requirement. The AsyncIO native code has a logical dependency on the
     * HardFailureNativeCleanup service. When AsyncIO is init'd it will register a native
     * AsyncIO cleanup routine into the server_process_data structure. Our native task-level resmgr
     * will check for a "marked" Thread/TCB going through termination. If it sees such a "marked"
     * Thread it will drive the registered native cleanup routines. Seeing the "marked" Thread
     * indicates that this server has taken a Hard failure (such as a Kill -9 or a Runtime.halt(),
     * or a unrecoverable native abend). So, we need this Service dependency on HardFailureNativeCleanup
     * which is the Service that manages (starts and stops) the native "marked" Thread to trigger
     * the cleanup to prevent hung servers.
     */

    // I'm not sure that the doPrivileged is needed (ie. not sure if this code can be
    // driven under App code), but I don't want Teddy to yell at me later if it is :-)
    static {
        AccessController.doPrivileged(new PrivSysPropCheck());
    }

    static class PrivSysPropCheck implements PrivilegedAction<Object> {
        public PrivSysPropCheck() {
            // nothing to do
        }

        @Override
        public Object run() {
            // Get AIO Enabling property, default to "true".
            asyncIOEnabledProperty = Boolean.parseBoolean(System.getProperty(AIOENABLE_PROPERTYNAME, "true"));

            return null;
        }
    }

    /**
     * @return true if ZOSAIO is enabled
     */
    public static boolean isZosAioRegistered() {
        synchronized (LOCK) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event(tc, "isZosaioEnabled = " + isZosaioEnabled);
            }
            return isZosaioEnabled;
        }
    }

    /**
     * DS method for activating this component.
     *
     * @param context
     */
    protected void activate(Map<String, Object> props) {
        synchronized (LOCK) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event(this, tc, "Activating ZOSAIO");
            }

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(this, tc, "props: " + props);
            }

            if (asyncIOEnabledProperty) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                    Tr.event(this, tc, "ZOSAIO config is enabled");
                }

                // Check for authorized services
                if (this.isAsyncIOAuthorized()) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                        Tr.event(this, tc, "ZOSAIO is authorized and enabled");
                    }

                    isZosaioEnabled = true;

                    // Issue ZOSAIO activated message
                    Tr.info(tc, TCPChannelMessageConstants.ZOSAIO_ACTIVATED);
                }
            }
        }
    }

    protected boolean isAsyncIOAuthorized() {
        // Note: The following list must match the list defined in native code server_authorized_functions.def for
        //       AsyncIO services.
        Set<String> asyncIOServiceNames = new HashSet<String>(Arrays.asList("AIOINIT",
                                                                            "AIOCONN",
                                                                            "AIOIOEV2",
                                                                            "AIOCALL",
                                                                            "AIOCLEAR",
                                                                            "AIOCANCL",
                                                                            "AIOSHDWN",
                                                                            "AIOCPORT",
                                                                            "AIOGSOC"));

        // Check that each required authorized service for AsyncIO is present
        return angelUtils.areServicesAvailable(asyncIOServiceNames);
    }

    /**
     * DS method for deactivating this component.
     *
     * @param context
     */
    protected void deactivate(ComponentContext context) {
        synchronized (LOCK) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event(this, tc, "Deactivating ZOSAIO");
            }
            isZosaioEnabled = false;

            if (asyncIOEnabledProperty) {
                // Issue ZOSAIO deactivated message
                Tr.info(tc, TCPChannelMessageConstants.ZOSAIO_DEACTIVATED);
            }
        }
    }

    protected void setAngelUtils(AngelUtils angelUtils) {
        this.angelUtils = angelUtils;
    }

    protected void unsetAngelUtils(AngelUtils angelUtils) {
        this.angelUtils = null;
    }

}
