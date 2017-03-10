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
package com.ibm.ws.kernel.boot.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class LoggerHandlerManager {
    private static final Logger rootLogger = Logger.getLogger("");
    private static Handler console;
    private static Handler singleton;

    /**
     * Called by LogManager.readConfiguration instead of reading
     * logging.properties.
     */
    public static synchronized void initialize() {
        if (console == null) {
            console = new ConsoleHandler();
            updateSingleton(console);
        }
    }

    private static synchronized void updateSingleton(Handler newHandler) {
        Handler oldHandler = singleton;
        if (newHandler != oldHandler) {
            // There is no way to atomically replace a handler on a logger.  We add
            // the new handler before removing the old handler so that the worst
            // case is a duplicate message rather than a lost message.

            if (newHandler != null) {
                rootLogger.addHandler(newHandler);
            }

            singleton = newHandler;

            if (oldHandler != null) {
                rootLogger.removeHandler(oldHandler);
            }
        }
    }

    public static synchronized Handler getSingleton() {
        return singleton != console ? singleton : null;
    }

    public static synchronized void setSingleton(Handler handler) {
        if (handler == null) {
            throw new IllegalArgumentException();
        }
        updateSingleton(handler);
    }

    public static synchronized void unsetSingleton() {
        updateSingleton(console);
    }
}