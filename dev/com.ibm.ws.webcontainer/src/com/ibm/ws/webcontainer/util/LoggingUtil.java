/*COPYRIGHT_START***********************************************************
 *
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *   IBM DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING
 *   ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE. IN NO EVENT SHALL IBM BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 *   CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF
 *   USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 *   OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 *   OR PERFORMANCE OF THIS SOFTWARE.
 *
 *  @(#) 1.2 SERV1/ws/code/web.session.core/src/com/ibm/ws/session/utils/LoggingUtil.java, WASCC.web.session.core, WASX.SERV1, ee1128.05 6/4/08 10:12:52 [7/15/11 20:41:56]
 *
 * @(#)file   LoggingUtil.java
 * @(#)version   1.2
 * @(#)date      6/4/08
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.webcontainer.util;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggingUtil {
    
    /**
     * A method that allows us to log a message with parameters as well as an exception
     * @param logger The java.util.logging.Logger that will log the message
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of method that issued the logging request
     * @param msg The string message (or a key in the message catalog)
     * @param params Array of parameters to the message
     * @param thrown Throwable associated with log message.
     */
    public static void logParamsAndException(Logger l, Level lev, String methodClassName, String methodName, String message, Object[] p, Throwable t) {
        LogRecord logRecord = new LogRecord(lev, message);
        logRecord.setLoggerName(l.getName());
        logRecord.setResourceBundle(l.getResourceBundle());
        logRecord.setResourceBundleName(l.getResourceBundleName());
        logRecord.setSourceClassName(methodClassName);
        logRecord.setSourceMethodName(methodName);
        logRecord.setParameters(p);
        logRecord.setThrown(t);
        l.log(logRecord);
    }
}
