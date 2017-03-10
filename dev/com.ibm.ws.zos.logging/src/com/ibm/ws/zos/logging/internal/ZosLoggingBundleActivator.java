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
package com.ibm.ws.zos.logging.internal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Dictionary;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.ibm.ws.kernel.zos.NativeMethodManager;
import com.ibm.ws.zos.jni.NativeMethodUtils;

/**
 * BundleActivator for com.ibm.ws.zos.logging bundle.
 *
 * Manages the registration / deregistration of z-specific LogHandlers,
 * based on the runtime env (e.g. STARTED TASK vs shell) and server config.
 *
 */
public class ZosLoggingBundleActivator implements BundleActivator {

    /**
     * ServiceTracker listens for NativeMethodManager and forwards registrations/deregistrations
     * to ZosLoggingBundleActivator.
     */
    private volatile NativeMethodManagerServiceTracker nativeMethodManagerServiceTracker = new NativeMethodManagerServiceTracker(this);

    /**
     * native method manager reference.
     */
    private volatile NativeMethodManager nativeMethodManager;

    /**
     * For WTO logging.
     */
    private final LoggingWtoLogHandler wtoLogHandler = new LoggingWtoLogHandler(this);

    /**
     * For MSGLOG logging.
     */
    private final MsgLogLogHandler msgLogLogHandler = new MsgLogLogHandler(this);

    /**
     * ManagedService that listens for updates to the <zosLogging> element.
     */
    private final ZosLoggingConfigListener configListener = new ZosLoggingConfigListener(this);

    /**
     * Used to determine whether WTO is required
     * for this launch context, making the config irrelevant
     */
    private volatile boolean isWTORequired = false;

    /**
     * If MSGLOG DD is defined we register the MsgLogLogHandler.
     */
    private volatile boolean isMsgLogDDDefined = false;

    /**
     * Used to determine whether WTO has been enabled in the config
     */
    private volatile boolean enableLogToMVS = false;

    /**
     * Indicates the bundle has been stopped
     */
    private volatile boolean stopped = false;

    /**
     * For registering our LogHandler service(s).
     */
    private BundleContext bContext;

    /**
     * Bundle activator: start
     */
    @Override
    public void start(BundleContext bundleContext) {
        bContext = bundleContext;

        // Register a ManagedService to be notified when the com.ibm.ws.zos.logging.config PID is updated.
        // When the PID is updated the configLIstener calls configUpdated() in this class.
        configListener.register(bundleContext);

        // ServiceTracker for NativeMethodManager.
        // When NativeMethodManager becomes available, native methods are registered and
        // the code immediately checks the native runtime env.  If we're a STARTED TASK,
        // then we register the LoggingWtoLogHandler.  If we're a STARTED TASK and the JCL
        // has the MSGLOG DD defined, then we register the MsgLogLogHandler.
        nativeMethodManagerServiceTracker.open(bundleContext);
    }

    /**
     * Bundle activator: stop
     */
    @Override
    public void stop(BundleContext ctx) {
        stopped = true;

        nativeMethodManagerServiceTracker.close();

        toggleRegistration();

        isWTORequired = false;
        enableLogToMVS = false;
        isMsgLogDDDefined = false;
    }

    /**
     * Called by ZosLoggingConfigListener when <zosLogging> is updated.
     *
     * Register/deregister LogHandlers according to the updated config.
     */
    protected void configUpdated(Dictionary config) {

        // TODO: (future) could add prop for MSGLOG DD
        enableLogToMVS = (Boolean) config.get("enableLogToMVS");

        toggleRegistration();
    }

    /**
     * Injected by the NativeMethodManagerServiceTracker.
     */
    protected void setNativeMethodManager(NativeMethodManager nativeMethodManager) {

        this.nativeMethodManager = nativeMethodManager;

        // Attempt to load native code via the method manager.
        nativeMethodManager.registerNatives(ZosLoggingBundleActivator.class);

        // go native to determine the launch context
        isWTORequired = !isLaunchContextShell();

        // determine if MSGLOG DD is defined.
        isMsgLogDDDefined = isMsgLogDDDefined();

        toggleRegistration();
    }

    /**
     * Un-Injected by the NativeMethodManagerServiceTracker.
     */
    protected void unsetNativeMethodManager(NativeMethodManager nativeMethodManager) {
        if (this.nativeMethodManager == nativeMethodManager) {
            this.nativeMethodManager = null;
        }
    }

    /**
     * Determine whether or not to enable WTOs for spawned JVMs.
     */
    private synchronized void toggleRegistration() {
        if (stopped || nativeMethodManager == null) {
            // force off
            wtoLogHandler.unregister();
            msgLogLogHandler.unregister();
            return;
        }

        if (isWTORequired) {
            // We were started by the native launcher,
            // WTO is required
            wtoLogHandler.register(bContext);
        } else {
            // We were started from USS. Use the value obtained from
            // server.xml's <zosLogging enableLogToMVS="true|false"/> property
            // in updated(). False is the default when enableLogToMVS is missing
            // or has a value other than "true" or "false" (case-insensitive).
            if (enableLogToMVS) {
                // We were switched on in the config
                wtoLogHandler.register(bContext);
            } else {
                // We were switched off in the config
                wtoLogHandler.unregister();
            }
        }

        if (isMsgLogDDDefined) {
            msgLogLogHandler.register(bContext);
        } else {
            msgLogLogHandler.unregister();
        }
    }

    /**
     * Helper method so auto entry/exit can be injected if necessary.
     *
     * @return true if server was launched from the shell; false if launched as a STARTED TASK.
     */
    private boolean isLaunchContextShell() {
        return ntv_isLaunchContextShell();
    }

    /**
     * Helper method so auto entry/exit can be injected if necessary.
     *
     * @return true if MSGLOG DD is defined; false otherwise.
     */
    private boolean isMsgLogDDDefined() {
        return ntv_isMsgLogDDDefined();
    }

    /**
     *
     * @return the native FILE *
     *
     * @throws IOException if DD:MSGLOG could not be opened.
     */
    protected long openFile(String fileName) throws IOException {
        ByteBuffer errorCodes = ByteBuffer.allocate(8);
        long retMe = ntv_openFile(NativeMethodUtils.convertToEBCDIC(fileName, false), errorCodes.array());

        if (retMe == 0) {
            throw new IOException("The server could not open file " + fileName + "."
                                  + " errno: " + errorCodes.asIntBuffer().get(0)
                                  + " errno2: " + errorCodes.asIntBuffer().get(1));
        }

        return retMe;
    }

    /**
     * Call to native code to write the message to the MSGLOG DD card
     *
     * @throws IOException on error
     */
    protected void writeFile(long filePtr, String msg) throws IOException {

        ByteBuffer errorCodes = ByteBuffer.allocate(8);
        int rc = ntv_writeFile(filePtr, NativeMethodUtils.convertToEBCDIC(msg, false), errorCodes.array());

        if (rc != 0) {
            throw new IOException("The server could not write to FILE ptr x" + Long.toHexString(filePtr) + "."
                                  + " rc: " + rc
                                  + " errno: " + errorCodes.asIntBuffer().get(0)
                                  + " errno2: " + errorCodes.asIntBuffer().get(1)
                                  + " msg: " + msg);
        }
    }

    /**
     * Call to native code to write the message to the MSGLOG DD card
     *
     * @throws IOException on error
     */
    protected void closeFile(long filePtr) throws IOException {

        ByteBuffer errorCodes = ByteBuffer.allocate(8);
        int rc = ntv_closeFile(filePtr, errorCodes.array());

        if (rc != 0) {
            throw new IOException("The server could not close FILE ptr x" + Long.toHexString(filePtr)
                                  + " rc: " + rc
                                  + " errno: " + errorCodes.asIntBuffer().get(0)
                                  + " errno2: " + errorCodes.asIntBuffer().get(1));
        }
    }

    /**
     * Call to native code to write the message to the operator console.
     *
     * @return 0 on success; non-zero on error
     */
    protected native int ntv_WriteToOperatorConsole(byte[] msg);

    /**
     * @return true if server was launched from the shell; false if launched as a STARTED TASK.
     */
    protected native boolean ntv_isLaunchContextShell();

    /**
     * @return true if MSGLOG DD is defined; false otherwise.
     */
    protected native boolean ntv_isMsgLogDDDefined();

    /**
     * Call to native code to open the given file.
     *
     * @return the FILE *, or 0 if an error occurred (errorCodes set)
     */
    protected native long ntv_openFile(byte[] fileName, byte[] errorCodes);

    /**
     * Write the given msg to the given file.
     *
     * @return 0 if all is well; non-zero for error (errorCodes set)
     */
    protected native int ntv_writeFile(long filePtr, byte[] msg, byte[] errorCodes);

    /**
     * Close the given file.
     *
     * @return 0 if all is well; non-zero for error (errorCodes set)
     */
    protected native int ntv_closeFile(long filePtr, byte[] errorCodes);
}
