package com.ibm.tx.jta.impl;

/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/*  ORIGINS: 27                                                                                          */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70(C) COPYRIGHT International Business Machines Corp. 1997, 2013 */
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
/*  Date      Programmer        Defect    Description                                                    */
/*  --------  ----------        ------    -----------                                                    */
/*  02-02-06  awilkins          115601-2  extend WSResourceImplBase                                      */
/*  02-02-07  hursdlg           111021    reduce log record size                                         */
/*  25/02/02  beavenj           LI1436.01 RAS Updates                                                    */
/*  02-02-25  hursdlg           111021.1  log resource recovery data                                     */
/*  27-02-02  beavenj           LIDB1220.151.1 Code instrumented for FFDC work                           */
/*  02-02-28  hursdlg           LI1169.1  trace recovery actions                                         */
/*  04-03-02  beavenj           LIDB1220.151 Add state access methods                                    */
/*  18-03-02  mallam            116917     Improve diagnostics for XAException                           */
/*  02-05-08  awilkins          126224    Unreg res after RMFAIL from rollback                           */
/*  02-05-13  awilkins          126658    Don't log until prepare time                                   */
/*  02-05-27  awilkins          131059    Improve XAException handling                                   */
/*  02-07-02  hursdlg           137510    Correct XAException from reconnect                             */
/*  05/09/02   gareth           ------    Move to JTA implementation                                     */
/*  23/09/02  hursdlg           ------    Partner log support                                            */
/*  11/10/02  awilkins          1452      XAException handling                                           */
/*  14/10/02  hursdlg           1454      XAException handling                                           */
/*  18/10/02  hursdlg           1433      Exception from getXAResource fix                               */
/*  25/11/02  awilkins          1513      Repackage ejs.jts -> ws.Transaction                            */
/*  27/01/03  hursdlg        LIDB1673.9.1 XARecoveryData changes                                         */
/*  21/02/03  gareth         LIDB1673.19  Make any unextended code final                                 */
/*  14/04/03  hursdlg          163130     Correct message                                                */
/*  22/04/03  hursdlg          163130     Throw non-RB exception on prepare                              */
/*  30/06/03  hursdlg          170434     Log partner XA data at enlist time                             */
/*  15/07/03   mallam       171151    Rollback using TMSUCCESS (TMFAIL on t/o)                           */
/*  13/08/03  hursdlg       174113    Reorder recovery                                                   */
/*  28/08/03  johawkes      174516    Distribute all ends before prepares                                */
/*  15/09/03  hursdlg       176247    XAResourceManager only used in recovery                            */
/*  18/09/03  hursdlg       177194    Migrate to 8 byte recovery ids                                     */
/*  18/09/03  johawkes      169114    Improve diags for prep/cmplt XA errors                             */
/*  20/11/03  johawkes      182862    Remove static partner log dependencies                             */
/*  30/12/03  hursdlg       LIDB2775  Utility functions                                                  */
/*  07/01/04  johawkes      LIDB2110  RA Uninstall                                                       */
/*  10/02/04  hursdlg       190239    Update JTAResource states                                          */
/*  10/02/04  hursdlg       LIDB2775  Use zOS bqual format                                               */
/*  04/03/04  johawkes      191316    Log resources when setting LPS state                               */
/*  25/03/04  hursdlg       196258    Make recovery get new connection                                   */
/*  31/03/04  johawkes      196310    Handle null from getXAResource()                                   */
/*  04/04/04  johawkes      196588    Set recovery classloader on thread                                 */
/*  13/04/04  beavenj       LIDB1578.1 Initial supprort for ha-recovery                                  */
/*  19/04/04  johawkes      193919.1  New methods for adminconsole                                       */
/*  22/04/04  awilkins      198904.1  getXid changes                                                     */
/*  22/04/04  hursdlg       199500    Remove excess debugging                                            */
/*  22/04/04  beavenj       LIDB1578.4 Early logging support for CScopes                                 */
/*  21/05/04  beavenj       LIDB1578.7 FFDC                                                              */
/*  27/05/04  johawkes      204546    Add getXAResourceInfo() method                                     */
/*  28/05/04  johawkes      204553    Check for null resource on forget()                                */
/*  15/06/04  johawkes      209345    Remove unnecessary code                                            */
/*  25/06/04  johawkes      199785    Fix partner log corruption on shutdown                             */
/*  01/07/04  hursdlg       213889    Interface changes                                                  */
/*  19/08/04  johawkes      224215    Detect uninstalled providers better                                */
/*  20/09/04  hursdlg       233078    Log more information                                               */
/*  14/06/05  hursdlg       283253    Componentization changes for recovery/retry                        */
/*  06/01/06  johawkes      306998.12 Use TraceComponent.isAnyTracingEnabled()                           */
/*  24/11/06  johawkes      396115    Fix inefficient error path on XAER_RMFAIL                          */
/*  01/05/07  johawkes      434414    Remove WAS dependencies                                            */
/*  09/05/07  hursdlg       419307    Reconnect resource after failure before issuing forget             */
/*  30/05/07  hursdlg       396035    Set vote on recovery and set RCs for FFDC                          */
/*  05/06/07  johawkes      443467    Move XAResourceInfo                                                */
/*  20/06/07  hursdlg       LI3968-1.2 Resource commit priority                                          */
/*  13/07/07  hursdlg       451941    Set priority for lastInCommitPhase cases                           */
/*  16/08/07  johawkes      451213    Move LPS back into JTM                                             */
/*  28/01/08  kaczyns       493829    Stop logging recovery ID in z/OS XID bqual                         */
/*  17/07/08  johawkes      536926    Remove JET dependencies on org.omg classes                         */
/*  02/06/09  mallam        596067    package move                                                       */
/*  09/06/09  brailsfo      PK87126   Handle exception in reconnectRM                                    */
/*  17/03/10  hursdlg       PM07874   Audit recovery information                                         */
/*  09/06/10  hursdlg       656080    Revisit PK87126 exception handling                                 */
/*  10/08/13  slaterpa      752004    TRANSUMMARY trace                                                  */
/* ***************************************************************************************************** */

import java.io.Serializable;

import javax.transaction.SystemException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.ibm.tx.TranConstants;
import com.ibm.tx.util.logging.FFDCFilter;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.ws.Transaction.JTA.JTAResourceBase;
import com.ibm.ws.Transaction.JTA.JTAXAResource;
import com.ibm.ws.Transaction.JTA.Util;
import com.ibm.ws.Transaction.JTA.XAReturnCodeHelper;
import com.ibm.ws.Transaction.JTA.XARminst;
import com.ibm.ws.recoverylog.spi.RecoverableUnitSection;

/**
 * An implementation of an XA resource
 * to support X/Open XA compliant resource managers.
 */
public class JTAXAResourceImpl extends JTAResourceBase implements JTAXAResource
{
    //
    // completed if commit, rollback, or forget has finished.
    //
    private boolean _completed;

    private XARminst _recoveredRM;

    protected XARecoveryData _recoveryData;

    private boolean _recovery;

    private Integer _priority;

    private static final TraceComponent tc = Tr.register(JTAXAResourceImpl.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);
    private static final TraceComponent tcSummary = Tr.register("TRANSUMMARY", TranConstants.SUMMARY_TRACE_GROUP, null);

    /**
     * Construct an JTAXAResource object.
     * 
     * @param xid the xid assigned to the XA resource.
     * @param resource the XA resource enlisted in the transaction.
     * @param recoveryData the XA resource recovery data associated with the XA resource.
     */
    public JTAXAResourceImpl(Xid xid, XAResource resource, XARecoveryData recoveryData)
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "JTAXAResourceImpl", new Object[] { xid, resource, recoveryData });
        _xid = xid;
        _resource = resource;
        _recoveryData = recoveryData;
        traceCreate();
        if (tc.isEntryEnabled())
            Tr.exit(tc, "JTAXAResourceImpl", this);
    }

    /**
     * Construct an JTAXAResource object during recovery from a log record
     * 
     * @param plt the partner log table associated with recovery
     * @param tid the global transaction identifier of the recovered transaction
     * @param logData the resource specific log data from recovery
     */
    public JTAXAResourceImpl(PartnerLogTable plt, byte[] tid, byte[] logData) throws Exception
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "JTAXAResourceImpl",
                     new Object[] { plt, Util.toHexString(tid), Util.toHexString(logData) });

        final byte[] stoken = Util.duplicateByteArray(logData, 0, 8);
        final int recoveryId = Util.getIntFromBytes(logData, 8, 4);
        final int seqNo = Util.getIntFromBytes(logData, 12, 2);

        if (tc.isDebugEnabled())
        {
            Tr.debug(tc, "recovered stoken is " + Util.toHexString(stoken));
            Tr.debug(tc, "recovered recoveryId is " + recoveryId);
            Tr.debug(tc, "recovered seqNo is " + seqNo);
        }

        // Check the partner log table for this recoveryId
        final PartnerLogData pld = plt.findEntry(recoveryId);

        if (pld instanceof XARecoveryData)
        {
            _recoveryData = (XARecoveryData) pld;

            pld.incrementCount(); // bump up recovery in use count

            // Build the XID from the log record and the tid. 
            _xid = new XidImpl(tid, seqNo, stoken);

            // Mark the resource as failed so it reconnects on use
            _state = FAILED;
            _recovery = true;
            _vote = JTAResourceVote.commit; // set for error messages if failure
        }
        else
        {
            throw new Exception("Invalid Xid/recoveryId in transaction log");
        }

        traceCreate();

        if (tc.isEntryEnabled())
            Tr.exit(tc, "JTAXAResourceImpl", this);
    }

    /**
     * Prepare a transaction.
     * 
     * <p>This is the first phase of the two-phase commit protocol.
     * 
     * @return int indicating vote response
     * @exception XAException
     */
    @Override
    public final int prepare() throws XAException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "prepare", new Object[] { _resource, _xid });
        if (tcSummary.isDebugEnabled())
            Tr.debug(tcSummary, "xa_prepare", this);

        int rc = -1;

        try
        {
            try
            {
                // Ensure that the associated XARecoveryData (the PartnerLogData entry in the PartnerLogTable)
                // has logged itself to the partner log. If the PLD was created before the recovery log
                // was available we need to ensure that it writes itself to the parter log now. Additionally,
                // if this was the case, the recovery id it holds is updated from zero to the real value 
                // so we need to update the value cached in this object (originally obtained during contruction
                // from the pre-logged PLD).  If we logged at enlist time, this is a no-op.
                _recoveryData.logRecoveryEntry();
            } catch (Exception e)
            {
                throw new XAException(XAException.XAER_INVAL);
            }

            rc = _resource.prepare(_xid);

            //
            // Convert to Vote.
            //
            if (rc == XAResource.XA_OK)
            {
                // Record the Vote
                _vote = JTAResourceVote.commit;
                return rc;
            }
            else if (rc == XAResource.XA_RDONLY)
            {
                // Record the Vote
                _vote = JTAResourceVote.readonly;
                destroy();
                return rc;
            }
        } catch (XAException xae)
        {
            _prepareXARC = xae.errorCode;
            // Record the prepare XA return code
            FFDCFilter.processException(xae, "com.ibm.ws.Transaction.JTA.JTAXAResourceImpl.prepare", "259", this);

            if (_prepareXARC >= XAException.XA_RBBASE && _prepareXARC <= XAException.XA_RBEND)
            {
                _vote = JTAResourceVote.rollback;
            }
            else if (_prepareXARC == XAException.XAER_RMFAIL)
            {
                // Force reconnect on rollback
                _state = FAILED;
            }

            throw xae;
        } finally
        {
            if (tc.isEntryEnabled())
            {
                if (_vote != null)
                {
                    Tr.exit(tc, "prepare", XAReturnCodeHelper.convertXACode(rc) + " (" + _vote.name() + ")");
                }
                else
                {
                    Tr.exit(tc, "prepare", XAReturnCodeHelper.convertXACode(rc));
                }
            }

            if (tcSummary.isDebugEnabled())
                Tr.debug(tcSummary, "xa_prepare result: " +
                                    XAReturnCodeHelper.convertXACode(rc));
        }

        // Any other response is invalid
        throw new XAException(XAException.XAER_INVAL);
    }

    /**
     * Commit a transaction.
     * 
     * @exception XAException
     */
    @Override
    public final void commit() throws XAException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "commit", new Object[] { _resource, _xid, getPriority() });
        if (tcSummary.isDebugEnabled())
            Tr.debug(tcSummary, "xa_commit", this);
        if (tc.isDebugEnabled())
            Tr.debug(tc, "Committing resource with priority " + getPriority());

        try {
            if (_state == FAILED)
                _resource = reconnectRM();

            _resource.commit(_xid, false);

            // Record the completion direction
            _completedCommit = true;

            destroy();
        } catch (XAException xae) {
            _completionXARC = xae.errorCode;
            // Record the completion XA return code
            FFDCFilter.processException(xae, "com.ibm.ws.Transaction.JTA.JTAXAResourceImpl.commit", "317", this);
            throw xae;
        } finally {
            if (tc.isEntryEnabled())
                Tr.exit(tc, "commit");
            if (tcSummary.isDebugEnabled())
                Tr.debug(tcSummary, "xa_commit result: " +
                                    XAReturnCodeHelper.convertXACode(_completionXARC));
        }
    }

    /**
     * Commit a transaction, using one-phase optimization.
     * 
     * @exception XAException
     */
    @Override
    public final void commit_one_phase() throws XAException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "commit_one_phase", new Object[] { _resource, _xid });
        if (tcSummary.isDebugEnabled())
            Tr.debug(tcSummary, "commit_one_phase", this);

        try
        {
            _resource.commit(_xid, true);

            // Record the completion direction and Automatic vote.
            _completedCommit = true;
            _vote = JTAResourceVote.commit;

            destroy();
        } catch (XAException xae)
        {
            _completionXARC = xae.errorCode;
            // Record the completion XA return code
            FFDCFilter.processException(xae, "com.ibm.ws.Transaction.JTA.JTAXAResourceImpl.commit_one_phase", "354", this);
            throw xae;
        } finally
        {
            if (tc.isEntryEnabled())
                Tr.exit(tc, "commit_one_phase", _completionXARC);
            if (tcSummary.isDebugEnabled())
                Tr.debug(tcSummary, "commit_one_phase result: " +
                                    XAReturnCodeHelper.convertXACode(_completionXARC));
        }
    }

    /**
     * Rollback a transaction.
     * 
     * @exception XAException
     */
    @Override
    public final void rollback() throws XAException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "rollback", new Object[] { _resource, _xid });
        if (tcSummary.isDebugEnabled())
            Tr.debug(tcSummary, "xa_rollback", this);

        try
        {
            if (_state == FAILED)
                _resource = reconnectRM();

            _resource.rollback(_xid);
            destroy();
        } catch (XAException xae)
        {
            _completionXARC = xae.errorCode;
            // Record the completion XA return code
            FFDCFilter.processException(xae, "com.ibm.ws.Transaction.JTA.JTAXAResourceImpl.rollback", "386", this);
            throw xae;
        } finally
        {
            if (tc.isEntryEnabled())
                Tr.exit(tc, "rollback");
            if (tcSummary.isDebugEnabled())
                Tr.debug(tcSummary, "xa_rollback result: " +
                                    XAReturnCodeHelper.convertXACode(_completionXARC));
        }
    }

    /**
     * The resource manager can forget all knowledge of the transaction.
     * 
     * @exception XAException
     */
    @Override
    public final void forget() throws XAException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "forget", new Object[] { _resource, _xid });
        if (tcSummary.isDebugEnabled())
            Tr.debug(tcSummary, "xa_forget", this);

        int rc = -1; // not an XA RC
        try
        {
            if (!_completed && _resource != null)
            {
                if (_state == FAILED)
                    _resource = reconnectRM(); // D419307A

                _resource.forget(_xid);
                rc = XAResource.XA_OK;
                destroy();
            }
        } catch (XAException xae)
        {
            rc = xae.errorCode;
            throw xae;
        } finally
        {
            if (tc.isEntryEnabled())
                Tr.exit(tc, "forget");
            if (tcSummary.isDebugEnabled())
                Tr.debug(tcSummary, "xa_rollback result: " +
                                    XAReturnCodeHelper.convertXACode(rc));
        }
    }

    /**
     * Write information about the resource to the transaction log
     * 
     * @exception SystemException
     */
    @Override
    public void log(RecoverableUnitSection rus) throws SystemException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "log", new Object[] { this, rus });

        if (tc.isDebugEnabled())
        {
            Tr.debug(tc, "about to log stoken " + Util.toHexString(((XidImpl) _xid).getStoken()));
            Tr.debug(tc, "about to log recoveryId " + getRecoveryId());
            Tr.debug(tc, "about to log seqNo " + ((XidImpl) _xid).getSequenceNumber());

            Tr.debug(tc, "ID from pld " + _recoveryData._recoveryId);
        }

        // Log the stoken, recoveryId and the sequence number
        final byte[] stoken = ((XidImpl) _xid).getStoken();
        final int recoveryId = (int) getRecoveryId();
        final int seqNo = ((XidImpl) _xid).getSequenceNumber();
        final byte[] data = new byte[stoken.length + 6];
        System.arraycopy(stoken, 0, data, 0, stoken.length);
        Util.setBytesFromInt(data, stoken.length, 4, recoveryId);
        Util.setBytesFromInt(data, stoken.length + 4, 2, seqNo);

        if (tc.isDebugEnabled())
        {
            Tr.debug(tc, "logging stoken " + Util.toHexString(stoken));
            Tr.debug(tc, "logging recoveryId " + recoveryId);
            Tr.debug(tc, "logging seqNo " + seqNo);
            Tr.debug(tc, "Actual data logged", Util.toHexString(data));
        }

        try
        {
            rus.addData(data);
        } catch (Exception exc)
        {
            FFDCFilter.processException(exc, "com.ibm.ws.Transaction.JTA.JTAXAResourceImpl.log", "326", this);
            if (tc.isEventEnabled())
                Tr.event(tc, "Exception raised adding data to the transaction log", exc);
            throw new SystemException(exc.toString());
        } finally
        {
            if (tc.isEntryEnabled())
                Tr.exit(tc, "log");
        }
    }

    @Override
    public long getRecoveryId()
    {
        if (_recoveryData != null)
        {
            return _recoveryData._recoveryId;
        }

        return -1;
    }

    @Override
    public int getPriority()
    {
        if (_priority != null) {
            return _priority.intValue();
        } else if (_recoveryData != null) {
            return _recoveryData.getPriority();
        }

        return DEFAULT_COMMIT_PRIORITY;
    }

    public boolean getCompleted()
    {
        return _completed;
    }

    /**
     * Destroy the JTAXAResourceImpl object from XAResourceManager.
     */

    @Override
    public final void destroy()
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "destroy", new Object[] { _resource, _xid });

        if (!_completed)
        {
            // If we created an XAResource by reconnectRM we need to destroy it
            if (_recoveredRM != null)
            {
                _recoveredRM.closeConnection();
                _recoveredRM = null;
                if (_recovery)
                {
                    if (_recoveryData != null)
                        _recoveryData.decrementCount();
                }
            }
            _completed = true;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "destroy");
    }

    /**
     * Reconnect to RM if previous xa commit/rollback fails because of XAER_RMFAIL.
     * 
     * @exception XAException
     */
    XAResource reconnectRM() throws XAException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "reconnectRM");

        XAResource resource = null;

        XARecoveryWrapper wrapper = null;

        if (_recoveryData != null)
        {
            wrapper = _recoveryData.getXARecoveryWrapper();
        }

        if (wrapper == null && _recovery)
        {
            // We've not yet deserialized the log data - retry later "quietly"
            final XAException xae = new XAException(XAException.XA_RETRY);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "reconnectRM", xae);
            throw xae;
        }

        // If we have already created an XAResource by this method we need to destroy it
        if (_recoveredRM != null)
        {
            _recoveredRM.closeConnection();
            _recoveredRM = null;
        }

        if (wrapper != null)
        {
            try
            {
                _recoveredRM = _recoveryData.getXARminst();
            } catch (XAException e)
            {
                FFDCFilter.processException(e, "com.ibm.ws.Transaction.JTA.JTAXAResourceImpl.reconnectRM", "607", this);
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "reconnectRM", XAReturnCodeHelper.convertXACode(e.errorCode));
                XAException xae = e;
                // We will convert any RMERR from getXARminst to RMFAIL. We do not want to return RMERR on commit/rollback
                // unless it has come from the resource as that then implies completion and we can then end the tran. An error
                // trying to get the resource should not allow the tran to complete since we may leave a resource in-doubt.
                // The downside of this is that we may retry forever - so the user needs to zap the tran via the console.
                // We need to allow some heuristic error codes through if the RM has been installed.
                if (e.errorCode == XAException.XAER_RMERR)
                {
                    xae = (XAException) (new XAException(XAException.XAER_RMFAIL).initCause(e));
                }
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "reconnectRM", xae);
                throw xae;
            } catch (Throwable t) // @D666666C
            {
                // Catch throwable in case we get OOM etc as we want to retry in this case.
                FFDCFilter.processException(t, "com.ibm.ws.Transaction.JTA.JTAXAResourceImpl.reconnectRM", "524", this);
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "reconnectRM caught ", t);
                final XAException xae = (XAException) (new XAException(XAException.XAER_RMFAIL).initCause(t));
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "reconnectRM", xae);
                throw xae;
            }
            // If we successfully created an XARmist, then get the XAResource to work with
            if (_recoveredRM != null)
                resource = _recoveredRM.getXaResource();
            // Update state from FAILED to IDLE so we dont reconnect unnecessarily
            if (resource != null)
                _state = IDLE; // D419307A
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "reconnectRM", resource);
        return resource;
    }

    @Override
    public final String toString()
    {
        final String tail = (_xid == null) ? "" : "#" + _xid.toString() + ", priority=" + getPriority();
        return getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(this)) + tail;
    }

    /**
     * Override java.lang.Object equals method.
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o instanceof JTAXAResourceImpl && _resource != null)
            return this._resource.equals(((JTAXAResourceImpl) o)._resource);

        return super.equals(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.Transaction.JTA.JTAResource#describe()
     */
    @Override
    public String describe()
    {
        Serializable xaResInfo = getXAResourceInfo();
        if (xaResInfo == null)
        {
            return null;
        }
        else if (xaResInfo instanceof DirectEnlistXAResourceInfo)
        {
            return ((DirectEnlistXAResourceInfo) xaResInfo).getXAResource().toString();
        }
        else
        {
            return xaResInfo.toString();
        }
    }

    /**
     * @return
     */
    public Serializable getXAResourceInfo()
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "getXAResourceInfo");

        XARecoveryWrapper wrapper = null;

        if (_recoveryData != null)
        {
            wrapper = _recoveryData.getXARecoveryWrapper();
        }

        if (wrapper != null)
        {
            final Serializable xari = wrapper.getXAResourceInfo();
            if (tc.isEntryEnabled())
                Tr.exit(tc, "getXAResourceInfo", xari);
            return xari;
        }

        if (tc.isDebugEnabled())
            Tr.debug(tc, "getXAResourceInfo", "still no wrapper");
        if (tc.isEntryEnabled())
            Tr.exit(tc, "getXAResourceInfo", null);
        return null;
    }
}
