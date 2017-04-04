package com.ibm.tx.jta.impl;

/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/*  ORIGINS: 27                                                                                          */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business Machines Corp. 2002, 2016 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                                         */
/*                                                                                                       */
/*  DESCRIPTION:                                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date      Programmer    Defect   Description                                                         */
/*  --------  ----------    ------   -----------                                                         */
/*  10/09/02   gareth       ------   Move to JTA implementation                                          */
/*  23/09/02   hursdlg      ------   Partner log support                                                 */
/*  01/10/02   hursdlg      ------   Reorder recovery to report failures                                 */
/*  03/10/02   hursdlg      ------   Support async recovery completion                                   */
/*  09/10/02   hursdlg      1453     Tidy up some recovery operations                                    */
/*  10/10/02   hursdlg      1426     Update to latest recovery.log spec                                  */
/*  23/10/02   hursdlg      1433     Make some methods public                                            */
/*  01/11/02   hursdlg      1476     Log the epoch value                                                 */
/*  04/11/02   hursdlg      1479     Recover resources before posting event                              */
/*  06/11/02   gareth       1449     Tidy up messages and exceptions                                     */
/*  06/11/02   hursdlg      1473     Zap service data on clean shutdown                                  */
/*  18/11/02   beavenj      1502     Handle new recovery.log exceptios                                   */
/*  19/11/02   hursdlg      1487     Remove log record during iteration                                  */
/*  21/11/02   awilkins     1507     JTS -> JTA. Thread local restructuring                              */
/*  04/12/02   hursdlg      1518     Clean up use of toHexString                                         */
/*  13-12-02   awilkins  LIDB1673.17 Embedded RAR recovery                                               */
/*  16-12-02   hursdlg   LIDB1673.2  Remove redundant code                                               */
/*  20-12-02   hursdlg   LIDB1673.2  Initialize WSCoordinator                                            */
/*  17/01/03   awilkins      1673.9  Pass WSCoordinator to interceptor                                   */
/*  20/01/03   gareth     LIDB1673.1 Add JTA2 messages                                                   */
/*  28/01/03   hursdlg    LIDB1673.9.1  Update Partner log support                                       */
/*  21/02/03   gareth     LIDB1673.19  Make any unextended code final                                    */
/*  28/02/03   hursdlg    LIDB1673.19.1 Clean up Config support                                          */
/*  02/04/03   hursdlg      162470      LogCursor.close() changes                                        */
/*  03/04/03   hursdlg    LIDB1673.22  Catch throwable on LogCursor                                      */
/*  11/06/03   hursdlg      169107     Update recoverylog interfaces                                     */
/*  15/07/03   mallam       171151    Rollback using TMSUCCESS (TMFAIL on t/o)                           */
/*  18/07/03   johawkes   LIDB2110.12  JCA1.5                                                            */
/*  25/07/03   hursdlg      172471    Terminate partner log before shutdown                              */
/*  31/07/03   hursdlg      173100    Use lastData on RecoverableUnitSection                             */
/*  15/08/03   hursdlg      174113    Reorganize for recovery retry                                      */
/*  28/08/03   johawkes     173214    Replace RegisteredResource vectors                                 */
/*  02/09/03   hursdlg      175486    Improve classpath trace                                            */
/*  18/09/03   hursdlg      177194    8 byte recovery ids/service data changes                           */
/*  19/09/03   hursdlg      177276    Force tranlog during shutdown                                      */
/*  22/09/03   hursdlg      174209    Reorder classpath logging                                          */
/*  25/09/03   johawkes     177208    Check for null XARecoveryData                                      */
/*  01/10/03   johawkes     178208.1  Use log generated recovery ids                                     */
/*  08/10/03   johawkes     178425    Change startup order for RA recovery                               */
/*  20/11/03   johawkes     182862    Remove static partner log dependencies                             */
/*  23/11/03   hursdlg      182220    Ensure WSCoordinator ready for recovery                            */
/*  24/11/03   hursdlg      183723    Dont update classpath if not logging                               */
/*  27/11/03   johawkes     178502    Start an RA during XA recovery                                     */
/*  02/12/03   johawkes     184169    Remove static servant mgr dependencies                             */
/*  05/12/03   johawkes     184903    Refactor PartnerLogTable                                           */
/*  06/01/04   hursdlg      LIDB2775  zOS/distributed merge                                              */
/*  04/02/04   johawkes     189497    Add imported txns to list for recovery                             */
/*  01/03/04   hursdlg      192671    Make recoverableUnit non-zero                                      */
/*  23/03/04   johawkes     195344    Ensure importing RAs stay logged                                   */
/*  24/03/04   mallam       LIDB2775  ws390 code drop                                                    */
/*  25/03/04   johawkes     195344.1  Fix number of partner log service items                            */
/*  26/03/04   hursdlg      196258    Remove XARecoveryManager/check cp in log                           */
/*  13/04/04   beavenj      LIDB1578.1 Initial supprort for ha-recovery                                  */
/*  22/04/04   awilkins     198904.1  getXid changes                                                     */
/*  15/04/04   beavenj      LIDB1578.3 Termination support for ha-recovery                               */
/*  22/04/04   hursdlg      199500     Init _classpath prior to recovery                                 */
/*  08/05/04   hursdlg      202183     Fix zOS recovery manager log checks                               */
/*  10/05/04   hursdlg      LIDB2775-39 More z/OS fixes                                                  */
/*  27/04/04   beavenj      LIDB1578.5 Connect up with HA framework                                      */
/*  19/05/04   beavenj      LIDB1578.6 Connect up with WLM cluster framework                             */
/*  21/05/04   beavenj      LIDB1578.7 FFDC                                                              */
/*  10/06/04   johawkes     207717     Don't get classpath too soon                                      */
/*  21/06/04   johawkes     210818     Don't wait for classloader on zOS                                 */
/*  21/06/04   johawkes     199785     Fix partner log corruption on shutdown                            */
/*  01/07/04   hursdlg      210818.1   Fix classpath for zOS                                             */
/*  06/07/04   johawkes     213406     Replaced TranManagerSet.isActive()                                */
/*  08-07-04   dmatthew     210276.1   WSAddressing HA changes                                           */
/*  10-08-04   beavenj      221931     Fix cleanup logic for recovery logs                               */
/*  26-08-04   beavenj      227136     Prevent potential log collision                                   */
/*  26-08-04   dmatthew     227192     Fix call to registerWSATServices                                  */
/*  28/09/04   beavenj      227911     Clean shutdown after openLog failure                              */
/*  29/09/04   beavenj      235642     Modify "lock not granted" behaviour                               */
/*  30/09/04   beavenj      227515     Messages                                                          */
/*  12-10-04   awilkins     227752.4   Notify runtime of failure in asynch startup                       */
/*  10/11/04   beavenj      243535.1   Fix cleanup logic for partners                                    */
/*  02/02/05   hursdlg      252732     Update WTRN0100 msgs                                              */
/*  09/02/05   hursdlg      240834     Implement Runnable                                                */
/*  17/01/05   mallam       LIDB3645   Recovery-mode restart                                             */
/*  03/03/05   hursdlg      254326     Fix another recovery shutdown hang                                */
/*  09/03/04   hursdlg      260096     Wrapper partnerlog initialization with Enq                        */
/*  14/06/05   hursdlg      283253     Componentization changes for recovery                             */
/*  01/07/05   kaczyns      PK08452    Create recovery classloader on z/OS                               */
/*  11/09/05   mezarin      LIDB3187   ots to java support                                               */
/*  30/10/05   johawkes     LIDB3462-37.TX WSAddressing HA changes                                       */
/*  08/12/05   johawkes     329403     Record when apps are started                                      */
/*  06/01/06   johawkes     306998.12  Use TraceComponent.isAnyTracingEnabled()                          */
/*  12/01/06   johawkes     337726     Synchronize CLASSPATH logging                                     */
/*  14/03/06   hursdlg      354931     Always output WTRN0028I message                                   */
/*  25/04/06   hursdlg      360413     Recovery retry changes                                            */
/*  12/10/06   pault1       PK31789    Transaction logs get in a bad state after errors when using       */
/*                                     failed over file system                                           */
/*  06/10/24   awilkins     397006     Synchronize checkClassPath                                        */
/*  06/11/01   awilkins     377289     Server name read from the log doesn't cope with double-byte chars */
/*  06/11/29   maples       402670     LI4119-19 code review changes                                     */
/*  07/01/05   dmatthew     PK37117    Fix HA                                                            */
/*  07/03/28   awilkins     422139     Fix errors during shutdown when using in-memory logging           */
// 07/04/12 johawkes LIDB4171-35    Componentization
// 07/04/12 johawkes 430278         Further componentization
/*  07-04-16   dmatthew     412459     call Object.wait from within a loop                               */
/*  07-05-01   johawkes     412459     Remove WAS dependencies                                           */
/*  07-05-16   johawkes     438575     Further componentization                                          */
/*  07-05-31   johawkes     441229     Fix CCE                                                           */
/*  07-06-06   johawkes     443467     Moved                                                             */
/*  07-06-07   johawkes     438695.3   Removed call to recoveryComplete                                  */
/*  07-06-12   hursdlg      445361     Close log iterators when exception thrown in replayPartnerLog     */
/*  07-06-27   hursdlg      448506     Close logs if a replay error                                      */
/*  07-06-30   hursdlg      LI3968-1.2 Update partner log for resource priority                          */
/*  07-06-30   hursdlg      446894.1   Signal JTM when resync finishes                                   */
/*  07-07-13   hursdlg      451491     Check priority field size in replayPartnerLog                     */
/*  07-08-06   johawkes     451213.1   Transaction Inflow                                                */
/*  07-08-21   johawkes     461056     Introduce handleXAResourceRecord to be overridden                 */
/*  07-08-24   johawkes     461798     Reconstruct correct classes in recovery                           */
/*  07-09-25   johawkes     469700     Handle JCAProvider sections turning up before plt is set up       */
/*  07-10-04   hursdlg      471680.1   WAS specific FailureScopeLifeCycleHelper                          */
/*  07-10-17   johawkes     468105     Report resync exceptions                                          */
/*  08-05-22   hursdlg      523343     Check partner log records before HA termination                   */
/*  08-07-17   johawkes     536926     Remove JET dependencies on org.omg classes                        */
/*  09-06-02   mallam       596067     package move                                                      */
/*  10-03-17   hursdlg      PM07874    Recovery audit logging                                            */
/*  10-06-09   hursdlg      656080     Reduce HA failback cleanup                                        */
/*  10-09-22   hursdlg      671043     NPE after 656080 when failing over empty logs                     */
/*  11-04-02   johawkes     732062     Reduce "recovering no transactions message" to info from audit    */
/*  13-12-12   johawkes     105657     Handle unwritable recovery logs                                   */
/*  14-05-07   dmatthew     PI16613    NPE in preShutdown, _tranlogEpochSection is null (serviceability) */
/*  15-12-02   dmatthew     PI45254    Enhance serviceability on MSRL.markFailed                         */
/* ***************************************************************************************************** */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.transaction.SystemException;
import javax.transaction.xa.Xid;

import com.ibm.tx.TranConstants;
import com.ibm.tx.config.ConfigurationProviderManager;
import com.ibm.tx.jta.TransactionManagerFactory;
import com.ibm.tx.jta.util.TxTMHelper;
import com.ibm.tx.util.TMHelper;
import com.ibm.tx.util.Utils;
import com.ibm.tx.util.logging.FFDCFilter;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.ws.Transaction.JTA.FailureScopeLifeCycle;
import com.ibm.ws.Transaction.JTA.FailureScopeLifeCycleHelper;
import com.ibm.ws.Transaction.JTA.JTAResource;
import com.ibm.ws.Transaction.JTA.Util;
import com.ibm.ws.Transaction.JTS.Configuration;
import com.ibm.ws.recoverylog.spi.DistributedRecoveryLog;
import com.ibm.ws.recoverylog.spi.FailureScope;
import com.ibm.ws.recoverylog.spi.InternalLogException;
import com.ibm.ws.recoverylog.spi.LogAllocationException;
import com.ibm.ws.recoverylog.spi.LogCursor;
import com.ibm.ws.recoverylog.spi.LogIncompatibleException;
import com.ibm.ws.recoverylog.spi.NotSupportedException;
import com.ibm.ws.recoverylog.spi.RecoverableUnit;
import com.ibm.ws.recoverylog.spi.RecoverableUnitSection;
import com.ibm.ws.recoverylog.spi.RecoveryAgent;
import com.ibm.ws.recoverylog.spi.RecoveryDirectorFactory;
import com.ibm.ws.recoverylog.spi.RecoveryLog;
import com.ibm.ws.recoverylog.spi.SharedServerLeaseLog;

/**
 * This class manages information required for recovery, and also general
 * state regarding transactions in a process.
 */
public class RecoveryManager implements Runnable {
    private static final TraceComponent tc = Tr.register(RecoveryManager.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    /**
     * LIDB3645: Flag set by TxServiceImpl during start up.
     * Indicates that we must notify Server runtime when all recovered
     * txns have completed.
     */
    public static boolean recoveryOnlyMode;

    public static boolean _waitForRecovery;

    private boolean _recoveryFailed;

    /**
     * This attribute is used to block requests against RecoveryCoordinators or
     * CoordinatorResources before recovery has completed.
     */
    protected final EventSemaphore _replayInProgress = new EventSemaphore();
    protected boolean _replayCompleted;

    protected final EventSemaphore _recoveryInProgress = new EventSemaphore();
    protected boolean _recoveryCompleted;

    protected boolean _shutdownInProgress;

    protected final RecoveryAgent _agent;
    protected RecoveryLog _tranLog; // 169107
    protected RecoveryLog _xaLog; // 169107
    protected final RecoveryLog _recoverXaLog; //@MD18134A

    protected SharedServerLeaseLog _leaseLog;
    protected String _recoveryGroup;
    protected String _localRecoveryIdentity;

    protected PartnerLogTable _recoveryPartnerLogTable;

    protected byte[] _ourApplId;
    protected int _ourEpoch;

    protected byte[] _recoveredApplId;
    protected int _recoveredEpoch;
    protected String _recoveredServerName;

    protected int _partnerEntryLowWatermark = -1; /* @MD18134A */
    protected int _partnerEntryNextId = -1; /* @MD18134A */

    protected static final int TRANSACTION_SERVICE_ITEMS = 3;
    protected static final int PARTNERLOG_SERVICE_ITEMS = 6;

    //
    // The following relate to the service data recoverable unit in the partner log
    // This is reserved and holds the server state, classpath, servername, applid and epoch.
    //
    protected RecoverableUnit _partnerServiceData;
    protected RecoverableUnitSection _stateSection;
    protected RecoverableUnitSection _classpathSection;
    protected RecoverableUnitSection _partnerServerSection;
    protected RecoverableUnitSection _partnerApplIdSection;
    protected RecoverableUnitSection _partnerEpochSection;
    protected RecoverableUnitSection _partnerLowWatermarkSection; /* @MD18134A */
    protected RecoverableUnitSection _partnerNextIdSection; /* @MD18134A */

    //
    // The following relate to the service data recoverable unit in the transaction log
    // This is reserved and holds the servername, applid and epoch.
    //
    protected RecoverableUnit _tranlogServiceData;
    protected RecoverableUnitSection _tranlogServerSection;
    protected RecoverableUnitSection _tranlogApplIdSection;
    protected RecoverableUnitSection _tranlogEpochSection;

    protected String _classPath; // current classpath for recovery
    // These are static as they are only initialized from the "server's" own log
    // We use our own classpaths and not from other servers in case we recover
    // for a filesystem that does not match our own.  
    protected static String _loggedClassPath; // classpath read from the log at startup

    // Server States logged for serviceability
    public static final int STARTING = 1;
    public static final int STOPPING = 3;

    protected FailureScopeController _failureScopeController;

    // Flag to indcate if recovery processing was prevented from occuring. This
    // can occur when the recovery logs are found to be incompatible or if
    // the recovery log file has been marked as not supporting ha enablement
    // either premanently or at this time.
    private boolean _recoveryPrevented;

    /**
     * This set contains a list of all recovering transactions.
     */
    protected HashSet<TransactionImpl> _recoveringTransactions;

    protected final Object _recoveryMonitor = new Object();

    protected boolean _cleanRemoteShutdown;

    public RecoveryManager(FailureScopeController fsc, RecoveryAgent agent, RecoveryLog tranLog, RecoveryLog xaLog, RecoveryLog recoverXaLog, byte[] defaultApplId, int defaultEpoch)/*
                                                                                                                                                                                      * throws
                                                                                                                                                                                      * Exception
                                                                                                                                                                                      */
    {
        if (tc.isEntryEnabled()) {
            Tr.entry(tc, "RecoveryManager", new Object[] { fsc, agent, tranLog, xaLog, recoverXaLog,
                                                          (defaultApplId == null ? "null" : Util.toHexString(defaultApplId)),
                                                          defaultEpoch });
        }

        // @240834D

        _failureScopeController = fsc;
        _agent = agent;
        _tranLog = tranLog;
        _xaLog = xaLog;
        _recoverXaLog = recoverXaLog; // @MD18134A

        _ourApplId = defaultApplId;
        _ourEpoch = defaultEpoch;

        _recoveringTransactions = new HashSet<TransactionImpl>();

        if (tc.isEntryEnabled())
            Tr.exit(tc, "RecoveryManager", this);
    }

    public PartnerLogTable getPartnerLogTable() {
        if (_recoveryPartnerLogTable == null) {
            _recoveryPartnerLogTable = new PartnerLogTable(_failureScopeController);
        }

        return _recoveryPartnerLogTable;
    }

    byte[] getApplId() {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "getApplId", _ourApplId);
        return _ourApplId;
    }

    int getCurrentEpoch() {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "getCurrentEpoch", _ourEpoch);
        return _ourEpoch;
    }

    public FailureScopeController getFailureScopeController() {
        return _failureScopeController;
    }

    /**
     * Requests that the Transaction log is replayed ready for recovery.
     * <p>
     * The log is read and a list of TransactionImpls is reconstructed that
     * corresponds to those transactions that were in-doubt at the time of the
     * previous failure. Any service data is assembled and merged with that
     * from the Partner log.
     * <p>
     * 
     */
    protected void replayTranLog() throws Exception {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "replayTranLog");

        final LogCursor recoverableUnits = _tranLog.recoverableUnits();
        LogCursor recoverableUnitSections = null;
        int recoveredServiceItems = 0;
        boolean shuttingDown = false;
        boolean recoveredTransactions = false;

        try {
            // The recovered transctions may include completed transactions.
            // These will be filtered out by the TransactionImpl.reconstruct call
            // We will force a checkpoint after this in case we previously crashed.
            while (recoverableUnits.hasNext() && !shuttingDown) {
                final RecoverableUnit ru = (RecoverableUnit) recoverableUnits.next();
                if (tc.isEventEnabled())
                    Tr.event(tc, "Replaying record " + ru.identity() + " from the transaction log");

                recoverableUnitSections = ru.sections();
                boolean tranRecord = false;
                while (!tranRecord && recoverableUnitSections.hasNext()) {
                    final RecoverableUnitSection rus = (RecoverableUnitSection) recoverableUnitSections.next();
                    final int rusId = rus.identity();
                    final byte[] logData = rus.lastData(); // Only ever have 1 data item per service data sections
                    if (tc.isEventEnabled())
                        Tr.event(tc, "Replaying section " + rusId, Util.toHexString(logData));
                    switch (rusId) {
                        case TransactionImpl.SERVER_DATA_SECTION:
                            if (tc.isDebugEnabled())
                                Tr.debug(tc, "Server name data record");
                            if (_tranlogServerSection != null) {
                                if (tc.isEventEnabled())
                                    Tr.event(tc, "Multiple log SERVER_DATA_SECTIONs found");
                                Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _tranLog.logProperties().logName());
                                throw new IOException(_tranLog.logProperties().logName() + " corrupted");
                            }
                            if (_tranlogServiceData == null) {
                                _tranlogServiceData = ru;
                            } else if (_tranlogServiceData != ru) {
                                if (tc.isEventEnabled())
                                    Tr.event(tc, "Multiple log service data records found");
                                Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _tranLog.logProperties().logName());
                                throw new IOException(_tranLog.logProperties().logName() + " corrupted");
                            }
                            _tranlogServerSection = rus;
                            recoveredServiceItems++;
                            if (logData != null && logData.length != 0) {
                                final String recoveredServerName = new String(logData);
                                if (_recoveredServerName == null) {
                                    _recoveredServerName = recoveredServerName;
                                    //
                                    // Check to see if the logged serverName is the same as this server
                                    // If it is different, we just output a message as we may be replaying another server log
                                    // on this server.  We leave the service data set to the original server name in case we
                                    // crash before recovery has completed.  We need to ensure we have a matching server name
                                    // with that from the partner log.  On shutdown, if there are no transactions running, we
                                    // clear out the transaction log, but leave the partner log and may update the server name
                                    // to this server in the partner log.  If there are transactions running, we never update
                                    // the server name in the log.
                                    //
                                    if (!_failureScopeController.serverName().equals(_recoveredServerName)) {
                                        Tr.warning(tc, "WTRN0020_RECOVERING_TRANSACTIONS", _recoveredServerName);
                                    }
                                } else if (!_recoveredServerName.equals(recoveredServerName)) {
                                    Tr.error(tc, "WTRN0024_INCONSISTENT_LOGS");
                                    throw new IOException("Inconsistent Transaction and XA Resource recovery logs");
                                }
                            }
                            break;
                        case TransactionImpl.APPLID_DATA_SECTION:
                            if (tc.isDebugEnabled())
                                Tr.debug(tc, "Applid data record");
                            if (_tranlogApplIdSection != null) {
                                if (tc.isEventEnabled())
                                    Tr.event(tc, "Multiple log APPLID_DATA_SECTIONs found");
                                Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _tranLog.logProperties().logName());
                                throw new IOException(_tranLog.logProperties().logName() + " corrupted");
                            }
                            if (_tranlogServiceData == null) {
                                _tranlogServiceData = ru;
                            } else if (_tranlogServiceData != ru) {
                                if (tc.isEventEnabled())
                                    Tr.event(tc, "Multiple log service data records found");
                                Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _tranLog.logProperties().logName());
                                throw new IOException(_tranLog.logProperties().logName() + " corrupted");
                            }
                            _tranlogApplIdSection = rus;
                            recoveredServiceItems++;
                            if (logData != null) {
                                if (_recoveredApplId == null) {
                                    _recoveredApplId = logData;
                                } else if (!Util.equal(_recoveredApplId, logData)) {
                                    Tr.error(tc, "WTRN0024_INCONSISTENT_LOGS");
                                    throw new IOException("Inconsistent Transaction and XA Resource recovery logs");
                                }
                            }
                            break;
                        case TransactionImpl.EPOCH_DATA_SECTION:
                            if (tc.isDebugEnabled())
                                Tr.debug(tc, "Epoch data record");
                            if (_tranlogEpochSection != null) {
                                if (tc.isEventEnabled())
                                    Tr.event(tc, "Multiple log EPOCH_DATA_SECTIONs found");
                                Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _tranLog.logProperties().logName());
                                throw new IOException(_tranLog.logProperties().logName() + " corrupted");
                            }
                            if (_tranlogServiceData == null) {
                                _tranlogServiceData = ru;
                            } else if (_tranlogServiceData != ru) {
                                if (tc.isEventEnabled())
                                    Tr.event(tc, "Multiple log service data records found");
                                Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _tranLog.logProperties().logName());
                                throw new IOException(_tranLog.logProperties().logName() + " corrupted");
                            }
                            _tranlogEpochSection = rus;
                            recoveredServiceItems++;
                            if (logData != null && logData.length > 3) {
                                final int recoveredEpoch = Util.getIntFromBytes(logData, 0, 4);
                                if (tc.isDebugEnabled())
                                    Tr.debug(tc, "Recovered epoch: " + recoveredEpoch);
                                // If epoch isnt set or tranlog epoch is larger than partner, take the larger.
                                // These can get out of step if one crashes during a restart.
                                if (recoveredEpoch > _recoveredEpoch) {
                                    _recoveredEpoch = recoveredEpoch;
                                }
                            }
                            break;
                        default:
                            tranRecord = true;
                            break;
                    }
                }
                recoverableUnitSections.close();
                recoverableUnitSections = null; // reset in case of throw/catch

                if (tranRecord) {
                    recoveredTransactions = handleTranRecord(ru, recoveredTransactions, recoverableUnits);
                }

                // Check to see if shutdown has begun before proceeding. If it has, no
                // further action can be taken.
                shuttingDown = shutdownInProgress();
            }

            // Only bother to check that the retrieved information is valid if we are not
            // trying to stop processing the partner log.
            if (!shuttingDown) {
                if (recoveredTransactions) {
                    // We have at least recovered a transaction
                    // Check we have all of the service data
                    if (recoveredServiceItems != TRANSACTION_SERVICE_ITEMS) {
                        if (tc.isEventEnabled())
                            Tr.event(tc, "Recoverable log records found without service data records");
                        Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _tranLog.logProperties().logName());
                        throw new IOException(_tranLog.logProperties().logName() + " corrupted");
                    }
                } else if (recoveredServiceItems != 0 && recoveredServiceItems != TRANSACTION_SERVICE_ITEMS) {
                    if (tc.isEventEnabled())
                        Tr.event(tc, "Only a subset of service data records recovered");
                    Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _tranLog.logProperties().logName());
                    throw new IOException(_tranLog.logProperties().logName() + " corrupted");
                }
            }
        } catch (Throwable exc) {
            FFDCFilter.processException(exc, "com.ibm.tx.jta.impl.RecoveryManager.replayTranLog", "512", this);
            if (recoverableUnitSections != null)
                recoverableUnitSections.close();
            Tr.error(tc, "WTRN0025_TRAN_RECOVERY_FAILED", exc);
            SystemException se = new SystemException(exc.toString());
            if (tc.isEntryEnabled())
                Tr.exit(tc, "replayTranLog", se);
            throw se;
        } finally {
            recoverableUnits.close();
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "replayTranLog");
    }

    protected boolean handleTranRecord(RecoverableUnit ru, boolean recoveredTransactions, LogCursor recoverableUnits) throws SystemException, NotSupportedException, InternalLogException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "handleTranRecord", new Object[] { ru, recoveredTransactions, recoverableUnits, new Exception("handleTranRecord Stack") });

        final TransactionImpl tx = new TransactionImpl(_failureScopeController);
        if (tx.reconstruct(ru, _tranLog)) {
            // If this txn was imported from an RA we need to re insert it in
            // TxExecutionHandler.txnTable
            if (tx.isRAImport()) {
                TxExecutionContextHandler.addTxn(tx);
            }

            recoveredTransactions = true;
        } else {
            // Discard any recoverable unit that is not reconstructed
            recoverableUnits.remove();
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "handleTranRecord", recoveredTransactions);
        return recoveredTransactions;
    }

    public void prepareToShutdown() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "prepareToShutdown");

        // Recovery processing associated with this specific recovery manager can be
        // be in a number of states:-
        // 
        // 1. Not yet started (about to be run from the TxServiceImpl class)
        // 2. Running (on a separate thread)
        // 3. Complete (dormant)
        // 
        // Either way, it will eventually complete and allow the waitForRecoveryCompletion()
        // call to return (see below).
        // 
        // However, we need to get this shutdown process going asap to allow quick shutdown
        // for reasons of speed and complience with the HA-framework requirements. For this
        // reason, we set a flag '_shutdownInProgress' to cause any recovery thread to drop
        // processing at the next logical point (recovery processing is divided into logical
        // chunks, with a "shall I stop" test between each)
        synchronized (_recoveryMonitor) {
            _shutdownInProgress = true;
            _recoveryMonitor.notify();
        }

        // Now ensure that any initial recovery processing has completed before we return.
        // (Note: a call to shutdownInProgress will cause this to complete since this is a logical chunk)
        waitForRecoveryCompletion();

        if (tc.isEntryEnabled())
            Tr.exit(tc, "prepareToShutdown");
    }

    /**
     * Informs the RecoveryManager that the transaction service is being shut
     * down.
     * 
     * The shutdown method can be driven in one of two ways:-
     * 
     * 1. Real server shutdown. The TxServiceImpl.destroy method runs through
     * all its FailureScopeControllers and calls shutdown on them. This in
     * turn directs the associated RecoveryManagers to shutdown.
     * 
     * 2. Peer recovery termination. The Recovery Log Service has directed the
     * transactions RecoveryAgent (TxServiceImpl again) to terminateRecovery
     * for the associated failure scope. TxServiceImpl directs the
     * corrisponding FailureScopeController to shutdown and again this directs
     * the associated RecoveryManager to shutdown (on its own this time others
     * stay running)
     * 
     * For immediate shutdown,
     * 
     * For quiesce,
     * 
     * YOU MUST HAVE CALLED "prepareToShutdown" BEFORE MAKING THIS CALL. THIS IS
     * REQUIRED IN ORDER THAT RECOVERY PROCESSING IS STOPPED BEFORE DRIVING
     * THE SHUTDOWN LOGIC.
     * 
     * @param immediate Indicates whether to stop immediately.
     */
    public void preShutdown(boolean transactionsLeft) throws Exception /* @PK31789C */
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "preShutdown", transactionsLeft);

        try {
            // Terminate partner log activity
            getPartnerLogTable().terminate(); // 172471

            // If the tranlog is null then we're using in memory logging and
            // the work the shutdown the log is not required.
            if (_tranLog != null) {
                //
                // Check if any transactions still active...
                //
                if (!transactionsLeft) {
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "There is no transaction data requiring future recovery");

                    if (_tranlogServiceData != null) {
                        if (tc.isDebugEnabled())
                            Tr.debug(tc, "Erasing service data from transaction log");

                        // transactions stopped running now
                        try {
                            _tranLog.removeRecoverableUnit(_tranlogServiceData.identity());
                        } catch (Exception e) {
                            FFDCFilter.processException(e, "com.ibm.tx.jta.impl.RecoveryManager.preShutdown", "359", this);
                            Tr.error(tc, "WTRN0029_ERROR_CLOSE_LOG_IN_SHUTDOWN");
                            throw e; /* @PK31789A */
                        }
                    } else {
                        if (tc.isDebugEnabled())
                            Tr.debug(tc, "No service data to erase from transaction log");
                    }
                } else if (_tranlogServiceData != null) // Only update epoch if there is data in the log - d671043
                {
                    // Force the tranlog by rewriting the epoch in the servicedata.  This will ensure
                    // any previous completed transactions will have their end-records forced.  Then
                    // it is safe to remove partner log records.  Otherwise, we can get into the state
                    // of recovering completed txns that are still in-doubt in the txn log but have
                    // no partner log entries as we've cleaned them up.  We can add code to cope with
                    // this but it gets very messy especially if recovery/shutdown keeps repeating
                    // itself - we need to check for NPE at every partner log check.

                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "There is transaction data requiring future recovery. Updating epoch");

                    if (_failureScopeController.localFailureScope() || (_tranlogServiceData != null && _tranlogEpochSection != null)) {

                        try {
                            _tranlogEpochSection.addData(Util.intToBytes(_ourEpoch));
                            _tranlogServiceData.forceSections();
                        } catch (Exception e) {
                            // We were unable to force the tranlog, so just return as if we had crashed
                            // (or did an immediate shutdown) and we will recover everything at the next restart.
                            FFDCFilter.processException(e, "com.ibm.tx.jta.impl.RecoveryManager.preShutdown", "608", this);

                            if (tc.isDebugEnabled())
                                Tr.debug(tc, "Exception raised forcing tranlog at shutdown", e);
                            throw e; /* @PK31789C */
                        }
                    } else {
                        if (tc.isDebugEnabled())
                            Tr.debug(tc, "No service data to update in transaction log");
                    }

                }
            }
        } finally {
            // If this is a peer server, or a local server where there are no transactions left running
            // then close the log. In the case of the local failure scope, we are unable to close the log if
            // there are transactions running as this shutdown represents the real server shutdown and 
            // transactions may still attempt to write to the recovery log. If we close the log now in this
            // situation, server shutdown will be peppered with LogClosedException errors. Needs refinement.          
            if (_tranLog != null && ((!_failureScopeController.localFailureScope()) || (!transactionsLeft))) {
                try {
                    _tranLog.closeLog();
                } catch (Exception e) {
                    FFDCFilter.processException(e, "com.ibm.tx.jta.impl.RecoveryManager.preShutdown", "360", this);
                    Tr.error(tc, "WTRN0029_ERROR_CLOSE_LOG_IN_SHUTDOWN");
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "preShutdown");
                    throw e; /* @PK31789A */
                }
            }
            if (tc.isEntryEnabled())
                Tr.exit(tc, "preShutdown");
        }

    }

    public void postShutdown(boolean partnersLeft) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "postShutdown", new Object[] { this, partnersLeft });

        try {
            // If the partner log is null then we're using in memory logging and
            // the work the shutdown the log is not required.
            if (_xaLog != null) {
                if (partnersLeft && _partnerServiceData != null) // ensure there is data in the log - d671043
                {
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "There is partner data requiring future recovery. Updating server state");

                    //
                    // Write our shutdown restart record
                    //
                    try {
                        updateServerState(STOPPING);
                        _partnerServiceData.forceSections();
                    } catch (Exception e) {
                        FFDCFilter.processException(e, "com.ibm.tx.jta.impl.RecoveryManager.postShutdown", "779", this);
                        if (tc.isEventEnabled())
                            Tr.event(tc, "updateServerState failed at shutdown on XAResources log", e);
                    }
                } else {
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "There is no partner data requiring future recovery");

                    // Delete the restart record as there is no data to recover
                    // This will ensure we do not carry forward classpaths etc.
                    // In the case of peer recovery of an empty peers log, we
                    // will not have retrieved any service data and will also
                    // not have created new default serviec data (unlike local
                    // recovery) so only do this if there really was some service
                    // data to erase.
                    if (_partnerServiceData != null) {
                        if (tc.isDebugEnabled())
                            Tr.debug(tc, "Erasing service data from partner log");

                        try {
                            _xaLog.removeRecoverableUnit(_partnerServiceData.identity());
                        } catch (Exception e) {
                            FFDCFilter.processException(e, "com.ibm.tx.jta.impl.RecoveryManager.postShutdown", "793", this);
                            if (tc.isEventEnabled())
                                Tr.event(tc, "removeRecoverableUnit failed at shutdown on XAResources log", e);
                        }
                    } else {
                        if (tc.isDebugEnabled())
                            Tr.debug(tc, "No service data to erase from partner log");
                    }
                }
            }
        } finally {
            if (_xaLog != null) {
                try {
                    _xaLog.closeLog();
                } catch (Exception e) {
                    FFDCFilter.processException(e, "com.ibm.tx.jta.impl.RecoveryManager.postShutdown", "824", this);
                    Tr.error(tc, "WTRN0029_ERROR_CLOSE_LOG_IN_SHUTDOWN");
                }

                try
                {
                    if (_leaseLog != null)
                    {
                        _leaseLog.deleteServerLease(_failureScopeController.serverName());
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    // Do you need FFDC here? Remember FFDC instrumentation and @FFDCIgnore
                    // http://was.pok.ibm.com/xwiki/bin/view/Liberty/LoggingFFDC
                    e.printStackTrace();
                }
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "postShutdown");
    }

    protected void checkPartnerServiceData(RecoverableUnit ru) throws IOException {
        if (_partnerServiceData == null) {
            _partnerServiceData = ru;
        } else if (_partnerServiceData != ru) {
            if (tc.isEventEnabled())
                Tr.event(tc, "Multiple log service data records found");
            throw new IOException(_xaLog.logProperties().logName() + " corrupted");
        }
        return;
    }

    /**
     * Reads the Partner log entries and replays them with the PartnerLogTable.
     * 
     * Returns the number of XA RM records found in the log, ie the number we will need
     * to perform recover on.
     * 
     * Note: the partner log is read before the transaction log.
     */
    protected int replayPartnerLog() throws Exception {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "replayPartnerLog");

        int result = 0;
        int recoveredServiceItems = 0;
        int records = 0;

        //
        // Replay all of the records and build the PartnerLogData table.  One record should be the service data
        // and contains the Classpath, server state, server name, applId and epoch for the server.
        //
        LogCursor recoverableUnits = null;
        LogCursor recoverableUnitSections = null;

        // We have two logical views of the same log on z/OS.  On the first
        // restart the service data might be in the main log, while on
        // subsequent restarts it will be in the restart log.  We need to
        // iterate over both of them, just in case.
        final ArrayList<RecoveryLog> logs = new ArrayList<RecoveryLog>(2);
        if (_recoverXaLog != null) {
            logs.add(_recoverXaLog);
        }
        logs.add(_xaLog);

        final Iterator logIterator = logs.iterator();

        boolean shuttingDown = false;

        try {
            while (logIterator.hasNext() && !shuttingDown) {
                final RecoveryLog currentLog = (RecoveryLog) logIterator.next();
                recoverableUnits = currentLog.recoverableUnits();

                while (recoverableUnits.hasNext() && !shuttingDown) {
                    records++;
                    final RecoverableUnit ru = (RecoverableUnit) recoverableUnits.next();
                    final long id = ru.identity();
                    if (tc.isEventEnabled())
                        Tr.event(tc, "Replaying record " + id + " from the partner log");
                    byte[] xaLogData = null;
                    int xaPriority = JTAResource.DEFAULT_COMMIT_PRIORITY;

                    recoverableUnitSections = ru.sections();
                    while (recoverableUnitSections.hasNext()) {
                        final RecoverableUnitSection rus = (RecoverableUnitSection) recoverableUnitSections.next();
                        final int rusId = rus.identity();
                        final byte[] logData = rus.lastData(); // Only ever have 1 data item per Partner log sections
                        if (tc.isEventEnabled())
                            Tr.event(tc, "Replaying section " + rusId, Util.toHexString(logData));
                        switch (rusId) {
                        //
                        // The XAResource and WSCoordinator sections exist in single recoverable units
                        // so they can be individually accessed by recoverable unit id from the tranlog
                        //
                            case TransactionImpl.XARESOURCEDATA_SECTION:
                                if (tc.isDebugEnabled())
                                    Tr.debug(tc, "XA resources data record");
                                if (logData != null) {
                                    // On z/OS, we cannot add entries from the restart
                                    // log to the table, because these entries are for
                                    // a previous epoch, and entries from this epoch
                                    // would be able to find and use them.
                                    if (currentLog != _recoverXaLog) /* @MD18134A */
                                    { /* @MD18134A */
                                        xaLogData = logData; // Save until we find an optional priority
                                        if (tc.isDebugEnabled())
                                            Tr.debug(tc, "XA resource", xaLogData);
                                    }
                                }
                                break;
                            case TransactionImpl.RESOURCE_PRIORITY_SECTION:
                                if (tc.isDebugEnabled())
                                    Tr.debug(tc, "Resources priority record");
                                if (logData != null && logData.length == 4) {
                                    xaPriority = Util.getIntFromBytes(logData, 0, 4);
                                    if (tc.isDebugEnabled())
                                        Tr.debug(tc, "Resources priority", xaPriority);
                                } else {
                                    if (tc.isEventEnabled())
                                        Tr.event(tc, "Invalid RESOURCE_PRIORITY_SECTION found");
                                    throw new IOException(_xaLog.logProperties().logName() + " corrupted");
                                }
                                break;
                            case TransactionImpl.WSCOORDINATOR_SECTION:
                                handleWSCSection(logData, id);
                                break;
                            case TransactionImpl.JCAPROVIDER_SECTION:
                                if (tc.isDebugEnabled())
                                    Tr.debug(tc, "JCA provider data record");
                                if (logData != null) {
                                    final JCARecoveryData jcard = new JCARecoveryData(this, logData, id);
                                    getPartnerLogTable().addEntry(jcard);
                                }
                                break;
                            //
                            // The following sections should all exist in the same RecoverableUnit and only
                            // one of each should exist in the partner log.
                            //
                            case TransactionImpl.CLASSPATH_SECTION:
                                handleClasspathSection(ru, logData, rus);
                                break;
                            case TransactionImpl.SERVER_STATE_SECTION:
                                if (tc.isDebugEnabled())
                                    Tr.debug(tc, "Server state data record");
                                if (_stateSection != null) {
                                    if (tc.isEventEnabled())
                                        Tr.event(tc, "Multiple log SERVER_STATE_SECTIONs found");
                                    Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _xaLog.logProperties().logName());
                                    throw new IOException(_xaLog.logProperties().logName() + " corrupted");
                                }
                                checkPartnerServiceData(ru);
                                _stateSection = rus;
                                recoveredServiceItems++;
                                // Extract the server status indicator
                                int flag = -1;
                                if (logData != null && logData.length > 3)
                                    flag = Util.getIntFromBytes(logData, 0, 4);

                                if (flag == STARTING) {
                                    if (tc.isEventEnabled())
                                        Tr.event(tc, "previous server may have crashed");
                                } else if (flag == STOPPING) {
                                    if (tc.isEventEnabled())
                                        Tr.event(tc, "previous server closed down cleanly with transactions still running");
                                } else {
                                    // If the log record data is invalid, then return immediately.
                                    if (tc.isEventEnabled())
                                        Tr.event(tc, "Invalid log record data in SERVER_STATE_SECTION");
                                    Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _xaLog.logProperties().logName());
                                    throw new IOException(_xaLog.logProperties().logName() + " corrupted");
                                }
                                break;
                            case TransactionImpl.SERVER_DATA_SECTION:
                                if (tc.isDebugEnabled())
                                    Tr.debug(tc, "Server name data record");
                                if (_partnerServerSection != null) {
                                    if (tc.isEventEnabled())
                                        Tr.event(tc, "Multiple log SERVER_DATA_SECTIONs found");
                                    Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _xaLog.logProperties().logName());
                                    throw new IOException(_xaLog.logProperties().logName() + " corrupted");
                                }
                                checkPartnerServiceData(ru);
                                _partnerServerSection = rus;
                                recoveredServiceItems++;
                                if ((logData != null) && (logData.length != 0)) {
                                    _recoveredServerName = new String(logData);
                                    //
                                    // Check to see if the logged serverName is the same as this server
                                    //
                                    if (!_failureScopeController.serverName().equals(_recoveredServerName)) {
                                        Tr.warning(tc, "WTRN0020_RECOVERING_TRANSACTIONS", _recoveredServerName);
                                    }
                                }
                                break;
                            case TransactionImpl.APPLID_DATA_SECTION:
                                if (tc.isDebugEnabled())
                                    Tr.debug(tc, "Applid data record");
                                if (_partnerApplIdSection != null) {
                                    if (tc.isEventEnabled())
                                        Tr.event(tc, "Multiple log APPLID_DATA_SECTIONs found");
                                    Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _xaLog.logProperties().logName());
                                    throw new IOException(_xaLog.logProperties().logName() + " corrupted");
                                }
                                checkPartnerServiceData(ru);
                                _partnerApplIdSection = rus;
                                recoveredServiceItems++;
                                if (logData != null) {
                                    _recoveredApplId = logData;
                                }
                                break;
                            case TransactionImpl.EPOCH_DATA_SECTION:
                                if (tc.isDebugEnabled())
                                    Tr.debug(tc, "Epoch data record");
                                if (_partnerEpochSection != null) {
                                    if (tc.isEventEnabled())
                                        Tr.event(tc, "Multiple log EPOCH_DATA_SECTIONs found");
                                    Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _xaLog.logProperties().logName());
                                    throw new IOException(_xaLog.logProperties().logName() + " corrupted");
                                }
                                checkPartnerServiceData(ru);
                                _partnerEpochSection = rus;
                                recoveredServiceItems++;
                                if (logData != null && logData.length > 3) {
                                    _recoveredEpoch = Util.getIntFromBytes(logData, 0, 4);
                                    if (tc.isDebugEnabled())
                                        Tr.debug(tc, "Recovered epoch: " + _recoveredEpoch);
                                }
                                break;
                            case TransactionImpl.LOW_WATERMARK_SECTION:
                                if (tc.isDebugEnabled())
                                    Tr.debug(tc, "Low Watermark section");
                                if (_partnerLowWatermarkSection != null) {
                                    if (tc.isEventEnabled())
                                        Tr.event(tc, "Multiple log LOW_WATERMARKs found");
                                    Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _xaLog.logProperties().logName());
                                    throw new IOException(_xaLog.logProperties().logName() + " corrupted");
                                }
                                checkPartnerServiceData(ru);

                                _partnerLowWatermarkSection = rus;
                                recoveredServiceItems++;
                                // Extract the server status indicator
                                if (logData != null && logData.length > 3)
                                    _partnerEntryLowWatermark = Util.getIntFromBytes(logData, 0, 4);

                                else {
                                    if (tc.isEventEnabled())
                                        Tr.event(tc, "LowWatermark data is invalid");
                                    Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _xaLog.logProperties().logName());
                                    throw new IOException(_xaLog.logProperties().logName() + " corrupted");
                                }

                                break;
                            case TransactionImpl.NEXT_ID_SECTION:
                                if (tc.isDebugEnabled())
                                    Tr.debug(tc, "Next Id section");
                                if (_partnerNextIdSection != null) {
                                    if (tc.isEventEnabled())
                                        Tr.event(tc, "Multiple log NEXT_IDs found");
                                    Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _xaLog.logProperties().logName());
                                    throw new IOException(_xaLog.logProperties().logName() + " corrupted");
                                }
                                checkPartnerServiceData(ru);

                                _partnerNextIdSection = rus;
                                recoveredServiceItems++;
                                // Extract the server status indicator
                                if (logData != null && logData.length > 3)
                                    _partnerEntryNextId = Util.getIntFromBytes(logData, 0, 4);
                                else {
                                    if (tc.isEventEnabled())
                                        Tr.event(tc, "NextId data is invalid");
                                    Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _xaLog.logProperties().logName());
                                    throw new IOException(_xaLog.logProperties().logName() + " corrupted");
                                }

                                break;
                            default:
                                if (tc.isEventEnabled())
                                    Tr.event(tc, "Invalid partner log data records found");
                                Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _xaLog.logProperties().logName());
                                throw new IOException(_xaLog.logProperties().logName() + " corrupted");
                        }
                    }
                    recoverableUnitSections.close();
                    recoverableUnitSections = null;

                    if (handleXAResourceRecord(currentLog, xaLogData, id, xaPriority)) {
                        result++;
                    }

                    // Determine if shutdown processing started during replayPartnerLog processing. 
                    // If it has, no further action can be taken.
                    if (shutdownInProgress()) {
                        shuttingDown = true;
                    }
                }
                recoverableUnits.close();
                recoverableUnits = null;
            } /* @MD18134M */
        } catch (IOException ioe) {
            FFDCFilter.processException(ioe, "com.ibm.tx.jta.impl.RecoveryManager.replayPartnerLog", "1100", this);
            if (recoverableUnitSections != null)
                recoverableUnitSections.close();
            if (recoverableUnits != null)
                recoverableUnits.close();
            Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _xaLog.logProperties().logName());
            if (tc.isEntryEnabled())
                Tr.exit(tc, "replayPartnerLog", ioe);
            throw ioe;
        }

        // Only bother to check that the retrieved information is valid if we are not
        // trying to stop processing the partner log.
        if (!shuttingDown) {
            if (records > 1) {
                // We have at least recovered a record for an XARM or partner system
                // Check we have all of the service data
                if (recoveredServiceItems != PARTNERLOG_SERVICE_ITEMS) {
                    if (tc.isEventEnabled())
                        Tr.event(tc, "Recoverable log records found without service data records. Got " + recoveredServiceItems + " expected " + PARTNERLOG_SERVICE_ITEMS);
                    IOException ioe = new IOException(_xaLog.logProperties().logName() + " corrupted");
                    FFDCFilter.processException(ioe, "com.ibm.tx.jta.impl.RecoveryManager.replayPartnerLog", "1118", this);
                    Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _xaLog.logProperties().logName());
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "replayPartnerLog", ioe);
                    throw ioe;
                }
            } else if (recoveredServiceItems != 0 && recoveredServiceItems != PARTNERLOG_SERVICE_ITEMS) {
                if (tc.isEventEnabled())
                    Tr.event(tc, "Only a subset of service data records recovered");
                IOException ioe = new IOException(_xaLog.logProperties().logName() + " corrupted");
                FFDCFilter.processException(ioe, "com.ibm.tx.jta.impl.RecoveryManager.replayPartnerLog", "1124", this);
                Tr.warning(tc, "WTRN0019_LOGFILE_CORRUPTED", _xaLog.logProperties().logName());
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "replayPartnerLog", ioe);
                throw ioe;
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "replayPartnerLog", result);
        return result;
    }

    protected boolean handleXAResourceRecord(RecoveryLog currentLog, byte[] xaLogData, long id, int xaPriority) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "handleXAResourceRecord", new Object[] { currentLog, xaLogData, id, xaPriority });

        // If we have a XA Resources record, process it with any priority from the log.
        if (xaLogData != null) {
            // Create an XARecoveryData entry and add to table.
            // Deserialize and recover later.
            final XARecoveryData xard = new XARecoveryData(currentLog, xaLogData, id, xaPriority);
            xard.setFailureScopeController(_failureScopeController);
            getPartnerLogTable().addEntry(xard);

            if (tc.isEntryEnabled())
                Tr.exit(tc, "handleXAResourceRecord", Boolean.TRUE);
            return true;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "handleXAResourceRecord", Boolean.FALSE);
        return false;
    }

    @SuppressWarnings("unused")
    protected void handleClasspathSection(RecoverableUnit ru, byte[] logData, RecoverableUnitSection rus) throws IOException {
        // Not used in JTM
    }

    @SuppressWarnings("unused")
    protected void handleWSCSection(byte[] logData, long id) throws Exception {
        // Not used in JTM
    }

    /**
     * Update service data in the tran log files
     * 
     */
    protected void updateTranLogServiceData() throws Exception {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "updateTranLogServiceData");

        try {
            //
            // Check we have a recoverableUnit and three sections to add the data.
            // On recovery we check that all service data sections were recovered.
            //
            if (_tranlogServiceData == null) {
                _tranlogServiceData = _tranLog.createRecoverableUnit();
                _tranlogServerSection = _tranlogServiceData.createSection(TransactionImpl.SERVER_DATA_SECTION, true);
                _tranlogApplIdSection = _tranlogServiceData.createSection(TransactionImpl.APPLID_DATA_SECTION, true);
                _tranlogEpochSection = _tranlogServiceData.createSection(TransactionImpl.EPOCH_DATA_SECTION, true);
                // Log this server name
                _tranlogServerSection.addData(Utils.byteArray(_failureScopeController.serverName()));
                // Log our ApplId
                _tranlogApplIdSection.addData(_ourApplId);
            }

            // Always update this server's current epoch
            _tranlogEpochSection.addData(Util.intToBytes(_ourEpoch));
            _tranlogServiceData.forceSections();
        } catch (Exception e) {
            FFDCFilter.processException(e, "com.ibm.tx.jta.impl.RecoveryManager.updateTranLogSeviceData", "1130", this);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "updateTranLogServiceData", e);
            throw e;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "updateTranLogServiceData");
    }

    /**
     * Update service data in the partner log files
     * 
     */
    protected void updatePartnerServiceData() throws Exception {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "updatePartnerServiceData");

        try {
            //
            // Check we have a recoverableUnit and three sections to add the data.
            // On recovery we check that all service data sections were recovered.
            //
            if (_partnerServiceData == null) {
                if (_recoverXaLog != null)
                    _partnerServiceData = _recoverXaLog.createRecoverableUnit();
                else
                    _partnerServiceData = _xaLog.createRecoverableUnit();

                _partnerServerSection = _partnerServiceData.createSection(TransactionImpl.SERVER_DATA_SECTION, true);
                _partnerApplIdSection = _partnerServiceData.createSection(TransactionImpl.APPLID_DATA_SECTION, true);
                _partnerEpochSection = _partnerServiceData.createSection(TransactionImpl.EPOCH_DATA_SECTION, true);
                _partnerLowWatermarkSection = _partnerServiceData.createSection(TransactionImpl.LOW_WATERMARK_SECTION, true); /* @MD18134A */
                _partnerNextIdSection = _partnerServiceData.createSection(TransactionImpl.NEXT_ID_SECTION, true); /* @MD18134A */

                // Log this server name
                _partnerServerSection.addData(Utils.byteArray(_failureScopeController.serverName()));
                // Log our ApplId
                _partnerApplIdSection.addData(_ourApplId);

                // Low watermark will start at 1
                _partnerEntryLowWatermark = 1; /* @MD18134A */
                _partnerLowWatermarkSection.addData(Util.intToBytes(_partnerEntryLowWatermark)); /* @MD18134A */

                // Next ID will start at 2.
                _partnerEntryNextId = 2; /* @MD18134A */
                _partnerNextIdSection.addData(Util.intToBytes(_partnerEntryNextId)); /* @MD18134A */
            }

            // Always update this server's current epoch
            _partnerEpochSection.addData(Util.intToBytes(_ourEpoch));

            // Update the server state in the log
            updateServerState(STARTING);

            _partnerServiceData.forceSections();
        } catch (Exception e) {
            FFDCFilter.processException(e, "com.ibm.tx.jta.impl.RecoveryManager.updatePartnerSeviceData", "1224", this);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "updatePartnerServiceData", e);
            throw e;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "updatePartnerServiceData");
    }

    private void updateServerState(int flag) throws Exception {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "updateServerState", flag);

        try {
            if (_stateSection == null) {
                _stateSection = _partnerServiceData.createSection(TransactionImpl.SERVER_STATE_SECTION, true);
            }

            _stateSection.addData(Util.intToBytes(flag));
        } catch (Exception e) {
            FFDCFilter.processException(e, "com.ibm.tx.jta.impl.RecoveryManager.updateServerState", "1250", this);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "updateServerState", e);
            throw e;
        }
        if (tc.isEntryEnabled())
            Tr.exit(tc, "updateServerState");
    }

    /**
     * Waits for replay to complete. This is used by coordinatorResources etc
     * if an incoming request occurs and we construct a coordResource but recovery
     * is still active reading/updating the logs. As the orb will have already
     * called us to recreate the objects, rather than just rejecting the call with
     * some transient execption, we wait a while as we know we will be ready soon.
     */
    public void waitForReplayCompletion() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "waitForReplayCompletion");

        if (!_replayCompleted) {
            try {
                if (tc.isEventEnabled())
                    Tr.event(tc, "starting to wait for replay completion");

                _replayInProgress.waitEvent();

                if (tc.isEventEnabled())
                    Tr.event(tc, "completed wait for replay completion");
            } catch (InterruptedException exc) {
                FFDCFilter.processException(exc, "com.ibm.tx.jta.impl.RecoveryManager.waitForReplayCompletion", "1242", this);
                if (tc.isEventEnabled())
                    Tr.event(tc, "Wait for resync complete interrupted.");
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "waitForReplayCompletion");
    }

    public void replayComplete() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "replayComplete");

        final FailureScopeLifeCycle fslc = makeFailureScopeActive(_failureScopeController.failureScope(), _failureScopeController.localFailureScope());
        _failureScopeController.setFailureScopeLifeCycle(fslc);

        _replayCompleted = true;
        _replayInProgress.post();

        if (tc.isEntryEnabled())
            Tr.exit(tc, "replayComplete");
    }

    protected FailureScopeLifeCycle makeFailureScopeActive(FailureScope fs, boolean isLocal) {
        return FailureScopeLifeCycleHelper.addToActiveList(fs, isLocal);
    }

    /**
     * Blocks the current thread until initial recovery has completed.
     */
    private void waitForRecoveryCompletion() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "waitForRecoveryCompletion");

        if (!_recoveryCompleted) {
            try {
                if (tc.isEventEnabled())
                    Tr.event(tc, "starting to wait for recovery completion");

                _recoveryInProgress.waitEvent();

                if (tc.isEventEnabled())
                    Tr.event(tc, "completed wait for recovery completion");
            } catch (InterruptedException exc) {
                FFDCFilter.processException(exc, "com.ibm.tx.jta.impl.RecoveryManager.waitForRecoveryCompletion", "1242", this);
                if (tc.isEventEnabled())
                    Tr.event(tc, "Wait for recovery complete interrupted.");
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "waitForRecoveryCompletion");
    }

    /**
     * Marks recovery as completed and signals the recovery director to this effect.
     */
    public void recoveryComplete() /* @LIDB3187C */
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "recoveryComplete");

        if (!_recoveryCompleted) {
            _recoveryCompleted = true;
            _recoveryInProgress.post();
        }

        // Check for null currently required as z/OS creates this object with a null agent reference.
        if (_agent != null) {
            try {
                RecoveryDirectorFactory.recoveryDirector().initialRecoveryComplete(_agent, _failureScopeController.failureScope());
            } catch (Exception exc) {
                FFDCFilter.processException(exc, "com.ibm.tx.jta.impl.RecoveryManager.recoveryComplete", "1546", this);
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "recoveryComplete");
    }

    public boolean recoveryFailed() {
        return _recoveryFailed;
    }

    /**
    */
    protected void recoveryFailed(Throwable t) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "recoveryFailed");

        _recoveryFailed = true;

        replayComplete();

        if (!_recoveryCompleted) {
            _recoveryCompleted = true;
            _recoveryInProgress.post();

            signalRecoveryComplete();
        }

        if (_failureScopeController.localFailureScope()) {
            TMHelper.asynchRecoveryProcessingComplete(t);
        } else {
            // Check for null currently required as z/OS creates this object with a null agent reference.
            if (_agent != null) {
                try {
                    RecoveryDirectorFactory.recoveryDirector().initialRecoveryFailed(_agent, _failureScopeController.failureScope());
                } catch (Exception exc) {
                    FFDCFilter.processException(exc, "com.ibm.tx.jta.impl.RecoveryManager.recoveryFailed", "1547", this);
                }
            }
        }

        // resync is not gonna be called now
        resyncComplete(new RuntimeException(t));

        if (tc.isEntryEnabled())
            Tr.exit(tc, "recoveryFailed");
    }

    protected void signalRecoveryComplete() {
        // Not used in JTM
    }

    // Checks to see if shutdown processing has begun. If it has, this method causes signals recovery
    // processing earlier than normal (ie before it would naturally have completed). Callers must
    // examine the boolean return value and not proceed with recovery if its true.
    public boolean shutdownInProgress() /* @LIDB3187C */
    {
        synchronized (_recoveryMonitor) {
            if (_shutdownInProgress) {
                // Put out a message stating the we are stopping recovery processing. Since this method can
                // be called a number of times in succession (to allow nested method calls to bail out by
                // calling this method) we only put the message out the first time round.
                if (!_recoveryCompleted) {
                    if (tc.isEventEnabled())
                        Tr.event(tc, "Shutdown is in progress, stopping recovery processing");
                    recoveryComplete();

                    if (_failureScopeController.localFailureScope()) {
                        TMHelper.asynchRecoveryProcessingComplete(null);
                    }
                }
            }
        }

        return _shutdownInProgress;
    }

    /**
     * Performs resync processing for single process mode operation.
     * <p>
     * For multi-process operation, resync is driven from the controller.
     * <p>
     * Requests that the RecoveryManager proceed with recovery of XA resources
     * via XARecoveryData/XARminst. Each XA RM is contacted for XIDs which make
     * appropriate matches with ourBranchQualifier. The XA RM information is
     * deserialized and validated prior to xa_recover. Any XIDs owned by this
     * server but for which there are no transactions are rolled back. Once
     * a first pass at recovering the RMs, any recovered transactions are
     * restarted. If these require RMs which have not yet been contacted, they
     * will remain uncompleted. We will poll the RMs and transactions until
     * recovery is completed or termination is requested.
     */
    protected void resync(int XAEntries) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "resync", XAEntries);

        try {
            boolean XArecovered = (XAEntries == 0);
            boolean auditRecovery = ConfigurationProviderManager.getConfigurationProvider().getAuditRecovery();

            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            if (auditRecovery) {
                Tr.audit(tc, "WTRN0134_RECOVERING_XARMS", XAEntries);
            }

            int retryAttempts = 0;
            final int configuredWait = ConfigurationProviderManager.getConfigurationProvider().getHeuristicRetryInterval();
            int retryWait = (configuredWait <= 0) ? TransactionImpl.defaultRetryTime : configuredWait;

            boolean callRecoveryComplete = true;
            boolean stopProcessing = shutdownInProgress();

            while (!stopProcessing) {
                if (retryAttempts > 0 && tc.isDebugEnabled())
                    Tr.debug(tc, "Retrying resync");

                // Get list of current recovered transactions
                TransactionImpl[] recoveredTransactions = getRecoveringTransactions();

                // Contact the XA RMs and get the indoubt XIDs.  Match any with our transactions, or else
                // roll back the ones that we have no information about.  If some fail to recover, we retry
                // later.
                if (!XArecovered) {
                    // Build a list of transaction Xids to check with each recovered RM
                    final Xid[] txnXids = new Xid[recoveredTransactions.length];
                    for (int i = 0; i < recoveredTransactions.length; i++) {
                        txnXids[i] = recoveredTransactions[i].getXid();
                    }

                    // plt recover will return early if shutdown is detected.
                    XArecovered = getPartnerLogTable().recover(this, cl, txnXids);
                }

                if (XArecovered) {
                    // If there are any transactions, proceed with recover.
                    for (int i = 0; i < recoveredTransactions.length; i++) {
                        if (stopProcessing = shutdownInProgress()) {
                            break;
                        }

                        try {
                            //LIDB3645: don't recover JCA inbound tx in recovery-mode
                            if (!(recoveryOnlyMode && recoveredTransactions[i].isRAImport())) {
                                recoveredTransactions[i].recover();
                                if (recoveryOnlyMode) {
                                    if (recoveredTransactions[i].getTransactionState().getState() != TransactionState.STATE_NONE) {
                                        Tr.warning(tc, "WTRN0114_TRAN_RETRY_NEEDED",
                                                   new Object[] { recoveredTransactions[i].getTranName(),
                                                                 retryWait });
                                    }
                                }
                            }
                        } catch (Throwable exc) {
                            FFDCFilter.processException(exc, "com.ibm.tx.jta.impl.RecoveryManager.resync", "1654", this);
                            Tr.error(tc, "WTRN0016_EXC_DURING_RECOVERY", exc);
                        }
                    }
                }

                if (stopProcessing = shutdownInProgress()) {
                    break;
                }

                boolean waitForTxComplete;
                if (recoveryOnlyMode || _waitForRecovery) {
                    waitForTxComplete = !recoveryModeTxnsComplete();
                } else {
                    waitForTxComplete = (_recoveringTransactions.size() > 0);
                }

                if (!XArecovered || waitForTxComplete) {
                    if (retryAttempts == 0 && !recoveryOnlyMode && _failureScopeController.localFailureScope()) {
                        // Call recoveryComplete after first attempt through the loop except for recoveryOnlyMode
                        // when we are the local server.  For recoveryOnlyMode we call recoveryComplete once we
                        // have fully completed recovery as this is a indication to shutdown the server.
                        // For the other case of local recovery, recoveryComplete indicates that we are far enough
                        // through recovery to allow further HA recovery to be enabled by joining their HA groups
                        // if everything was successful or shutdown the server if anything failed. 
                        // For peer recovery we only call recoveryComplete once we have stopped any recovery action
                        // as this is used as a "lock" to wait on failback log tidy up.
                        // So this does not indicate that recovery has completed...
                        recoveryComplete();
                        callRecoveryComplete = false;
                    }

                    // Not yet completed recovery - wait a while and retry again
                    // Retry xa resources until complete... Note: we check txn retries in TransactionImpl
                    // and if they "timeout" they will get removed from _recoveringTransactions.
                    retryAttempts++;

                    // Extend the retry interval the same as for a normal transaction...
                    if (retryAttempts % 10 == 0 && retryWait < Integer.MAX_VALUE / 2) {
                        retryWait *= 2;
                    }

                    synchronized (_recoveryMonitor) {
                        long timeout = retryWait * 1000L;
                        long startTime = System.currentTimeMillis();
                        long startTimeout = timeout;

                        while (!_shutdownInProgress) {
                            try {
                                if (tc.isDebugEnabled())
                                    Tr.debug(tc, "Resync retry in " + timeout + " milliseconds");
                                _recoveryMonitor.wait(timeout);
                            } catch (InterruptedException e) {
                                // No FFDC Code Needed.
                                if (tc.isDebugEnabled())
                                    Tr.debug(tc, "Resync wait interrupted");
                            }

                            if (_shutdownInProgress) {
                                break;
                            }

                            long elapsedTime = System.currentTimeMillis() - startTime;
                            if (elapsedTime < startTimeout) {
                                timeout = startTimeout - elapsedTime;
                            } else {
                                break;
                            }
                        }
                    }

                    stopProcessing = shutdownInProgress();
                } else {
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "Resync completed");
                    break;
                }
            }

            if (stopProcessing) {
                // stopProcessing is either the local server closing down or peer recovery being halted for a failurescope.
                if (_failureScopeController.localFailureScope()) {
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "local failure scope resync interupted");
                    // FailureScopeController.shutdown() closes the logs            
                } else {
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "Non-local failure scope resync interupted");
                    // FailureScopeController.shutdown() does the tidy up and closes the logs
                    // Don't bother checking that we have no recovering transactions and XArecovered
                    // since recoveryComplete has already been called so we'd need extra synchronization
                    // with the shutdown thread.
                }
            }
            // If we have completed resync normally, take this opportunity to clean out the partner log
            // if all the trans have completed
            else if (XArecovered && (_recoveringTransactions.size() == 0)) {
                // NOTE TO REVIEWERS - should we close the logs here or in shutdown?
                // shutdown is always called.  We should not remove the RM from _activeRecoveryManagers
                // until shutdown as we may recieve commit/rollback for completed transactions.

                // NOTE it is NOT possible that shutdown is racing us to close the logs - it will be waiting in prepareToShutdown
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Clearing any unused partners from partner log");

                if (!_failureScopeController.localFailureScope()) {
                    // flag the RM as cleanRemoteShutdown - this stops a shutdown thread trying to close the logs too
                    // on the off chance that it is also running.
                    _cleanRemoteShutdown = true;
                    // Even though we think we have recovered everything, we need to
                    // check that there are no partners left because of some bad internal failure
                    // which may allow us to now corrupt the logs
                    // @D656080 - dont clean up logs for HA cases - if we have completed
                    // recovery the transactions will be marked complete. We will perform a
                    // XARM recovery on the next restart and clean up the partners at this point...
                    try /* @PK31789A */
                    { /* @PK31789A */
                        // preShutdown will ensure that the tranlog is forced.        @PK31789A

                        preShutdown(true); /* @D656080C */
                    } /* @PK31789A */
                    catch (Exception e) /* @PK31789A */
                    { /* @PK31789A */
                        // no FFDC required.                                          @PK31789A
                    } /* @PK31789A */

                    postShutdown(true); // Close the partner log
                } else /* @PK31789A */
                { /* @PK31789A */
                    // Ensure all end-tran records processed before we delete partners
                    boolean failed = false; /* @PK31789A */
                    if ((_tranLog != null) && (_tranLog instanceof DistributedRecoveryLog)) /* @PK31789A */
                    { /* @PK31789A */
                        try /* @PK31789A */
                        {
                            // keypoint will ensure that the tranlog is forced.       @PK31789A   
                            // If there are any exceptions, then it is not safe to    @PK31789A
                            // tidy up the partnerlog (clearUnused skipped).          @PK31789A

                            ((DistributedRecoveryLog) _tranLog).keypoint(); /* @PK31789A */
                        } /* @PK31789A */
                        catch (Exception exc2) /* @PK31789A */
                        { /* @PK31789A */
                            FFDCFilter.processException(exc2, "com.ibm.tx.jta.impl.RecoveryManager.resync", "1974", this); /* @PK31789A */
                            if (tc.isDebugEnabled())
                                Tr.debug(tc, "keypoint of transaction log failed ... partner log will not be tidied", exc2); /* @PK31789A */

                            failed = true; /* @PK31789A */
                        } /* @PK31789A */
                    } /* @PK31789A */

                    if (!failed) /* @PK31789A */
                    {
                        getPartnerLogTable().clearUnused(); /* @PK31789A */
                        if (auditRecovery)
                            Tr.audit(tc, "WTRN0133_RECOVERY_COMPLETE");
                    }
                } /* @PK31789A */

                if (callRecoveryComplete)
                    recoveryComplete();
            } else {
                // Should only get here if we are in recoveryOnlyMode for local server.
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Transactions were active. Not clearing any unused partners from partner log");
                if (callRecoveryComplete)
                    recoveryComplete();
            }
        } catch (RuntimeException r) {
            FFDCFilter.processException(r, "com.ibm.tx.jta.impl.RecoveryManager.resync", "1729", this);
            resyncComplete(r);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "resync", r);
            throw r;
        }

        resyncComplete(null);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "resync");
    }

    protected void resyncComplete(RuntimeException r) {
        TxTMHelper.resyncComplete(r);
    }

    // This method should only be called from the "recover" thread else ConcurretModificationException may arise
    protected TransactionImpl[] getRecoveringTransactions() {
        TransactionImpl[] recoveredTransactions = new TransactionImpl[_recoveringTransactions.size()];
        _recoveringTransactions.toArray(recoveredTransactions);
        return recoveredTransactions;
    }

    /*
     * Cleans up any outstanding transactions and closes the logs
     */

    public void cleanupRemoteFailureScope() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "cleanupRemoteFailureScope");

        if (!_failureScopeController.localFailureScope()) {
            // If we are in peer recovery - we need to forget any knowledge of the transactions in memory
            // Need to be careful after this as we have no transactions in memory but there may be some in the logs
            // so we need to get out as soon as possible and not do any more work with this peer.
            if (tc.isDebugEnabled())
                Tr.debug(tc, "Stop processing. Clear down any transactions");
            if (_recoveringTransactions.size() > 0) {
                // Get list of current recovered transactions

                // NOTE TO REVIEWERS - SHOULD REALLY BE SYNCHRONIZED BETWEEN ALLOCATING ARRAY AND toArray
                TransactionImpl[] recoveredTransactions = new TransactionImpl[_recoveringTransactions.size()];
                _recoveringTransactions.toArray(recoveredTransactions);

                for (int i = 0; i < recoveredTransactions.length; i++) {
                    // We need to "forget" them so we clean up any datastructures etc...
                    try {
                        recoveredTransactions[i].forgetTransaction(false);
                    } catch (Throwable exc) {
                        FFDCFilter.processException(exc, "com.ibm.tx.jta.impl.RecoveryManager.cleanupRemoteFailureScope", "2069", this);
                    }
                }
            }

            // NOTE we always shut the logs when cleaning up for a remote failure scope

            // If the resync thread has marked the recoveryManager as cleanRemoteShutdown, then it has
            // already closed the logs
            if (!_cleanRemoteShutdown) {
                try {
                    preShutdown(true); // Process the tran log
                } catch (Exception e) {
                    // No FFDC required
                }

                postShutdown(true); // Process the partner log 
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "cleanupRemoteFailureScope");
    }

    /**
     * Close the loggs without any keypoint - to be called on a failure to leave
     * the logs alone and ensure distributed shutdown code does not update them.
     * 
     * The closeLeaseLog parameter will be false in the case that we have determined that a peer is
     * recovering the server's logs.
     * 
     * @param closeLeaseLog is true if we should process a lease log
     */
    protected void closeLogs(boolean closeLeaseLog) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "closeLogs", new Object[] { this, closeLeaseLog });

        if ((_tranLog != null) && (_tranLog instanceof DistributedRecoveryLog)) {
            try {
                ((DistributedRecoveryLog) _tranLog).closeLogImmediate();
            } catch (Exception e) {
                // No FFDC Needed
            }
            _tranLog = null;
        }

        if ((_xaLog != null) && (_xaLog instanceof DistributedRecoveryLog)) {
            try {
                ((DistributedRecoveryLog) _xaLog).closeLogImmediate();
            } catch (Exception e) {
                // No FFDC Needed
            }
            _xaLog = null;
        }

        try
        {
            if (_leaseLog != null && closeLeaseLog)
            {
                _leaseLog.deleteServerLease(_failureScopeController.serverName());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // Do you need FFDC here? Remember FFDC instrumentation and @FFDCIgnore
            // http://was.pok.ibm.com/xwiki/bin/view/Liberty/LoggingFFDC
            e.printStackTrace();
        }
        if (tc.isEntryEnabled())
            Tr.exit(tc, "closeLogs");
    }

    /**
     * Main recovery processing occurs during this method. Thread.run is spun out onto another thread from TxServiceImpl
     * for single server processing such as distributed when we continue to perform resync. For non-single server models
     * such as z/OS, we only read the logs here and resync (partner or transaction) is driven later by the controller.
     */
    @Override
    public void run() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "run");
        try {
            if (tc.isDebugEnabled())
                Tr.debug(tc, "Performing recovery for " + _failureScopeController.serverName());

            // Lets update our entry in the leaseLog early
            if (_leaseLog != null)
            {
                // TODO - need a sensible lease time
                try
                {
                    //Don't update the server lease if this is a peer rather than local server.
                    if (_localRecoveryIdentity.equals(_failureScopeController.serverName()))
                        _leaseLog.updateServerLease(_failureScopeController.serverName(), _recoveryGroup, true);
                } catch (Exception exc) {
                    FFDCFilter.processException(exc, "com.ibm.tx.jta.impl.RecoveryManager.run", "1698", this);
                    Tr.error(tc, "WTRN0112_LOG_OPEN_FAILURE", _leaseLog);
                    Object[] errorObject = new Object[] { _localRecoveryIdentity };
                    Tr.audit(tc, "CWRLS0008_RECOVERY_LOG_FAILED",
                             errorObject);
                    Tr.info(tc, "CWRLS0009_RECOVERY_LOG_FAILED_DETAIL", exc);
                    // If the logs were opened successfully then attempt to close them with a close immediate. This does
                    // not cause any keypointing, it just closes the file channels and handles (if they are still open)
                    closeLogs(false);

                    recoveryFailed(exc);

                    if (tc.isEventEnabled())
                        Tr.event(tc, "Exception caught opening Lease log!", exc);
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "run");
                    return;
                }
            }

            // Open the transaction log. This contains details of inflight transactions.
            if (_tranLog != null) {
                try {
                    _tranLog.openLog();
                } catch (LogIncompatibleException exc) {
                    // No FFDC Code needed.
                    // The attempt to open the transaction log has failed because this recovery log is from a version
                    // of the RLS / transaction service thats not supported by this level of the code.
                    //
                    // This specific failure type is most likely to occur in an HA enabled environment
                    // where there are other servers in the cluster that are from a down-level websphere
                    // product. For example 5.1 and 6.0 servers in the same cluster. When a 6.0 server
                    // attempts to peer recover the 5.1 log (as will be the case since the 5.1 server
                    // will not join an HA group get ownership of its recovery logs) a
                    // LogIncompatibleException will be generated. To try and avoid confusion, this is 
                    // logged in a single place only (in the trace - to be replaced with messages asap)
                    // Additionally, no FFDC is generated.
                    //
                    // The response by this service is to stop recovery processing and be able to handle
                    // as a noop any subsequent terminateRecovery request. This prevents the service
                    // conflicting with a downlevel server thats not playing the HA game.
                    //
                    // Clearly, this is somewhat in conflict with the reporting of errors when a recovery
                    // log from an older WS install is manually recovered on newer WS install (not supported
                    // anyway). The HA issues above are deemed to be more important.
                    haltDownlevelRecovery();

                    // Now signal recovery completion to ensure that no shutdown logic will hang pending this
                    // "recovery" process.
                    recoveryComplete();

                    if (_failureScopeController.localFailureScope())
                        TMHelper.asynchRecoveryProcessingComplete(exc);

                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "run");
                    return;
                } catch (LogAllocationException exc) {
                    FFDCFilter.processException(exc, "com.ibm.tx.jta.impl.RecoveryManager.run", "1830", this);
                    Tr.error(tc, "WTRN0111_LOG_ALLOCATION", _tranLog);

                    closeLogs(true);

                    recoveryFailed(exc);

                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "run");
                    return;
                } catch (Exception exc) {
                    FFDCFilter.processException(exc, "com.ibm.tx.jta.impl.RecoveryManager.run", "1698", this);
                    Tr.error(tc, "WTRN0112_LOG_OPEN_FAILURE", _tranLog);

                    // If the logs were opened successfully then attempt to close them with a close immediate. This does
                    // not cause any keypointing, it just closes the file channels and handles (if they are still open)
                    closeLogs(true);

                    recoveryFailed(exc);

                    if (tc.isEventEnabled())
                        Tr.event(tc, "Exception caught opening transaction recovery log!", exc);
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "run");
                    return;
                }
            }

            // Check to see if shutdown has begun before proceeding. If it has, no
            // further action can be taken.
            if (shutdownInProgress()) {
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "run");
                return;
            }

            // Open the partner log. This contains details of the resource managers in use by the above inflight transactions.
            if (_xaLog != null) {
                try {
                    _xaLog.openLog();
                    if (_recoverXaLog != null)
                        _recoverXaLog.openLog();
                } catch (LogIncompatibleException exc) {
                    // No FFDC Code needed.
                    // The attempt to open the transaction log has failed because this recovery log is from a version
                    // of the RLS / transaction service thats not supported by this level of the code.
                    //
                    // This specific failure type is most likely to occur in an HA enabled environment
                    // where there are other servers in the cluster that are from a down-level websphere
                    // product. For example 5.1 and 6.0 servers in the same cluster. When a 6.0 server
                    // attempts to peer recover the 5.1 log (as will be the case since the 5.1 server
                    // will not join an HA group get ownership of its recovery logs) a
                    // LogIncompatibleException will be generated. To try and avoid confusion, this is 
                    // logged in a single place only (in the trace - to be replaced with messages asap)
                    // Additionally, no FFDC is generated.
                    //
                    // The response by this service is to stop recovery processing and be able to handle
                    // as a noop any subsequent terminateRecovery request. This prevents the service
                    // conflicting with a downlevel server thats not playing the HA game.
                    //
                    // Clearly, this is somewhat in conflict with the reporting of errors when a recovery
                    // log from an older WS install is manually recovered on newer WS install (not supported
                    // anyway). The HA issues above are deemed to be more important.
                    //
                    // There is no need to close the recovery log as its already been closed as a result
                    // of this exception.
                    haltDownlevelRecovery();

                    // Now signal recovery completion to ensure that no shutdown logic will hang pending this
                    // "recovery" process.
                    recoveryComplete();

                    if (_failureScopeController.localFailureScope())
                        TMHelper.asynchRecoveryProcessingComplete(exc);

                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "run");
                    return;
                } catch (LogAllocationException exc) {
                    FFDCFilter.processException(exc, "com.ibm.tx.jta.impl.RecoveryManager.run", "1922", this);
                    Tr.error(tc, "WTRN0111_LOG_ALLOCATION", _xaLog);

                    // If the logs were opened successfully then attempt to close them with a close immediate. This does
                    // not cause any keypointing, it just closes the file channels and handles (if they are still open)
                    closeLogs(true);

                    recoveryFailed(exc);

                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "run");
                    return;
                } catch (Exception exc) {
                    FFDCFilter.processException(exc, "com.ibm.tx.jta.impl.RecoveryManager.run", "1723", this);
                    Tr.error(tc, "WTRN0112_LOG_OPEN_FAILURE", _xaLog);

                    // If the logs were opened successfully then attempt to close them with a close immediate. This does
                    // not cause any keypointing, it just closes the file channels and handles (if they are still open)
                    closeLogs(true);

                    // Signal recovery completion to ensure that no shutdown logic will hang pending this
                    // "recovery" process.
                    recoveryFailed(exc);

                    if (tc.isEventEnabled())
                        Tr.event(tc, "Exception caught opening XA resources recovery log!", exc);
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "run");

                    return;
                }
            }

            // Check to see if shutdown has begun before proceeding. If it has, no
            // further action can be taken.
            if (shutdownInProgress()) {
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "run");
                return;
            }

            // Replay the partner log. This method also checks shutdownInProgress and exits
            // directly if it retun true.
            final int _XAEntries;

            if (_xaLog != null) {
                try {
                    _XAEntries = replayPartnerLog();
                } catch (Throwable exc) {
                    // REQD:L:1578 Recovery processing has failed. We need to add a "leave group" instruction at this point
                    // if this turns out to be a peer recovery process. If this is the main line server run then there is
                    // nothing we can do. Simply allow the server to continue in 1PC only mode. Additional cleanup required.
                    FFDCFilter.processException(exc, "com.ibm.tx.jta.impl.RecoveryManager.run", "1814", this);
                    if (tc.isEventEnabled())
                        Tr.event(tc, "An unexpected error occured during partner log replay: " + exc);

                    closeLogs(true);

                    recoveryFailed(exc);

                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "run");
                    return;
                }
            } else {
                _XAEntries = 0;
            }

            // Determine if shutdown processing started during replayPartnerLog processing. 
            // If it has, no further action can be taken.
            if (shutdownInProgress()) {
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "run");
                return;
            }

            // Replay the transaction log. This method also checks shutdownInProgress and exits
            // directly if it retun true.
            if (_tranLog != null) {
                try {
                    if ((_xaLog instanceof DistributedRecoveryLog) && (_tranLog instanceof DistributedRecoveryLog))
                    {
                        // When the partner log fails non-HA can still process JTA transactions that do not require a new partner
                        // to be logged, hence pass false to associateLog on _xaLog
                        ((DistributedRecoveryLog) _xaLog).associateLog((DistributedRecoveryLog) _tranLog, false);
                        // In the event that the tran log fails, mark the partner as failed to ensure nothing tries to modify it.
                        ((DistributedRecoveryLog) _tranLog).associateLog((DistributedRecoveryLog) _xaLog, true);
                    }
                    replayTranLog();
                } catch (Throwable exc) {
                    // REQD:L:1578 Recovery processing has failed. We need to add a "leave group" instruction at this point
                    // if this turns out to be a peer recovery process. If this is the main line server run then there is
                    // nothing we can do. Simply allow the server to continue in 1PC only mode. Additional cleanup required.
                    FFDCFilter.processException(exc, "com.ibm.tx.jta.impl.RecoveryManager.run", "1842", this);
                    if (tc.isEventEnabled())
                        Tr.event(tc, "An unexpected error occured during transaction log replay: " + exc);

                    closeLogs(true);

                    recoveryFailed(exc);

                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "run");
                    return;
                }
            }

            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "_recoveredApplId = " + (_recoveredApplId == null ? "null" : Util.toHexString(_recoveredApplId)));
                Tr.debug(tc, "_recoveredEpoch = " + _recoveredEpoch);
            }

            // Determine if shutdown processing started during replayTranLog processing. 
            // If it has, no further action can be taken.
            if (shutdownInProgress()) {
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "run");
                return;
            }

            validateServiceData();

            // If this is main server startup recovery then update the overall server applId and Epoch in the main 
            // Configuration as these will be used to create new GlobalTIDs and XIDs for this server instance.
            if (_failureScopeController.localFailureScope()) {
                Configuration.setApplId(_ourApplId);
                Configuration.setCurrentEpoch(_ourEpoch);
            }

            registerGlobalCoordinator();

            //
            // Inform the logs that all recovery work has been finished 
            // so that they can keypoint.
            //
            if (_xaLog != null) {
                try {
                    // Update the service data.
                    if (_failureScopeController.localFailureScope()) {
                        if (_tranLog != null) {
                            updateTranLogServiceData();
                        }
                        updatePartnerServiceData(); // this includes the status
                    }

                    // Determine if shutdown processing started during update processing. 
                    // If it has, no further action can be taken.
                    if (shutdownInProgress()) {
                        if (tc.isEntryEnabled())
                            Tr.exit(tc, "run");
                        return;
                    }

                    if (_tranLog != null) {
                        _tranLog.recoveryComplete();
                    }

                    // Determine if shutdown processing started during recoveryComplete processing. 
                    // If it has, no further action can be taken.
                    if (shutdownInProgress()) {
                        if (tc.isEntryEnabled())
                            Tr.exit(tc, "run");
                        return;
                    }

                    if (_xaLog != null) {
                        _xaLog.recoveryComplete();
                    }

                    // Determine if shutdown processing started during recoveryComplete processing. 
                    // If it has, no further action can be taken.
                    if (shutdownInProgress()) {
                        if (tc.isEntryEnabled())
                            Tr.exit(tc, "run");
                        return;
                    }
                } catch (Exception e) {
                    FFDCFilter.processException(e, "com.ibm.tx.jta.impl.RecoveryManager.run", "1866", this);

                    // REQD:L:1578 Recovery processing
                    // if this turns out to be a peer recovery process. If this is the main line server run then there is
                    // nothing we can do. Simply allow the server to continue in 1PC only mode. Additional cleanup required.

                    Tr.error(tc, "WTRN0026_KEYPOINT_EXC_IN_RECOVERY", e);

                    if (tc.isEventEnabled())
                        Tr.event(tc, "An unexpected error occured during keypointing: " + e);

                    recoveryFailed(e); // @254326C

                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "run");
                    return;
                }
            }

            if (tc.isEventEnabled())
                Tr.event(tc, "replay completed");

            // Post the recovery in progress event so that requests waiting for replay
            // to complete may proceed.
            replayComplete();

            // NB Any exit point (be it a thrown exception or return statement) from this method
            // prior to this point MUST call the runtime framework to inform it that the
            // transaction service is "ready"; by supplying an instance of RuntimeError we can
            // cause the runtime to fail server startup.

            // Ensure that transaction service is enabled and can now import/export transactions or
            // start transactions and enlist more than one resource.
            ((TranManagerSet) TransactionManagerFactory.getTransactionManager()).replayComplete(_failureScopeController.localFailureScope());

            performResync(_XAEntries);
        } finally {
            if (tc.isDebugEnabled())
                Tr.debug(tc, "Performed recovery for " + _failureScopeController.serverName());
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "run");
    }

    protected void registerGlobalCoordinator() {
        // Not used in JTM
    }

    protected void performResync(int xaEntries) {
        boolean auditRecovery = ConfigurationProviderManager.getConfigurationProvider().getAuditRecovery();
        if (auditRecovery) {
            if (_recoveredServerName == null) // New logs - output what we will write to logs and use for xid creation
            {
                Tr.audit(tc, "WTRN0132_RECOVERY_INITIATED", new Object[] {
                                                                          //                     _failureScopeController.serverName(), Util.toHexString(_ourApplId), _recoveredEpoch});     // PM07874
                                                                          _failureScopeController.serverName(), (_ourApplId == null ? "null" : Util.toHexString(_ourApplId)),
                                                                          _recoveredEpoch });
            } else // output data from the logs - maybe recovering another servers logs
            {
                Tr.audit(tc, "WTRN0132_RECOVERY_INITIATED", new Object[] {
                                                                          //                     _recoveredServerName, Util.toHexString(_recoveredApplId), _recoveredEpoch});               // PM07874
                                                                          _recoveredServerName, (_recoveredApplId == null ? "null" : Util.toHexString(_recoveredApplId)),
                                                                          _recoveredEpoch });
            }
        }

        int recoveredTransactions = _recoveringTransactions.size();
        if (recoveredTransactions == 1) {
            Tr.audit(tc, "WTRN0027_RECOVERING_TXN");
        } else if (recoveredTransactions > 0) {
            Tr.audit(tc, "WTRN0028_RECOVERING_TXNS", recoveredTransactions);
        } else {
            Tr.info(tc, "WTRN0135_RECOVERING_NOTXNS");
        }

        // Always call resync to process XA resources and any transactions.  This may
        // continue forever.  For local recovery, after first pass we call recoveryComplete().
        // resync handles clean up of peer recovery logs, whereas local cleanup is done
        // at shutdown by the failurescopecontroller.
        resync(xaEntries);
    }

    protected void validateServiceData() {
        // Validate the service data from each log
        if (_recoveredApplId != null)
            _ourApplId = _recoveredApplId;

        // Update the epoch for this server from the log
        if (_recoveredEpoch >= _ourEpoch)
            _ourEpoch = _recoveredEpoch + 1;
    }

    /**
     * Registers a recovered transactions existance. This method is triggered from
     * the FailureScopeController.registerTransaction for all transactions that
     * have been created during a recovery process.
     * 
     * @param tran The transaction reference object.
     */
    public void registerTransaction(TransactionImpl tran) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "registerTransaction", new Object[] { this, tran });

        _recoveringTransactions.add(tran);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "registerTransaction", _recoveringTransactions.size());
    }

    /**
     * Deregisters a recovered transactions existance. This method is triggered from
     * the FailureScopeController.deregisterTransaction for recovered transactions.
     * 
     * @param tran The transaction reference object.
     */
    public void deregisterTransaction(TransactionImpl tran) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "deregisterTransaction", new Object[] { this, tran });

        _recoveringTransactions.remove(tran);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "deregisterTransaction", _recoveringTransactions.size());
    }

    //LIDB3645: check whether any transactions have yet to complete
    // This does not include JCA inbound txns
    protected boolean recoveryModeTxnsComplete() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "recoveryModeTxnsComplete");

        if (_recoveringTransactions != null) {
            for (TransactionImpl tx : _recoveringTransactions) {
                if (tx != null && !tx.isRAImport()) {
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "recoveryModeTxnsComplete", Boolean.FALSE);
                    return false;
                }
            }
        } // end if

        if (tc.isEntryEnabled())
            Tr.exit(tc, "recoveryModeTxnsComplete", Boolean.TRUE);
        return true;
    }

    /**
     * Accessor for the recoveryPrevented flag.
     */
    public boolean recoveryPrevented() {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "recoveryPrevented", _recoveryPrevented);
        return _recoveryPrevented;
    }

    public void haltDownlevelRecovery() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "haltDownlevelRecovery");

        // Mark recovery as prevented to allow the FailureScopeController to pick this up 
        // and bypass the shutdown logic.
        _recoveryPrevented = true;

        // Lookup the failurescope for incopopration in the trace output.
        FailureScope failureScope = _failureScopeController.failureScope();

        // Output appropriate trace and message
        Tr.warning(tc, "WTRN0113_LOG_DOWNLEVEL", failureScope);

        if (tc.isDebugEnabled())
            Tr.debug(tc, "Halting recovery processing of downlevel transaction recovery log for failure scope" + failureScope + ")");
        if (tc.isEntryEnabled())
            Tr.exit(tc, "haltDownlevelRecovery");
    }

    public void setLeaseLog(SharedServerLeaseLog leaseLog)
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "setLeaseLog", new Object[] { leaseLog });

        _leaseLog = leaseLog;

        if (tc.isEntryEnabled())
            Tr.exit(tc, "RecoveryManager", this);
    }

    public void setRecoveryGroup(String recoveryGroup)
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "setRecoveryGroup", new Object[] { recoveryGroup });
        _recoveryGroup = recoveryGroup;
        if (tc.isEntryEnabled())
            Tr.exit(tc, "setRecoveryGroup");
    }

    public void setLocalRecoveryIdentity(String recoveryIdentity)
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "setLocalRecoveryIdentity", new Object[] { recoveryIdentity });
        _localRecoveryIdentity = recoveryIdentity;
        if (tc.isEntryEnabled())
            Tr.exit(tc, "setLocalRecoveryIdentity");
    }
}
