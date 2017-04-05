package com.ibm.tx.jta.impl;

/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002,2013 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date      Programmer    Defect    Description                                                        */
/*  --------  ----------    ------    -----------                                                        */
/*  02-02-25  hursdlg       111021.1  create resource recovery logs                                      */
/*  27-02-02  beavenj       LIDB1220.151.1 Code instrumented for FFDC work                               */
/*  02-02-28  hursdlg       LI1169.1  track recovery actions                                             */
/*  02-03-02  hursdlg       LI1169    complete shutdown logic                                            */
/*  02-03-08  hursdlg       120732    cleanup classpath handling                                         */
/*  02-04-11  hursdlg       124938    improve recovery diagnostics                                       */
/*  02-04-15  hursdlg       125255    fix recoveryId allocation after restart                            */
/*  02-05-13  awilkins      126658    Perform logging at prepare time                                    */
/*  05/09/02   gareth       ------    Move to JTA implementation                                         */
/*  23/09/02  hursdlg       ------    Add partner log support                                            */
/*  03/10/02  hursdlg       ------    Change behaviour of recoveryCompleted()                            */
/*  09/10/02  hursdlg       1453      Tidy up recover call                                               */
/*  10/10/02  hursdlg       1426      Update to latest recovery.log spec                                 */
/*  18/10/02  hursdlg       1433      Fix FFDC                                                           */
/*  05/11/02   gareth       1449      Tidy up messages and exceptions                                    */
/*  25/11/02  awilkins      1513      Repackage ejs.jts -> ws.Transaction                                */
/*  13-12-02  awilkins    LIDB1673.17 Embedded RAR recovery                                              */
/*  21/01/03   gareth     LIDB1673.1  Add JTA2 messages                                                  */
/*  28/01/03  hursdlg     LIDB1673.9.1 Use PartnerLogData base class                                     */
/*  21/02/03   gareth     LIDB1673.19 Make any unextended code final                                     */
/*  04/04/03  hursdlg     LIDB1673.22 Pass thru server specific recover bqual                            */
/*  22/04/03  hursdlg       163130    Add comments for serialize/deserialize                             */
/*  28/05/03  hursdlg       167373    Make XARecoveryWrapper match Aquila                                */
/*  11/06/03  hursdlg       169107    Remove redundant include                                           */
/*  10/07/03  hursdlg       169606    Improve recovery log failure                                       */
/*  25/07/03  hursdlg       172471    Reject log when terminating                                        */
/*  13/08/03  hursdlg       174113    Reorder recovery                                                   */
/*  20/08/03  hursdlg       165981.1  Validate serialization on register                                 */
/*  22/08/03  hursdlg       174849    Temporarily disable deserialize errors                             */
/*  08/09/03  hursdlg       174849.1  Fix deserialize class loader                                       */
/*  18/09/03  hursdlg       177194    Migrate to 8 byte recovery ids                                     */
/*  19/09/03  hursdlg       177276    Bypass iterator for recover                                        */
/*  22/09/03  hursdlg       174209    Update classpath on new register                                   */
/*  01/10/03  johawkes      178208.1  Use log generated recovery ids                                     */
/*  20/11/03  johawkes      182862    Remove static partner log dependencies                             */
/*  27/11/03  johawkes      178502    Start an RA during XA recovery                                     */
/*  05/12/03  johawkes      184903    Refactor PartnerLogTable                                           */
/*  06/01/04  hursdlg       LIDB2775  zOS/distributed merge                                              */
/*  07/01/04  johawkes      LIDB2110  RA Uninstall                                                       */
/*  04/02/04  johawkes      189497    Warn when RA is uninstalled                                        */
/*  24/03/04   mallam       LIDB2775  ws390 code drop                                                    */
/*  29/03/04  hursdlg       196258    Incorporate XARminst code                                          */
/*  31/03/04  johawkes      196310    Handle null from getXAResource()                                   */
/*  04/04/04  johawkes      196588    Set recovery classloader on thread                                 */
/*  13/04/04  beavenj       LIDB1578.1 Initial supprort for ha-recovery                                  */
/*  22/04/04  beavenj       LIDB1578.4 Early logging support for CScopes                                 */
/*  29/04/04  johawkes      200859    Handle UnsatisfiedLinkError                                        */
/*  21/05/04  beavenj       LIDB1578.7  FFDC                                                             */
/*  08/06/04  johawkes      207717    Properly look for txRecoveryUtils.jar                              */
/*  25/06/04  johawkes      199785    Fix partner log corruption on shutdown                             */
/*  06/07/04  johawkes      213406    Increase initial HashSet size for classpaths                       */
/*  19/08/04  johawkes      224215    Detect uninstalled providers better                                */
/*  26/09/04  hursdlg       234516    Pass FailureScopeController to PartnerLogData                      */
/*  28/09/04  mallam        235569    Use recover for zOS rollbackUnknownTx                              */
/*  08/10/04  mezarin       LIDB1578-22 z/OS HA Manager support                                          */
/*  17/01/05  mallam     LIDB3645     Recovery-mode restart                                              */
/*  15/03/05  mallam        261246    LIDB3645 code review                                               */
/*  14/06/05  hursdlg       283253    Componentization changes for recovery                              */
/*  08/12/05  johawkes      329403    Don't recover with embedded RAs until apps are loaded              */
/*  30/01/06  hursdlg       340103    Run isProviderInstalled with server subject                        */
/*  03/02/06  hursdlg       343233    Reduce output of WTRN0005 during recovery                          */
/*  05/12/06  mezarin       369064.2  Add getRecoveryClassLoader                                         */
/*  02/08/06  maples        373006    WESB performance isSameRM optimization                             */
/*  01/05/07  johawkes      434414    Remove WAS dependencies                                            */
/*  18/05/07  johawkes      438575    Further componentization                                           */
/*  05/06/07  johawkes      443467    Move XAResourceInfo                                                */
/*  17/06/07  johawkes      444613    Repackaging                                                        */
/*  20/06/07  hursdlg       LI3968-1.2 Support resource priority                                         */
/*  16/08/07  johawkes      451213    Move LPS back into JTM                                             */
/*  02/06/09  mallam        596067    package move                                                       */
/*  17/03/10  hursdlg       PM07874   Recovery audit logging                                             */
/*  13/05/10  hursdlg       649934    More PM07874 changes                                               */
/*  07-09-11  nyoung        715979    Liberty: share XAResourceFactory info through Bundle Registry      */
/*  07/02/12  johawkes      727586    Don't complain if we can't lookup XAResourceFactory yet            */
/*  20/05/13  johawkes      F099608   XAResource.setTransactionTimeout                                   */
/* ***************************************************************************************************** */
import java.io.File;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.transaction.Status;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.ibm.tx.TranConstants;
import com.ibm.tx.config.ConfigurationProviderManager;
import com.ibm.tx.jta.XAResourceFactory;
import com.ibm.tx.jta.XAResourceNotAvailableException;
import com.ibm.tx.util.logging.FFDCFilter;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.ws.Transaction.JTA.JTAResource;
import com.ibm.ws.Transaction.JTA.Util;
import com.ibm.ws.Transaction.JTA.XAReturnCodeHelper;
import com.ibm.ws.Transaction.JTA.XARminst;
import com.ibm.ws.recoverylog.spi.RecoverableUnit;
import com.ibm.ws.recoverylog.spi.RecoverableUnitSection;
import com.ibm.ws.recoverylog.spi.RecoveryLog;

/**
 * XARecoveryData is a specialization of PartnerLogData
 * 
 * The log data object is an XARecoveryWrapper and this class provides
 * methods to support the use of this particular data type.
 */
public class XARecoveryData extends PartnerLogData {
    private static final TraceComponent tc = Tr.register(XARecoveryData.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    protected boolean _supportsIsSameRM;

    // The classloader used to load the XARecoveryWrapper in recovery scenarios.
    protected ClassLoader _recoveryClassLoader;

    // Parsed log data on recovery
    protected byte[] _wrapperData;
    protected final String[] _extDirs;
    protected final int _priority;

    protected boolean auditRecovery = ConfigurationProviderManager.getConfigurationProvider().getAuditRecovery();

    /**
     * When we're calling setTransactionTimeout on XAResources, we normally stop after the first exception from an RM.
     * When this field is set, we carry on setting the timeout.
     */
    private static boolean _continuePropagatingXAResourceTimeout = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        @Override
        public Boolean run() {
            return Boolean.getBoolean("com.ibm.tx.continuePropagatingXAResourceTimeout");
        }
    });

    /**
     * This field says whether to call setTransactionTimeout on XAResources
     */
    protected boolean _propagateXAResourceTransactionTimeout = ConfigurationProviderManager.getConfigurationProvider().getPropagateXAResourceTransactionTimeout();

    /**
     * Ctor when called from registration of an XAResource
     * 
     * @param failureScopeController
     * @param logData
     */
    protected XARecoveryData(FailureScopeController failureScopeController, XARecoveryWrapper logData) {
        super(logData, failureScopeController);
        _sectionId = TransactionImpl.XARESOURCEDATA_SECTION;
        _priority = logData.getPriority();
        // Required to build a recoveryClassLoader on retries
        _extDirs = logData.getXAResourceFactoryClasspath();

        if (tc.isDebugEnabled())
            Tr.debug(tc, "XARecoveryData", new Object[] { failureScopeController, logData, this });
    }

    /**
     * Ctor when called for recovery or registration of an XAResource for z/OS HA recovery
     * 
     * @param partnerLog
     * @param logData
     * @param id
     * @param priority
     */
    /* @LIDB1578-22A */
    public XARecoveryData(RecoveryLog partnerLog, byte[] serializedLogData, long id, int priority) {
        super(serializedLogData, null, id, partnerLog);
        _priority = priority;

        // Extract serialized wrapper data and the classpath array from the serialized logdata
        int delimiterPosition = 0;

        for (int i = 0; i < serializedLogData.length; i++) {
            if (serializedLogData[i] == 0) {
                delimiterPosition = i;
                break;
            }
        }

        _wrapperData = new byte[serializedLogData.length - delimiterPosition - 1];
        System.arraycopy(serializedLogData, delimiterPosition + 1, _wrapperData, 0, _wrapperData.length);

        if (delimiterPosition > 0) {
            final byte[] classpathBytes = new byte[delimiterPosition];

            System.arraycopy(serializedLogData, 0, classpathBytes, 0, classpathBytes.length);

            final String xaResInfoClasspath = new String(classpathBytes);
            if (tc.isDebugEnabled())
                Tr.debug(tc, "Classpath data recovered", xaResInfoClasspath);

            final StringTokenizer tokenizer = new StringTokenizer(xaResInfoClasspath, File.pathSeparator);
            _extDirs = new String[tokenizer.countTokens()];

            for (int i = 0; i < _extDirs.length; i++) {
                _extDirs[i] = tokenizer.nextToken();
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "_extDirs[" + i + "] = " + _extDirs[i]);
            }
        } else {
            _extDirs = null;
        }

        if (tc.isDebugEnabled())
            Tr.debug(tc, "XARecoveryData", new Object[] { partnerLog, serializedLogData, id, priority, this });
    }

    /*
     * Perform a pre-log data check prior to logging the XARecoveryData.
     * Need to validate that the classpath hasnt changed by admin resource
     * configuration during runtime. If it has we need to log again.
     * This will only occur in main-line calls as any recovered data will
     * already be written to disk. We can just use the recovery manager
     * from the configuration object.
     */
    @Override
    protected void preLogData() throws Exception {
        _fsc.getRecoveryManager().waitForReplayCompletion();
    }

    /*
     * Perform a post-log data check after logging the XARecoveryData prior to the force.
     * Use this to log the priority in a separate log unit section to the
     * main XARecoveryData serialized wrapper and classpath data. Note: this
     * method is not called if we have no logs defined.
     */
    @Override
    protected void postLogData(RecoverableUnit ru) throws Exception {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "postLogData");

        // Only log if priority is non-zero to keep compatability with old releases
        if (_priority != JTAResource.DEFAULT_COMMIT_PRIORITY) {
            // Let caller catch any exceptions as it is already handling RU/RUS failures
            final RecoverableUnitSection section = ru.createSection(TransactionImpl.RESOURCE_PRIORITY_SECTION, true);
            section.addData(Util.intToBytes(_priority));
        }

    }

    /*
     * deserialize is called from recover prior to recovery and repeatedly if it cannot deserialize
     * for some reason. The logic of repeating this is that each record from the resource files
     * is passed to replay. replay deserializes the record and saves the information in the
     * recoveryTable. We do not need to check for duplicate recoveryIds as these are filtered out
     * by the RecoveryLogManager cache. Note: we can get multiple records which match the same
     * resource manager. This means that on recover we can get the same xids twice. This does not
     * matter as both copies of the same xid will match the same JTAXAResource. XARminst will
     * cope with a JTAXAResource from an apparent different ressource manager (rmid) - the rmid is
     * mainly used to determine if we are not able to contact a resource manager on recovery.
     * 
     * deserialize is also called various times on z/OS when reading a partner log record. Note:
     * on z/OS many SRs may be running and each has its own cached PartnerLogTable which may not be
     * in step with the recovery log. At certain points, the log is re-read and entries deserialized
     * to compare with the cached table.
     * 
     * @param parentClassLoader
     */
    public void deserialize(ClassLoader parentClassLoader) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "deserialize", new Object[] { this, parentClassLoader });

        // Deserialize the logData object.  If this fails, we may retry later.  We should
        // always be able to deserialize, otherwise we can never recover.  There should only
        // ever be resource recovery records in the log if:
        //
        // 1) we crashed - there may have been active txns
        // 2) we closed down with active txns
        // 3) we closed down with no active txns but failed to recover all resources at startup
        //
        // If we shutdown normally with no active txns, there will be no resource recovery
        // records to recover.  Note: we log the XA recovery log data at enlist time - and
        // we may never ever perform a prepare to the RM.  Also, on z/OS we never clean out the
        // logs on shutdown although this may change in the future,

        // Before we deserialize the data from the object we need to determine if there
        // is any classpath data logged. If there is then we may have to setup a special classloader
        // with this data so that the serializedLogData can be successfully deserialized.
        // We save the resulting class loader as we will need it again when we need to create
        // an xaresource for recover, commit, rollback, forget, etc. since we may not have the
        // correct parentClassLoader available at that point.   This works as we never use the
        // same XARecoveryData record for recovery and normal running.

        final XARecoveryWrapper wrapper = XARecoveryWrapper.deserialize(_wrapperData);
        if (wrapper != null) {
            if (_extDirs != null)
                wrapper.setXAResourceFactoryClasspath(_extDirs);
            wrapper.setPriority(_priority);
            _logData = wrapper;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "deserialize");
    }

    public XARecoveryWrapper getXARecoveryWrapper() {
        return (XARecoveryWrapper) _logData;
    }

    public Serializable getXAResourceInfo() {
        return ((XARecoveryWrapper) _logData).getXAResourceInfo();
    }

    public int getPriority() {
        return _priority;
    }

    /*
     * Create a XAResourceFactory and obtain an XAResource.
     * The factory and resource are returned in an XARMinst object which can be
     * used for xa_recover calls and closeConnection when complete.
     * 
     * This call is made from this.recover and from JTAXAResourceImpl.reconnectRM
     * either during recovery or retry of a failed XAResource. Care is needed when
     * considering the recoveryClassLoader (RCL). We need to use it for xa_recover
     * and subsequently on any xa_commit or xa_rollback which may be issued from
     * other threads asynchronously to the recover processing - hence we save the
     * RCL during deserialization and keep it available for the remainder of recovery.
     * When the PartnerLogData inuse count drops to zero (ie recovery is complete),
     * the RCL is released for garbage collection. For the normal running case,
     * we always build a RCL on the fly.
     */
    public XARminst getXARminst() throws XAException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "getXARminst", new Object[] { this });

        XAResource xaRes = null;
        XAResourceFactory xaResFactory;
        Serializable xaResInfo = null;

        final XARecoveryWrapper xarw = (XARecoveryWrapper) _logData;

        if ((xaResInfo = getXAResourceInfo()) instanceof DirectEnlistXAResourceInfo) {
            xaResFactory = null;
            xaRes = ((DirectEnlistXAResourceInfo) xaResInfo).getXAResource();
        } else {
            // We should always have a non-null non-blank factory classname as it is
            // validated by registerResourceInfo before creating an XARecoveryWrapper
            final String xaResFactoryClassName = xarw.getXAResourceFactoryClassName();

            /*
             * Starting in 8.5 we can have a filter registered instead of a class name
             */
            xaResFactory = XARecoveryDataHelper.lookupXAResourceFactory(xaResFactoryClassName);
            if (xaResFactory != null) {
                // Have recovered xaResourceFactory
                try {
                    xaRes = xaResFactory.getXAResource(xaResInfo);
                } catch (XAResourceNotAvailableException e) {
                    // Swallow this exception we'll follow the "traditional" codepath below
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "getXARminst", e);
                }
            }

            if (xaRes == null) {
                // If this looks like a filter we'll try again later
                if (xaResFactoryClassName.startsWith("(")) {
                    final XAException e = new XAException(XAException.XAER_RMFAIL);
                    e.initCause(new XAResourceNotAvailableException());
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "getXARminst", e);
                    throw e;
                }

                // Traditional
                Class<?> xaResFactoryClass = null;

                try {
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "calling Class.forName", xaResFactoryClassName);
                    xaResFactoryClass = Class.forName(xaResFactoryClassName);

                    // Trace the class and its loader - this should work as we are not in app code
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "xaResFactoryClass", new Object[] { xaResFactoryClass, xaResFactoryClass.getClassLoader() });

                    xaResFactory = (XAResourceFactory) xaResFactoryClass.newInstance();
                } catch (Throwable t) {
                    //
                    // If we cannot create one of our known factories, then we are broken.
                    // Flag this and carry on.  We try to recover as much as possible for all resources.
                    //
                    FFDCFilter.processException(t, "com.ibm.tx.jta.impl.XARecoveryData.getXARminst", "419");
                    Tr.error(tc, "WTRN0004_CANT_CREATE_XARESOURCEFACTORY", new Object[] { xaResFactoryClassName, t });
                    final XAException xae = new XAException(XAException.XAER_RMERR);
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "getXARminst", xae);
                    throw xae;
                }

                try {
                    // If we can't proceed of if the factory gives us a null XAResource
                    // we want to retry
                    xaResInfo = getXAResourceInfo();
                    if (null == (xaRes = xaResFactory.getXAResource(xaResInfo))) {
                        throw new XAResourceNotAvailableException();
                    }
                } catch (XAResourceNotAvailableException e) {
                    FFDCFilter.processException(e, "com.ibm.tx.jta.impl.XARecoveryData.getXARminst", "491", this);
                    final Throwable t = new XAException(XAException.XAER_RMFAIL).initCause(e);
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "getXARminst", t);
                    throw (XAException) t;
                } catch (Throwable e) {
                    //
                    // Fatal error - mark it as such for when we return
                    //
                    FFDCFilter.processException(e, "com.ibm.tx.jta.impl.XARecoveryData.getXARminst", "563");
                    Tr.error(tc, "WTRN0005_CANT_RECREATE_XARESOURCE", new Object[] { xaResInfo, e });
                    final Throwable t = new XAException(XAException.XAER_RMERR).initCause(e);
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "getXARminst", t);
                    throw (XAException) t;
                }

            }
        }

        // Trace the resource and its loader - this should work as we are not in app code
        if (tc.isDebugEnabled())
            Tr.debug(tc, "xaResource", new Object[] { xaRes, xaRes.getClass().getClassLoader() });

        //
        // Create XARminst, which is a Resource Manager proxy.
        // Need to add xaResFactory to XARminst during
        // recovery, so we can closeConnection after recovery.
        //
        final XARminst xarm = new XARminst(xaRes, xaResFactory);
        if (tc.isEntryEnabled())
            Tr.exit(tc, "getXARminst", xarm);
        return xarm;
    }

    @Override
    public boolean recover(ClassLoader cl, Xid[] knownXids, byte[] failedStoken, byte[] cruuid, int restartEpoch) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "recover", new Object[] { cl, knownXids, failedStoken, cruuid, restartEpoch, this });

        // If we've already recovered this XARecoveryData entry, skip to next
        if (_recovered) {
            if (tc.isEntryEnabled())
                Tr.exit(tc, "recover", "recovered");
            return true;
        }

        // If this entry is terminating then do not try to process it
        if (_terminating) {
            if (tc.isEntryEnabled())
                Tr.exit(tc, "recover", "terminating");
            return false; // flag to retry later
        }

        // Check we have already deserialized the log data
        if (_logData == null) {
            deserialize(cl);

            if (_logData == null) {
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "recover", "deserialize failed");
                return RecoveryManager.recoveryOnlyMode;
            }
        }

        if (tc.isDebugEnabled())
            Tr.debug(tc, "recovering", _logData);

        //
        // Create XARminst, which is a Resource Manager proxy.
        //
        XARminst xarm = null;
        try {
            auditXaRecover(getXAResourceInfo());
            xarm = getXARminst();
            if (xarm == null)
                throw new XAException(XAException.XAER_RMERR);
        } catch (XAException xae) {
            boolean result; // recovery retry complete status
            switch (xae.errorCode) {
                case XAException.XA_HEURMIX:
                    // Non-retriable condition - RA uninstalled
                    // Cant recover again so mark recovered and decrement use count so
                    // the entry will get deleted from the log
                    decrementCount();
                    _recovered = true;
                    // return _recovered status
                case XAException.XAER_RMFAIL:
                    // Retriable condition - unable to create XAResource
                    result = _recovered;
                    break;
                case XAException.XAER_RMERR:
                default:
                    // Failure case - unable to create factory or XAResource
                    // Retry in normal server state but not for recoveryOnly mode -
                    // LIDB3645 probably no point retrying,
                    // but dont mark entry recovered as we want it to stay in the log
                    // so it will be retried at next server restart.
                    result = RecoveryManager.recoveryOnlyMode;
                    break;
            }
            if (tc.isEntryEnabled())
                Tr.exit(tc, "recover", result);
            return result;
        }

        Xid[] inDoubt = null;
        int numXids = 0;
        try {
            /*----------------------------------------------------------*/
            /* Drive recovery on that resource. Pass both Start and */
            /* End flags to the resource so that the complete list of */
            /* Xids is obtained. Our WS390XARminst wrapper ensures */
            /* that a null list is never returned (the length is zero). */
            /*----------------------------------------------------------*/
            inDoubt = xarm.recover();
            _recovered = true; // Flag we have successfully issued recovery
            numXids = inDoubt.length;
        } catch (Throwable t) {
            // FFDC and messages logged in xarm.recover()
            FFDCFilter.processException(t, "com.ibm.tx.jta.impl.XARecoveryData.recover", "564", this);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "recover", false);
            return false;
        }

        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "Resource returned " + numXids + " Xids");
            for (int n = 0; n < numXids; n++) {
                if (inDoubt[n] == null)
                    continue;
                int formatID = inDoubt[n].getFormatId();
                byte[] gtrid = inDoubt[n].getGlobalTransactionId();
                byte[] bqual = inDoubt[n].getBranchQualifier();
                Tr.debug(tc, "Trace Xid[" + n + "] FormatID: " + Integer.toHexString(formatID));
                Tr.debug(tc, "Trace Xid[" + n + "] Gtrid: " + Util.toHexString(gtrid));
                Tr.debug(tc, "Trace Xid[" + n + "] Bqual: " + Util.toHexString(bqual));
            }
        }

        /*----------------------------------------------------------*/
        /* Filter out all non-WAS formatIds from the inDoubt list. */
        /*----------------------------------------------------------*/
        ArrayList xidList = filterXidsByType(inDoubt);
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "After type filter, Xids to recover " + xidList.size());
        }

        /*----------------------------------------------------------*/
        /* Filter out all Xids that don't have this */
        /* cruuid, or whose epoch number is greater than or equal */
        /* to the current epoch number. */
        /*----------------------------------------------------------*/
        xidList = filterXidsByCruuidAndEpoch(xidList, cruuid, restartEpoch);
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "After filter by cruuid and epoch, Xids " +
                         "to recover " + xidList.size());
        }

        auditXaRecoverCount(getXAResourceInfo(), numXids, xidList.size());

        /*----------------------------------------------------------*/
        /* For each Xid that is left, see if it belongs in one of */
        /* the transactions (XID_t) that we know about. If it */
        /* doesn't, forget it. */
        /*----------------------------------------------------------*/
        for (int y = 0; y < xidList.size(); y++) {
            final XidImpl ourXid = (XidImpl) xidList.get(y);
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Recovering Xid[" + y + "]", ourXid);
            }
            if (ourXid.getEpoch() < (restartEpoch - 1)) {
                auditLateEpoch(ourXid, getXAResourceInfo());
            }

            /*------------------------------------------------------*/
            /* If we have no transactions to recover, or if we don't */
            /* know about this Xid, we roll it back. */
            /*------------------------------------------------------*/
            if (knownXids == null || canWeForgetXid(ourXid, knownXids)) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Found XID with no associated transaction");

                // Roll the XID back if the transaction is not recognised.  This
                // happens when the RM has recorded its prepare vote, but the
                // TM has not recorded its prepare vote.
                try {
                    auditSendRollback(ourXid, getXAResourceInfo());
                    xarm.rollback(ourXid);
                    auditRollbackResponse(XAResource.XA_OK, ourXid, getXAResourceInfo());
                } catch (XAException xae) {
                    FFDCFilter.processException(xae, "com.ibm.tx.jta.impl.XARecoveryData.recover", "660", this);
                    final int errorCode = xae.errorCode;
                    auditRollbackResponse(errorCode, ourXid, getXAResourceInfo());
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "XAException: error code " + XAReturnCodeHelper.convertXACode(errorCode), xae);
                    // Force retry of recovery for retriable errors - ignore non-recoverable ones
                    // such as XAER_INVAL or XAER_PROTO
                    if ((errorCode == XAException.XAER_RMFAIL) ||
                        (errorCode == XAException.XAER_RMERR)) {
                        if (tc.isDebugEnabled())
                            Tr.debug(tc, "Forcing retry of recovery");
                        _recovered = false;
                    }
                }
            }
        } // end for y

        //
        // Now we have finished with the XAResource we can destroy it
        //
        xarm.closeConnection();

        if (_recovered)
            decrementCount(); // recovery good

        if (tc.isEntryEnabled())
            Tr.exit(tc, "recover", new Boolean(_recovered));

        return _recovered;
    }

    /**
     * Removes all non-WAS Xids from an array of Xids, and puts them
     * in an ArrayList structure.
     * 
     * @param xidArray An array of generic Xids.
     * @return An ArrayList of XidImpl objects.
     */
    protected ArrayList filterXidsByType(Xid[] xidArray) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "filterXidsByType", xidArray);

        final ArrayList<XidImpl> xidList = new ArrayList<XidImpl>();

        if (xidArray != null) {
            /*----------------------------------------------------------*/
            /* Iterate over the list of returned xids, and insert them */
            /* into a new list containing all the xids that have been */
            /* recovered thus far. We don't have to worry about */
            /* duplicates because every resource is guaranteed to have a */
            /* unique bqual + gtrid combination. */
            /*----------------------------------------------------------*/
            for (int y = 0; y < xidArray.length; y++) {
                // PQ56777 - Oracle can return entries with null in them.
                // normally(?) it will be the last in the list.  It
                // appears to happen if an indoubt transaction on the
                // database completes during our call to recover.
                if (xidArray[y] == null) {
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "RM has returned null inDoubt Xid entry - " + y);
                    continue;
                }

                /*------------------------------------------------------*/
                /* We only want to add this Xid to our list if it is */
                /* one that we generated. */
                /*------------------------------------------------------*/
                if (XidImpl.isOurFormatId(xidArray[y].getFormatId())) {
                    /*--------------------------------------------------*/
                    /* It is possible that the Xid we get back from the */
                    /* RM is actually an instance of our XidImpl. In */
                    /* the case that it's not, we have to re-construct */
                    /* the XidImpl so that we can extract the xid bytes. */
                    /*--------------------------------------------------*/
                    XidImpl ourXid = null;
                    if (xidArray[y] instanceof XidImpl) {
                        ourXid = (XidImpl) xidArray[y];
                    } else {
                        ourXid = new XidImpl(xidArray[y]);
                        // Check the bqual is one of ours...
                        // as V5.1 also uses the same formatId but with
                        // different length encoding
                        if (ourXid.getBranchQualifier().length != XidImpl.BQUAL_JTA_BQUAL_LENGTH) {
                            if (tc.isDebugEnabled())
                                Tr.debug(tc, "Xid is wrong length - " + y);
                            continue;
                        }
                    }
                    xidList.add(ourXid);
                } /* if isOurFormatId() */
            } /* for each Xid */
        } /* if xidArray != null */

        if (tc.isEntryEnabled())
            Tr.exit(tc, "filterXidsByType", xidList);

        return xidList;
    }

    /**
     * Removes all Xids from an ArrayList that don't have our cruuid in
     * their bqual. Assumes that all Xids are XidImpls.
     * 
     * @param xidList A list of XidImpls.
     * @param cruuid The cruuid to filter.
     * @param epoch The epoch number to filter.
     * @return An ArrayList of XidImpl objects.
     */
    protected ArrayList filterXidsByCruuidAndEpoch(ArrayList xidList,
                                                   byte[] cruuid,
                                                   int epoch) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "filterXidsByCruuidAndEpoch", new Object[] {
                                                                     xidList,
                                                                     cruuid,
                                                                     epoch });

        for (int x = xidList.size() - 1; x >= 0; x--) {
            final XidImpl ourXid = (XidImpl) xidList.get(x);
            final byte[] xidCruuid = ourXid.getCruuid();
            final int xidEpoch = ourXid.getEpoch();
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Trace other Cruuid: " + xidCruuid + ", or: " + Util.toHexString(xidCruuid));
                Tr.debug(tc, "Trace my Cruuid: " + cruuid + ", or: " + Util.toHexString(cruuid));
            }
            if ((!java.util.Arrays.equals(cruuid, xidCruuid))) {

                if (tc.isDebugEnabled())
                    Tr.debug(tc, "filterXidsByCruuidAndEpoch: cruuid is different: " + ourXid.getCruuid());

                xidList.remove(x);
            } else if (xidEpoch >= epoch) {

                if (tc.isDebugEnabled())
                    Tr.debug(tc, "filterXidsByCruuidAndEpoch: xid epoch is " + xidEpoch);

                xidList.remove(x);
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "filterXidsByCruuidAndEpoch", xidList);

        return xidList;
    }

    /**
     * Iterates over the list of known Xids retrieved from transaction
     * service, and tries to match the given javax.transaction.xa.Xid
     * with one of them.
     * 
     * @param ourXid The javax.transaction.xa.Xid we are trying to match.
     * @param knownXids The array of Xids that are possible matches.
     * @return true if we find a match, false if not.
     */
    protected boolean canWeForgetXid(XidImpl ourXid, Xid[] knownXids) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "canWeForgetXid", new Object[] {
                                                         ourXid,
                                                         knownXids });

        if (tc.isDebugEnabled()) {
            // We are only called if knownXids != null
            for (int i = 0; i < knownXids.length; i++) {
                Tr.debug(tc, "tx xid[" + i + "] " + knownXids[i]);
            }
        }

        boolean forgetMe = true;

        /*----------------------------------------------------------------*/
        /* Yank the parts of the JTA xid. The branch qualifier will be */
        /* the full JTA branch qualifier, and will need to be shortened */
        /* to the same length of the transaction bqual so that the array */
        /* compare has a chance to complete successfully. */
        /*----------------------------------------------------------------*/
        final int jtaFormatId = ourXid.getFormatId();
        final byte[] jtaGtrid = ourXid.getGlobalTransactionId();
        final byte[] fullJtaBqual = ourXid.getBranchQualifier();
        byte[] jtaBqual = null;

        /*----------------------------------------------------------------*/
        /* We have to separate the transaction gtrid and bqual for the */
        /* array compares. These are places to store these items. */
        /*----------------------------------------------------------------*/
        int txnFormatId;
        byte[] txnGtrid;
        byte[] txnBqual;

        /*----------------------------------------------------------------*/
        /* Iterate over all the known XIDs (if there are none, we won't */
        /* iterate over anything). */
        /*----------------------------------------------------------------*/
        int x = 0;
        while ((x < knownXids.length) && (forgetMe == true)) {
            /*------------------------------------------------------------*/
            /* If this is the first XID, shorten the JTA bqual to the */
            /* length of the transaction bqual. We are assuming here */
            /* that all the transaction bquals are the same length. If */
            /* they arent, for some reason, we will need to revisit this. */
            /*------------------------------------------------------------*/
            if (x == 0) {
                final int bqualLength = (knownXids[x].getBranchQualifier()).length;
                jtaBqual = new byte[bqualLength];
                System.arraycopy(fullJtaBqual, 0,
                                 jtaBqual, 0,
                                 bqualLength);
            }

            /*------------------------------------------------------------*/
            /* Separate the transaction XID into comparable units. */
            /*------------------------------------------------------------*/
            txnFormatId = knownXids[x].getFormatId();
            txnGtrid = knownXids[x].getGlobalTransactionId();
            txnBqual = knownXids[x].getBranchQualifier();

            /*------------------------------------------------------------*/
            /* Compare the individual parts of the XID for equality. If */
            /* they are equal, set a boolean value and we can stop */
            /* checking. */
            /*------------------------------------------------------------*/
            if ((jtaFormatId == txnFormatId) &&
                (java.util.Arrays.equals(jtaGtrid, txnGtrid)) &&
                (java.util.Arrays.equals(jtaBqual, txnBqual))) {
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, "Xid has been matched to a transaction:",
                             ourXid);
                }
                auditTransactionXid(ourXid, knownXids[x], getXAResourceInfo());
                forgetMe = false;
            }

            x++;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "canWeForgetXid", forgetMe);
        return forgetMe;
    }

    // Called once recovery has finished with this XARecoveryData object
    // We can clean up as recovery entries are not shared with runtime entries.
    @Override
    public synchronized boolean clearIfNotInUse() {
        boolean cleared = super.clearIfNotInUse();
        if (cleared) {
            // Entry has been recovered and removed from the log
            // gc the class loader etc.
            _recoveryClassLoader = null;
        }
        return cleared;
    }

    /**
     * Retrieves the recovery classloader
     * 
     * @return The recovery classloader
     */
    public ClassLoader getRecoveryClassLoader() /* @369064.2A */
    {
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "getRecoveryClassLoader", _recoveryClassLoader);
        }

        return _recoveryClassLoader;
    }

    public boolean supportsIsSameRM() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "supportsIsSameRM");
        if (tc.isEntryEnabled())
            Tr.exit(tc, "supportsIsSameRM", Boolean.valueOf(_supportsIsSameRM));

        return _supportsIsSameRM;
    }

    protected void auditXaRecover(Serializable xaResInfo) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "auditXaRecover", xaResInfo);

        if (auditRecovery) {
            Tr.audit(tc, "WTRN0151_REC_XA_RECOVER", getRMInfo(xaResInfo));
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "auditXaRecover");
    }

    protected void auditXaRecoverCount(Serializable xaResInfo, int rms, int ours) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "auditXaRecoverCount", new Object[] { xaResInfo, rms, ours });

        if (auditRecovery) {
            Tr.audit(tc, "WTRN0146_REC_XA_RECOVERED", new Object[] { rms, getRMInfo(xaResInfo), ours });
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "auditXaRecoverCount");
    }

    public String getRMInfo(Serializable xaResInfo) {
        if (xaResInfo instanceof DirectEnlistXAResourceInfo) {
            return ((DirectEnlistXAResourceInfo) xaResInfo).getXAResource().toString();
        } else {
            return xaResInfo.toString();
        }
    }

    protected void auditLateEpoch(XidImpl xid, Serializable xaResInfo) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "auditLateEpoch", new Object[] { xid, xaResInfo });

        if (auditRecovery) {
            Tr.audit(tc, "WTRN0147_REC_XID_LATE", new Object[] { xid.printOtid(), getRMInfo(xaResInfo), xid.getEpoch() });
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "auditLateEpoch");
    }

    protected void auditSendRollback(XidImpl xid, Serializable xaResInfo) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "auditSendRollback", new Object[] { xid, xaResInfo });

        if (auditRecovery) {
            Tr.audit(tc, "WTRN0148_REC_XA_ROLLBACK", new Object[] { xid.printOtid(), getRMInfo(xaResInfo) });
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "auditSendRollback");
    }

    protected void auditRollbackResponse(int code, XidImpl xid, Serializable xaResInfo) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "auditRollbackResponse", new Object[] { xid, xaResInfo });

        if (auditRecovery) {
            Tr.audit(tc, "WTRN0150_REC_XA_ROLLEDBACK", new Object[] { xid.printOtid(), getRMInfo(xaResInfo), XAReturnCodeHelper.convertXACode(code) });
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "auditRollbackResponse");
    }

    private void auditTransactionXid(XidImpl xid, Xid txnXid, Serializable xaResInfo) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "auditTransactionXid", new Object[] { xid, txnXid, xaResInfo });

        if (auditRecovery) {
            String txnid;
            if (txnXid instanceof XidImpl) {
                txnid = ((XidImpl) txnXid).printOtid();
            } else {
                txnid = txnXid.toString();
            }
            Tr.audit(tc, "WTRN0149_REC_XA_TRAN", new Object[] { xid.printOtid(), getRMInfo(xaResInfo), getTransactionId(txnXid), Util.printStatus(getTransactionStatus(txnXid)) });
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "auditTransactionXid");
    }

    protected int getTransactionStatus(Xid xid) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "getTransactionStatus", xid);

        if (tc.isDebugEnabled())
            Tr.debug(tc, "fsc, rm", new Object[] { _fsc, _fsc.getRecoveryManager() });
        // Get current list of recovering txns
        final TransactionImpl[] trans = (_fsc.getRecoveryManager().getRecoveringTransactions());
        // Go through list and look for a match.  We should find a match as we are called on the "recover" thread
        // and we have already found a match of the XID with a transaction XID.  Note: the XID matching is common
        // with ZOS which is why we need to go back and look for the TransactionImpl again.
        for (int i = 0; i < trans.length; i++) {
            // txnXid should be an XidImpl
            if (tc.isDebugEnabled())
                Tr.debug(tc, "Matching xid with ", trans[i]);
            if (xid.equals(trans[i].getXidImpl())) {
                int status = trans[i].getStatus();
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "getTransactionStatus", Util.printStatus(status));
                return status;
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "getTransactionStatus", "Failed to find a transaction - error");
        return Status.STATUS_UNKNOWN;
    }

    protected String getTransactionId(Xid xid) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "getTransactionId", xid);

        if (tc.isDebugEnabled())
            Tr.debug(tc, "fsc, rm", new Object[] { _fsc, _fsc.getRecoveryManager() });
        // Get current list of recovering txns
        final TransactionImpl[] trans = (_fsc.getRecoveryManager().getRecoveringTransactions());
        // Go through list and look for a match.  We should find a match as we are called on the "recover" thread
        // and we have already found a match of the XID with a transaction XID.  Note: the XID matching is common
        // with ZOS which is why we need to go back and look for the TransactionImpl again.
        for (int i = 0; i < trans.length; i++) {
            // txnXid should be an XidImpl
            if (tc.isDebugEnabled())
                Tr.debug(tc, "Matching xid with ", trans[i]);
            if (xid.equals(trans[i].getXidImpl())) {
                long id = trans[i].getLocalTID();
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "getTransactionId", id);
                return Long.toString(id);
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "getTransactionId", "Failed to find a transaction - error");
        return "null";
    }

    public boolean continuePropagatingXAResourceTimeout() {
        return _continuePropagatingXAResourceTimeout;
    }

    public boolean propagateXAResourceTransactionTimeout() {
        return _propagateXAResourceTransactionTimeout;
    }

    public void disablePropagatingXAResourceTimeout() {
        _propagateXAResourceTransactionTimeout = false;
    }
}