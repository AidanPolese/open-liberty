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
package com.ibm.ws.logging;

import java.util.logging.Logger;

import com.ibm.ws.logging.internal.WsLogger;

public class LoggerHelper {
    /**
     * Adds the logger to the specific group. This is useful for code that needs
     * to run in environments with and without RAS. In an environment without
     * RAS, Logger.getLogger must be used, and addLoggerToGroup can be invoked
     * reflectively when available. For example:
     *
     * <pre>
     * private static Logger getLogger(Class&lt;?> c, String group) {
     * &#32;&#32;Logger logger = Logger.getLogger(c.getName());
     * &#32;&#32;try {
     * &#32;&#32;&#32;&#32;Class.forName("com.ibm.ws.logging.LoggerHelper")
     * &#32;&#32;&#32;&#32;&#32;&#32;.getMethod("addLoggerToGroup", Logger.class, String.class)
     * &#32;&#32;&#32;&#32;&#32;&#32;.invoke(logger, group);
     * &#32;&#32;} catch (Exception e) {
     * &#32;&#32;&#32;&#32;// Ignored.
     * &#32;&#32;}
     * &#32;&#32;return logger;
     * }
     * </pre>
     *
     * @param logger the logger
     * @param group the group
     * @throws NullPointerException if logger or group are null
     * @throws IllegalArgumentException if logger does not support dynamically added groups
     */
    public static void addLoggerToGroup(Logger logger, String group) {
        if (logger == null)
            throw new NullPointerException("logger");
        if (group == null)
            throw new NullPointerException("group");
        if (!(logger instanceof WsLogger))
            throw new IllegalArgumentException("logger");

        WsLogger wsLogger = (WsLogger) logger;
        wsLogger.addGroup(group);
    }
}
