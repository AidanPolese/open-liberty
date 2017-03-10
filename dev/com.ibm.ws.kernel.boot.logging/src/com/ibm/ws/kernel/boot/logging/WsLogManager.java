/*
 * COMPONENT_NAME: WAS.ras
 *
 * ORIGINS: 27         (used for IBM originated files)
 *
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 1998,2008
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 *
 * Reason        Date        User id   Description
 * ------------  ----------  -------   ----------------------------------------------
 * LIDB1241.2.1  03/25/2004  tomasz    JVM Logging Changes
 * D226682.2     09/07/2004  tomasz    Added a flag for disabling reset method
 * D373917       10/25/2006  andymc    Loading Tr in getLogger to ensure RAS is initted before client logging code
 * PK35603       12/07/2006  andymc    LOGMANAGER.GETLOGGER() CREATING UNWANTED INSTANCES OF LOGMANGER
 * 414407        01/11/2007  andymc    Adding switch to enable PK35603
 * PK49534       07/24/2007  andymc    New property to enable super.readConfiguration() in server mode
 * 496825        02/13/2008  andymc    Refactored system property logic
 * PK62221       03/05/2008  spaungam  check to make sure Tr hasn't already been loaded
 * 512212        05/21/2008  mcasile   Privilege wrap the classloader retrieval
 */
package com.ibm.ws.kernel.boot.logging;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * WebSphere replacement for Java Logging LogManager class. If system variable
 * java.util.logging.configureByServer is set, the java.util.logging.LogManager
 * intialization process will be skipped. This class always returns a logger
 * instance when its getLogger method is called. The instance is always an
 * instance of WsLogger.
 */
public class WsLogManager extends LogManager {
    private static final String CLASS_NAME = WsLogManager.class.getName();

    /**
     * Name of the system property indicating that java logging is to be
     * configured by server
     */
    private static final String CONFIGURE_BY_SERVER_PROPERTY_NAME = "java.util.logging.configureByServer";

    /**
     * Name of property indicating that the <JRE_HOME>/lib/logging.properties
     * file should be read for logging configuration.
     */
    private static final String CONFIGURE_BY_LOGGING_PROPERTIES_FILE = "java.util.logging.configureByLoggingPropertiesFile";

    private static final boolean configureByServer = "true".equalsIgnoreCase(System.getProperty(CONFIGURE_BY_SERVER_PROPERTY_NAME, "true"));
    private static final boolean configureByLoggingProperties = "true".equalsIgnoreCase(System.getProperty(CONFIGURE_BY_LOGGING_PROPERTIES_FILE));

    private static volatile Constructor<?> wsLogger;

    /**
     * Flag controlling execution of LogManager.reset() method. Default value is
     * true but in server environment, this should be set to false by calling
     * disableReset() method, to prevent losing logging during JVM shutdown.
     */
    private boolean resetEnabled = true;

    /**
     * Default constructor.
     */
    public WsLogManager() {
        super();
    }

    /**
     * Reads the configuration for Java logging. If system property
     * "java.util.logging.configureByServer" is specified, the configuration is
     * expected to be provided by server by calling
     * readConfiguration(InputStream) method.
     * 
     * @see java.util.logging.LogManager#readConfiguration()
     */
    @Override
    public void readConfiguration() throws IOException, SecurityException {
        if (!configureByServer || configureByLoggingProperties) {
            super.readConfiguration();
        } else {
            // Add a ConsoleHandler to the root logger until we're far enough
            // along to add our real handler.  This is similar to what the
            // default logging.properties would have been done by
            // super.readConfiguration().
            LoggerHandlerManager.initialize();
        }
    }

    /**
     * Returns an instance of WsLogger with specified name. If an instance with
     * specified name does not exist, it will be created.
     * 
     * @param name
     *            name of the logger to obtain
     * @return Logger instance of a logger with specified name
     * 
     */
    @Override
    public Logger getLogger(String name) {
        // get the logger from the super impl
        Logger logger = super.getLogger(name);
        // At this point we don't know which concrete class to use until the
        // ras/logging provider is initialized enough to provide a
        // wsLogger class
        if (wsLogger == null) {
            return logger;
        }

        // The Java Logging (JSR47) spec requires that the method,
        // LogManager.getLogManager().getLogger(...) returns null if the logger
        // with the passed in name does not exist. However, the method
        // Logger.getLogger(...)
        // calls this method in order to create a new Logger object. In WAS, we
        // always want the call to Logger.getLogger(...) to return an instance
        // of WsLogger. If this method returns null to it (as the spec requires),
        // then Logger.getLogger() would return an instance of java.util.logging.Logger
        // rather than our WsLogger.
        // To account for this "issue", we need to return null when a customer
        // calls this method and passes in a non-existent logger name, but must
        // create a new WsLogger when Logger.getLogger() calls this method. As
        // such, the following code will get the current thread's stack trace
        // and look for a pattern like:
        // ...
        // at com.ibm.ws.bootstrap.WsLogManager.getLogger ...
        // at java.util.logging.Logger.getLogger ...
        // ...
        // If it identifies this pattern in the stacktrace, this method will
        // create a new logger. If not, it will return null.
        if (logger == null) {
            boolean createNewLogger = false;
            boolean foundCaller = false;

            Exception ex = new Exception();
            StackTraceElement[] ste = ex.getStackTrace();
            Class<?> caller = null;

            int i = 0;

            while (!foundCaller && i < ste.length) {
                StackTraceElement s = ste[i++];

                // A) look for com.ibm.ws.bootstrap.WsLogManager.getLogger
                if (s.getClassName().equals(CLASS_NAME) && s.getMethodName().equals("getLogger")) {
                    // B) java.util.logging.Logger.getLogger
                    while (!foundCaller && i < ste.length) {
                        s = ste[i++];
                        if (s.getClassName().equals("java.util.logging.Logger") && s.getMethodName().equals("getLogger")) {
                            createNewLogger = true;
                        } else if (createNewLogger) {
                            caller = StackFinderSingleton.instance.getCaller(i, s.getClassName());
                            foundCaller = caller != null;
                        }
                    }
                }
            }

            if (createNewLogger) {
                try {
                    logger = (Logger) wsLogger.newInstance(name, caller, null);
                    // constructing the new logger will add the logger to the log manager
                    // See the constructor com.ibm.ws.logging.internal.WsLogger.WsLogger(String, Class<?>, String)
                    // This is pretty unfortunate escaping of 'this' out of the constructor, but may be risky to try and fix that.
                    // Instead add a hack here to double check that another thread did not win in creating and adding the WsLogger instance
                    Logger checkLogger = super.getLogger(name);
                    if (checkLogger != null) {
                        // Simply reassign because checkLogger is for sure the one that got added.
                        // Not really sure what it would mean if null was returned from super.getLogger, but do nothing in that case
                        logger = checkLogger;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return logger;
    }

    /*
     * @see java.util.logging.LogManager#reset()
     */
    @Override
    public void reset() throws SecurityException {
        if (this.resetEnabled) {
            super.reset();
        }
    }

    /**
     * Enables reset() method.
     */
    public void enableReset() {
        this.resetEnabled = true;
    }

    /**
     * Disables reset() method.
     */
    public void disableReset() {
        this.resetEnabled = false;
    }

    /**
     * Returns the value of internal flag controlling execution of reset method.
     * 
     * @return boolean value indicating if reset method is enabled or not
     */
    public boolean isResetEnabled() {
        return this.resetEnabled;
    }

    /**
     * Query whether the log manager is configured by the Java
     * logging.properties file or by the server.
     * 
     * @return boolean, true means logging.properties based
     */
    public static boolean isConfiguredByLoggingProperties() {
        return configureByLoggingProperties;
    }

    /**
     * Called by TrLoggerHelper#setTrLogger. The WsLogger class is part of the
     * logging jar/bundle: when the LogProvider is initialized, a callback is made
     * to provide the Logger class that should be used for constructed Logger instancs.
     * 
     * @param clazz
     */
    public static void setWsLogger(Class<?> clazz) {
        try {
            Class<?>[] classParams = { String.class, Class.class, String.class };
            wsLogger = clazz == null ? null : clazz.getConstructor(classParams);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static class StackFinderSingleton {
        static final StackFinder instance = AccessController.doPrivileged(new PrivilegedAction<StackFinder>() {
            @Override
            public StackFinder run() {
                return new StackFinder();
            }
        });
    }

    public static class StackFinder extends SecurityManager {
        @SuppressWarnings({ "unchecked" })
        public Class<?> getCaller(int i, String className) {
            Class<?> aClass = null;

            Class<?> stack[] = this.getClassContext();

            // Try the index first: the stacks should be the same.
            aClass = stack[i];
            if (aClass.getName().equals(className)) {
                return aClass;
            }

            // Walk the stack backwards to find the calling class:
            // can't use Class.forName, because we want the class as loaded
            // by it's original classloader
            for (Class<?> bClass : stack) {
                // Find the first class in the stack that _isn't_ Tr or StackFinder,
                // etc. Use the name rather than the class instance (to also work across
                // classloaders, should that happen)
                String name = bClass.getName();
                if (name.equals(className)) {
                    aClass = bClass;
                    break;
                }
            }

            return aClass;
        }
    }
}