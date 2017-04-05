package com.ibm.tx.jta.impl;

/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/*  ORIGINS: 27                                                                      */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2010 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  Date      Programmer    Defect   Description                                     */
/*  --------  ----------    ------   -----------                                     */
/*  05/09/02   gareth       ------   Move to JTA implementation                      */
/*  18/10/02   mallam       1470     Rework to use AlarmManager mechanism            */
/*  07/11/02   hursdlg      1473     Restore in-doubt transaction code               */
/*  19/11/02   hursdlg      1503     Fix message                                     */
/*  17/12/02   mallam    LIDB1673.xx Further changes for passive timeout             */
/*  21/01/03   gareth     LIDB1673.1 Add JTA2 messages                               */
/*  29/01/03   hursdlg   LIDB1673.9.1 Fix trace                                      */
/*  30/01/03   mallam    LIDB1673.24 Inactivity timer                                */
/*  21/02/03   gareth    LIDB1673.19  Make any unextended code final                 */
/*  14/03/03  mallam    160995      New message for inactivity timeout               */
/*  06/01/04  hursdlg    LIDB2775   zOS/distributed merge                            */
/*  16/03/04  hursdlg    194662     Use merged alarm manager                         */
/*  15/06/04  johawkes   209345     Remove unnecessary code                          */
/*  06/01/06   johawkes  306998.12   Use TraceComponent.isAnyTracingEnabled()        */
/*  09/28/06   mezarin   PK32141     Tran should always schedule NonDeferrable alarms*/
/*  06/06/07  johawkes   443467     Moved                                            */
/*  29/08/07  johawkes   461798     Minor perf changes                               */
/*  21/11/07  awilkins   481738     Output stack of thread when tx times out         */
/*  02/06/09  mallam     596067     package move                                     */
/*  06/31/09  mezarin    PK90497    z/OS Terminate SR timeout type                   */
/*  23/03/10  johawkes   640174.1   Remove TimeoutInfo map                           */
/* ********************************************************************************* */

import java.io.PrintWriter;
import java.io.StringWriter;

import com.ibm.tx.TranConstants;
import com.ibm.tx.config.ConfigurationProviderManager;
import com.ibm.tx.util.alarm.Alarm;
import com.ibm.tx.util.alarm.AlarmListener;
import com.ibm.tx.util.alarm.AlarmManager;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

/**
 * This class records state for timing out transactions, and runs a thread
 * which performs occasional checks to time out transactions.
 */
public class TimeoutManager
{
    private static final TraceComponent tc = Tr.register(
                                                         TimeoutManager.class
                                                         , TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    /**
     * Constants which define the types of timeout possible.
     */
    public static final int CANCEL_TIMEOUT = 0;
    public static final int NO_TIMEOUT = 0;
    public static final int ACTIVE_TIMEOUT = 1;
    public static final int IN_DOUBT_TIMEOUT = 2;
    public static final int REPEAT_TIMEOUT = 3;
    public static final int INACTIVITY_TIMEOUT = 4;
    public static final int SR_TERMINATION_TIMEOUT = 5;

    /**
     * Sets the timeout for the transaction to the specified type and time in
     * seconds.
     * <p>
     * If the type is none, the timeout for the transaction is
     * cancelled, otherwise the current timeout for the transaction is modified
     * to be of the new type and duration.
     * 
     * @param localTID The local identifier for the transaction.
     * @param timeoutType The type of timeout to establish.
     * @param seconds The length of the timeout.
     * 
     * @return Indicates success of the operation.
     */
    public static void setTimeout(TransactionImpl tran, int timeoutType, int seconds)
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "setTimeout",
                     new Object[] { tran, timeoutType, seconds });

        switch (timeoutType)
        {
        // If the new type is active or in_doubt, then create a new TimeoutInfo
        // if
        // necessary, and set up the type and interval.
            case TimeoutManager.ACTIVE_TIMEOUT:
            case TimeoutManager.IN_DOUBT_TIMEOUT:
            case TimeoutManager.REPEAT_TIMEOUT:
                TimeoutInfo info = tran.setTimeoutInfo(new TimeoutInfo(tran, seconds, timeoutType));

                if (tc.isDebugEnabled() && info != null
                    && timeoutType != TimeoutManager.REPEAT_TIMEOUT)
                    Tr.debug(tc, "Found existing timeout for transaction: " + info);
                // not expecting this, should we cancel it?

                break;

            // For any other type, remove the timeout if there is one.
            default:
                info = tran.getTimeoutInfo();
                if (null != info)
                {
                    tran.setTimeoutInfo(null);
                    info.cancelAlarm();
                }
                else
                {
                    if (tc.isDebugEnabled())
                        Tr.debug(tc,
                                 "Failed to find existing timeout for transaction: "
                                                 + tran);
                }

                break;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "setTimeout");
    }

    /**
     * This class records information for a timeout for a transaction.
     */
    public static class TimeoutInfo implements AlarmListener
    {
        protected final TransactionImpl _tran;
        protected final int _duration;
        protected final int _timeoutType; // = TimeoutManager.NO_TIMEOUT;
        private Alarm _alarm;

        private final AlarmManager _alarmManager = ConfigurationProviderManager.getConfigurationProvider().getAlarmManager();

        protected TimeoutInfo(TransactionImpl tran, int duration, int type)
        {
            if (tc.isEntryEnabled())
                Tr.entry(tc, "TimeoutInfo", tran);

            _tran = tran;
            _duration = duration;
            _timeoutType = type;

            _alarm = _alarmManager.scheduleAlarm(_duration * 1000l, this, null);

            if (tc.isEntryEnabled())
                Tr.exit(tc, "TimeoutInfo");
        }

        /**
         * Takes appropriate action for a timeout.
         * The entry in the pendingTimeouts hashtable will be removed by
         * the transaction completion code.
         */
        @Override
        public void alarm(Object alarmContext)
        {
            if (tc.isEntryEnabled())
                Tr.entry(tc, "alarm", _tran);

            switch (_timeoutType)
            {
            // If active, then attempt to roll the transaction back.
                case TimeoutManager.ACTIVE_TIMEOUT:
                    if (tc.isEventEnabled())
                        Tr.event(tc, "Transaction timeout", _tran);
                    Tr.info(tc, "WTRN0006_TRANSACTION_HAS_TIMED_OUT", new Object[]
                    { _tran.getTranName(), new Integer(_duration) });

                    final Thread thread = _tran.getMostRecentThread();

                    if (thread != null)
                    {
                        final StackTraceElement[] stack = thread.getStackTrace();

                        final StringWriter writer = new StringWriter();
                        final PrintWriter printWriter = new PrintWriter(writer);

                        printWriter.println();

                        for (StackTraceElement element : stack)
                        {
                            printWriter.println("\t" + element);
                        }

                        Tr.info(tc, "WTRN0124_TIMED_OUT_TRANSACTION_STACK", new Object[] { thread, writer.getBuffer() });
                    }

                    _tran.timeoutTransaction(true);
                    break;

                case TimeoutManager.REPEAT_TIMEOUT:
                    if (tc.isEventEnabled())
                        Tr.event(tc, "Transaction repeat timeout", _tran);
                    _tran.timeoutTransaction(false);
                    break;

                // If in doubt, then replay_completion needs to be driven.
                // This is done by telling the TransactionImpl to act as
                // if in recovery.  
                case TimeoutManager.IN_DOUBT_TIMEOUT:
                    // Remove the pending timer entry.  Do this here as the recover code
                    // may be called without using the timer.  We need to remove it first
                    // as the recover code could restart another timer.   Active timeout does
                    // not need to remove the timer as it will eventually rollback which
                    // will remove any timer entries.
                    _tran.setTimeoutInfo(null);
                    _tran.recover();
                    break;

                default: // Otherwise do nothing.
                    break;
            }

            if (tc.isEntryEnabled())
                Tr.exit(tc, "alarm");
        }

        public void cancelAlarm()
        {
            if (tc.isEntryEnabled())
                Tr.entry(tc, "cancelAlarm", _alarm);

            if (_alarm != null)
            {
                _alarm.cancel();
                _alarm = null;
            }

            if (tc.isEntryEnabled())
                Tr.exit(tc, "cancelAlarm");
        }
    }

    protected static String getThreadId(Thread thread)
    {
        final StringBuffer buffer = new StringBuffer();

        // pad the HexString ThreadId so that it is always 8 characters long
        String tid = Long.toHexString(thread.getId());

        int length = tid.length();

        for (int i = length; i < 8; ++i)
        {
            buffer.append('0');
        }

        buffer.append(tid);

        return buffer.toString();
    }
}