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

import java.util.ArrayList;

import com.ibm.tx.TranConstants;
import com.ibm.tx.config.ConfigurationProviderManager;
import com.ibm.tx.util.alarm.Alarm;
import com.ibm.tx.util.alarm.AlarmListener;
import com.ibm.tx.util.alarm.AlarmManager;
import com.ibm.tx.util.logging.FFDCFilter;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.ws.recoverylog.spi.LibertyRecoveryDirectorImpl;
import com.ibm.ws.recoverylog.spi.RecoveryAgent;
import com.ibm.ws.recoverylog.spi.RecoveryDirector;
import com.ibm.ws.recoverylog.spi.RecoveryDirectorImpl;
import com.ibm.ws.recoverylog.spi.RecoveryFailedException;
import com.ibm.ws.recoverylog.spi.SharedServerLeaseLog;

/**
 * This class records state for timing out transactions, and runs a thread
 * which performs occasional checks to time out transactions.
 */
public class LeaseTimeoutManager
{
    private static final TraceComponent tc = Tr.register(
                                                         LeaseTimeoutManager.class
                                                         , TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    public static void setTimeout(SharedServerLeaseLog leaseLog, String recoveryIdentity, String recoveryGroup, RecoveryAgent recoveryAgent, RecoveryDirector recoveryDirector,
                                  int seconds)
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "setTimeout",
                     new Object[] { leaseLog, recoveryIdentity, recoveryAgent, seconds });

        TimeoutInfo info = new TimeoutInfo(leaseLog, recoveryIdentity, recoveryGroup, recoveryAgent, recoveryDirector, seconds);
        if (tc.isEntryEnabled())
            Tr.exit(tc, "setTimeout", info);
    }

    /**
     * This class records information for a timeout for a transaction.
     */
    private static class TimeoutInfo implements AlarmListener
    {
        protected final SharedServerLeaseLog _leaseLog;
        protected String _recoveryIdentity;
        protected String _recoveryGroup;
        protected RecoveryAgent _recoveryAgent;
        protected RecoveryDirector _recoveryDirector;
        protected final int _duration;

        private Alarm _alarm;

        private final AlarmManager _alarmManager = ConfigurationProviderManager.getConfigurationProvider().getAlarmManager();

        protected TimeoutInfo(SharedServerLeaseLog leaseLog, String recoveryIdentity, String recoveryGroup, RecoveryAgent recoveryAgent, RecoveryDirector recoveryDirector,
                              int duration)
        {
            if (tc.isEntryEnabled())
                Tr.entry(tc, "TimeoutInfo", leaseLog);

            _leaseLog = leaseLog;
            _duration = duration;
            _recoveryIdentity = recoveryIdentity;
            _recoveryGroup = recoveryGroup;

            _recoveryAgent = recoveryAgent;
            _recoveryDirector = recoveryDirector;

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
                Tr.entry(tc, "alarm", _leaseLog);

//            Tr.audit(tc, "WTRN0108I: " +
//                         "Update " + _recoveryIdentity + " lease and check the leases of other servers");
            // Update the lease when we pop
            try {
                if (_leaseLog.lockLocalLease(_recoveryIdentity))
                {
                    _leaseLog.updateServerLease(_recoveryIdentity, _recoveryGroup, false);

                    _leaseLog.releaseLocalLease(_recoveryIdentity);
                }
                else
                {
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "Could not lock lease for " + _recoveryIdentity);
                }
            } catch (Exception e) {
                //TODO:
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Swallow exception " + e);
            }

            // Check if other servers need recovering
            if (_recoveryAgent != null)
            {
                ArrayList<String> peersToRecover = _recoveryAgent.processLeasesForPeers(_recoveryIdentity, _recoveryGroup);
                if (_recoveryDirector != null && _recoveryDirector instanceof RecoveryDirectorImpl)
                {
                    try {
                        ((LibertyRecoveryDirectorImpl) _recoveryDirector).peerRecoverServers(_recoveryAgent, _recoveryIdentity, peersToRecover);
                    } catch (RecoveryFailedException e) {
                        FFDCFilter.processException(e, "com.ibm.tx.jta.impl.LeaseTimeoutManager.alarm", "146", this);
                        if (tc.isDebugEnabled())
                            Tr.debug(tc, "Swallow exception " + e);
                    }
                }
            }

            // Respawn the alarm
            _alarm = _alarmManager.scheduleAlarm(_duration * 1000l, this, null);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "alarm");
        }
//        public void cancelAlarm()
//        {
//            if (tc.isEntryEnabled())
//                Tr.entry(tc, "cancelAlarm", _alarm);
//
//            if (_alarm != null)
//            {
//                _alarm.cancel();
//                _alarm = null;
//            }
//
//            if (tc.isEntryEnabled())
//                Tr.exit(tc, "cancelAlarm");
//        }
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