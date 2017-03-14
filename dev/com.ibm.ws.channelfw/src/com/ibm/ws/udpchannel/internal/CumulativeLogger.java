//-------------------------------------------------------------------------------
//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2003, 2007
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//
//Change ID     Author    Abstract
//---------     --------  -------------------------------------------------------
//-------------------------------------------------------------------------------
package com.ibm.ws.udpchannel.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public class CumulativeLogger {

    private Timer timer = null; // don't create a Timer unless one is needed.

    private static long lbLoggingThreadInterval = 10000L;
    private Map<String, LookupFailure> lookupFailureTable = new HashMap<String, LookupFailure>();
    private boolean timerStarted = false;

    /**
     * RAS Trace Component.
     */
    static final TraceComponent tc = Tr.register(CumulativeLogger.class, UDPMessages.TR_GROUP, UDPMessages.TR_MSGS);

    /**
     * Constructor.
     */
    private CumulativeLogger() {
        this.timer = new Timer();
    }

    private static CumulativeLogger loggerInstance = null;

    private static CumulativeLogger getLoggerInstance() {
        if (loggerInstance == null) {
            synchronized (CumulativeLogger.class) {
                if (null == loggerInstance) {
                    loggerInstance = new CumulativeLogger();
                }
            }
        }

        return loggerInstance;
    }

    private void startTimer() {
        if (!timerStarted) {
            if (tc.isDebugEnabled())
                Tr.debug(tc, "Starting LoggingTimer.");
            timer.schedule(new LoggingTimerTask(), 30000L, lbLoggingThreadInterval);
            timerStarted = true;
        } else {
            if (tc.isDebugEnabled())
                Tr.debug(tc, "LoggingTimer already started.");
        }
    }

    private void _logLookupFailure(String hostname) {

        synchronized (lookupFailureTable) {
            if (lookupFailureTable.size() < 5) {
                if (!timerStarted) {
                    startTimer();
                }
                LookupFailure clusterObject = lookupFailureTable.get(hostname);
                if (clusterObject == null) {
                    clusterObject = new LookupFailure(hostname);
                    lookupFailureTable.put(hostname, clusterObject);
                }
                clusterObject.incrementCount();
            }
        }
    }

    public static void logLookupFailure(String hostnameName) {
        getLoggerInstance()._logLookupFailure(hostnameName);
    }

    private static class LookupFailure {
        private String serverName = null;
        private int count = 0;

        LookupFailure(String serverName) {
            this.serverName = serverName;
        }

        public int getCount() {
            return this.count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public void incrementCount() {
            this.count++;
        }

        public String getServerName() {
            return this.serverName;
        }

    }

    class LoggingTimerTask extends TimerTask {

        LoggingTimerTask() {
            // do nothing
        }

        public synchronized void run() {

            synchronized (lookupFailureTable) {
                if (!lookupFailureTable.isEmpty()) {
                    for (Entry<String, LookupFailure> entry : lookupFailureTable.entrySet()) {
                        if (tc.isWarningEnabled())
                            Tr.warning(tc, "CWUDP0006I", ((Object) (new Object[] { entry.getValue().getServerName(), Integer.toString(entry.getValue().getCount()) })));
                    }
                    lookupFailureTable.clear();
                } else {
                    //
                    // If I come in here and don't have any lookup failures,
                    // then stop the Timer.
                    //
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "Stopping LoggingTimer.");

                    timer.cancel();
                    timerStarted = false;
                }
            }
        }

    }
}
