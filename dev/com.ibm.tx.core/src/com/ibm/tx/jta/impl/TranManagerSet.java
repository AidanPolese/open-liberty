package com.ibm.tx.jta.impl;
/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002,2013 */
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
/*  Date      Programmer  Defect         Description                                 */
/*  --------  ----------  ------         -----------                                 */
/*  02-11-21  awilkins    1507           JTS -> JTA. Thread local restructuring      */
/*  21/11/02  gareth      1481           Basic LPS implementation                    */
/*  25/11/02  hursdlg     1506           Compile error on perf metrics again         */
/*  25/11/02  hursdlg     1503           Activate/deactivate/shutdown                */
/*  25/11/02  awilkins    1513           Repackage ejs.jts -> ws.Transaction         */
/*  26/11/02  gareth      1482           Implement PMI statistics                    */
/*  02/12/02  awilkins    1526           setRollbackOnly; no SystemException         */
/*  05/12/02  awilkins    1535           getTxType moved to UOWCoordinator           */
/*  02-12-08  irobins     LIDB1673.2     Added completeTxTimeout                     */
/*  13/12/02  mallam      LIDB1673.18    Synchronous completion for usertrans        */
/*  13-12-02  awilkins    LIDB1673.17    Embedded RAR recovery                       */
/*  17/12/02  mallam      LIDB1673.xx    Further changes for passive timeout         */
/*  17/01/03  awilkins    LIDB1673.9     Tidy-up exception handling                  */
/*  30/01/03  mallam      LIDB1673.24    Inactivity timer                            */
/*  21/02/03  gareth      LIDB1673.19    Make any unextended code final              */
/*  18/07/03  johawkes    LIDB2110.12    JCA 1.5                                     */
/*  25/07/03  hursdlg     172471         RegisterResourceInfo in non-active svce     */
/*  03-08-08  beavenj     173275         Commit/RB now pass tran UOWCoord to C/B     */  
/*  01/10/03  johawkes    178208.1       Use log generated recovery ids              */
/*  22/10/03  johawkes    180487         Fix trace                                   */
/*  20/11/03  johawkes    182862         Remove static partner log dependencies      */
/*  24/11/03  awilkins    183479         Synchronization tiers                       */
/*  25/11/03  hursdlg     182746         Check for null UOWCoord before call C/B     */
/*  27/11/03  johawkes    178502         Start an RA during XA recovery              */
/*  30/11/03  hursdlg     LIDB2775       Move callbacks to impl                      */
/*  05/12/03  johawkes    184903         Refactor PartnerLogTable                    */
/*  07/01/04  johawkes    LIDB2110       RA Uninstall                                */
/*  05/02/04  mallam      LIDB2775       Remove synchronous completion               */
/*  18/02/04  hursdlg     LIDB2775       Move LPS to registeredresources             */
/*  25/03/04  johawkes    195344.1       Stop logging JCAProvider on registerAS      */
/*  13/04/04  beavenj     LIDB1578.1     Initial supprort for ha-recovery            */     
/*  20/04/04  johawkes    199358         Add classpath to registerActivationSpec     */
/*  21/04/04  awilkins    LIDB3133-23.4  Empty impls of suspend/resumeUOW            */
/*  22/04/04  beavenj     LIDB1578.4     Early logging support for CScopes           */
/*  27/04/04  mallam      197039         Prolong finish for heuristic on recovery    */
/*  14/05/04  awilkins    203382         UOWToken repacked to com.ibm.ws.uow         */
/*  14-05-04  awilkins    202175         UOWScopeCallback work                       */
/*  15/06/04  johawkes    209345         Remove unnecessary code                     */
/*  21/06/04  johawkes    199785         Fix partner log corruption on shutdown      */
/*  06/07/04  johawkes    213406         Allow completion during quiesce             */
/*  27/07/04  johawkes    219412         Fix shutdown for JCA imported transactions  */
/*  28/06/04  awilkins    227752.2       Notify runtime when ready for new work      */
/*  12-10-04  awilkins    227752.4       Notify runtime of failure in asynch startup */
/*  14-12-04  hursdlg     246070         Revert to logEarly for normal PLD usage     */
/*  07-19-05  mezarin     LI3187         z/OS support for CR calls to syncpoints     */
/*  27/10/05  johawkes    316435.1       getGlobalGlobalID                           */
/*  08/11/05  mezarin     322331         Abstract before and after completion calls  */
/*  06/01/06  johawkes    306998.12      Use TraceComponent.isAnyTracingEnabled()    */
/*  07/05/01  johawkes    434414         Remove WAS dependencies                     */
/*  07/05/03  johawkes    436180         Remove temporary applid generation          */
/*  07/05/16  johawkes    438575         Further componentization                    */
/*  07/06/05  johawkes    443467         Move XAResourceInfo                         */
/*  07/06/05  hursdlg     LI3968-1.1     registerResourceInfo priority               */
/*  07/06/17  johawkes    444613         Repackaging                                 */
/*  26/06/17  johawkes    446894         Fix JTM shutdown delay                      */
/*  30/06/10  hursdlg     LI3968-1.2     Update registerResourceInfo for priority    */
/*  03/07/07  johawkes    446894.1       Added JTM STOPPING state                    */
/*  06/08/07  johawkes    451213.1       registerJCAProvider and quiesce came back   */
/*  29/08/07  johawkes    461798         Stronger typing for self                    */
/*  15/02/08  kaczyns     512190         Handle SystemException on begin             */
/*  10/06/08  hursdlg     PK66133.1      Change use of log early flag on rri         */
/*  14/01/09  irobins     569929.1       Clear cached objects when TM recycled       */
/*  02/06/09  mallam      596067         package move                                */
/*  09-08-19  mallam      602532.3       ltc bundle                                  */
/*  09-12-08  johawkes    631451         Cleanup instantiation                       */
/*  09-12-22  mallam      633225         Changes for Aries/Liberty osgi services      */
/*  11-10-18  johawkes    719671         UOWEventListener support                    */
/*  25/02/13  johawkes    744928         Add begin(timeout)                          */
/* ********************************************************************************* */

import java.io.Serializable;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

import com.ibm.tx.TranConstants;
import com.ibm.tx.jta.*;
import com.ibm.tx.util.TMHelper;
import com.ibm.tx.jta.util.TxTMHelper;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.ws.Transaction.JTA.JTAResource;
import com.ibm.ws.Transaction.JTS.Configuration;
import com.ibm.ws.Transaction.UOWCallback;
import com.ibm.ws.Transaction.UOWCoordinator;
import com.ibm.ws.Transaction.UOWCurrent;
import com.ibm.wsspi.tx.UOWEventListener;


/** A singleton class that delegates method calls to a
 *  thread local transaction manager.
 *  The class is also responsible for the registration
 *  of two varieties of callbacks: context change
 *  callbacks and unit of work callbacks.
 */
public class TranManagerSet implements ExtendedTransactionManager, UOWCurrent
{
    private static final TraceComponent tc=Tr.register(TranManagerSet.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    protected static ExtendedTransactionManager _instance;

    protected volatile boolean _replayComplete;

    protected volatile boolean _quiesced;    

    protected static ThreadLocal<?> _thread;

    protected TranManagerSet() {}

    public synchronized static ExtendedTransactionManager instance()
    {
        if (_instance == null)
        {
            _instance = new TranManagerSet();
            _thread = new ThreadLocal<TranManagerImpl>()
            {
                public TranManagerImpl initialValue()
                {
                    return new TranManagerImpl();
                }
            };
            
        }

        return _instance;
    }

    protected TranManagerImpl self()
    {
        return (TranManagerImpl)_thread.get();
    }

    public void cleanup()
    {
    	if (_thread != null)
    		_thread.remove();
    }

    public void begin() throws NotSupportedException, SystemException /* @512190C*/
    {
        TMHelper.checkTMState();

        self().begin();
    }

    public void begin(int timeout) throws NotSupportedException, SystemException
    {
        TMHelper.checkTMState();

        self().begin(timeout);
    }

    /**
     * Used by UserTransaction to create a transaction
     */
    public void beginUserTran() throws NotSupportedException, SystemException /* @512190C*/
    {
        TMHelper.checkTMState();

        self().beginUserTran();
    }

    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException
    {
        self().commit();
    }

    public int getStatus()
    {
        return self().getStatus();
    }

    public Transaction getTransaction()
    {
        return self().getTransaction();
    }

    public TransactionImpl getTransactionImpl()
    {
        return self().getTransactionImpl();
    }

    public void resume(Transaction tran) throws InvalidTransactionException, IllegalStateException
    {
        self().resume(tran);
    }

    public void rollback() throws IllegalStateException, SecurityException, SystemException
    {
        self().rollback();
    }

    public void setRollbackOnly() throws IllegalStateException
    {
        self().setRollbackOnly();
    }

    public void setTransactionTimeout(int timeout) throws SystemException
    {
        self().setTransactionTimeout(timeout);
    }

    public Transaction suspend()
    {
        return self().suspend();
    }

    public boolean delist(XAResource xaRes, int flag)
    {
        return self().delist(xaRes, flag);
    }

    public void replayComplete(boolean localFailureScope)
    {
        if (tc.isDebugEnabled()) Tr.debug(tc, "replayComplete", localFailureScope);
        
        if (localFailureScope)
        {
            TMHelper.asynchRecoveryProcessingComplete(null);            
        }

        _replayComplete = true;
    }

    public boolean isReplayComplete()
    {
        if (tc.isDebugEnabled()) Tr.debug(tc, "isReplayComplete", _replayComplete);
        return _replayComplete;
    }



    public int registerResourceInfo(String xaResFactoryClassName, Serializable xaResInfo)
    {
        return registerResourceInfo(xaResFactoryClassName, xaResInfo, JTAResource.DEFAULT_COMMIT_PRIORITY);
    }

    public int registerResourceInfo(String xaResFactoryClassName, Serializable xaResInfo, int priority)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "registerResourceInfo", new Object[]{xaResFactoryClassName, xaResInfo, priority, this});

        try
        {
            TMHelper.checkTMState();         // start the TX service if not already
        }
        catch (NotSupportedException ex)
        {
            if (tc.isDebugEnabled()) Tr.debug(tc, "registerResourceInfo: checkTMState failed: ", ex);
        }

        int index = -1;
        if (xaResFactoryClassName != null && xaResFactoryClassName.length() != 0 && TxTMHelper.ready())
        {
            final PartnerLogTable plt = Configuration.getFailureScopeController().getPartnerLogTable();

            if (plt != null) // ie if the core TM is not stopped
            {
                final XARecoveryWrapper xaWrapper = new XARecoveryWrapper(xaResFactoryClassName, xaResInfo, null, priority);

                // Ensure this wrapper is in the cache
                final PartnerLogData pld = plt.findEntry(xaWrapper);

                // Could have been set terminating if this is an unregistered activationspec
                pld._terminating = false;

                index = pld.getIndex();
            }
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "registerResourceInfo", index);
        return index;
    }

    /* Called by MessageEndpointHandler */
    public boolean enlist(XAResource xaRes, int recoveryId)
    throws RollbackException, IllegalStateException, SystemException
    {
        return self().enlist(xaRes, recoveryId);
    }

    public PartnerLogData registerJCAProvider(String providerId)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "registerJCAProvider", providerId);

        final PartnerLogData pld;

        final JCARecoveryWrapper jcarw = new JCARecoveryWrapper(providerId);

        // Ensure this wrapper is in the cache
        final PartnerLogTable plt = Configuration.getFailureScopeController().getPartnerLogTable();
        pld = plt.findEntry(jcarw);

        if (tc.isEntryEnabled()) Tr.exit(tc, "registerJCAProvider", pld);
        return pld;
    }

    /**
     * 
     */
    public void quiesce()
    {
        _quiesced = true;
    }
    
    /**
     * @return
     */
    public boolean isQuiesced()
    {
        return _quiesced;
    }

    //
    // UOWCurrent interface
    //
    public UOWCoordinator getUOWCoord()
    {
        return self().getUOWCoord();
    }
 
    public int getUOWType()
    { 
        if (tc.isEntryEnabled()) Tr.entry(tc, "getUOWType");
 
        final UOWCoordinator uowCoord = getUOWCoord();
 
        int result = UOWCurrent.UOW_NONE;
 
        if (uowCoord != null)
        {
            result = uowCoord.isGlobal() ? UOWCurrent.UOW_GLOBAL : UOWCurrent.UOW_LOCAL;
        }
 
        if (tc.isEntryEnabled()) Tr.exit(tc, "getUOWType", result);
        return result;
    }    

    public void registerLTCCallback(UOWCallback callback)
    { 
        if (tc.isEntryEnabled()) Tr.entry(tc, "registerLTCCallback", callback);
 
        // No support for LTCCallbacks ... provided by derived classes

        if (tc.isEntryEnabled()) Tr.exit(tc, "registerLTCCallback");
    }

	@Override
	public void setUOWEventListener(UOWEventListener el)
	{
		self().setUOWEventListener(el);	
	}

	@Override
	public void unsetUOWEventListener(UOWEventListener el)
	{
		self().unsetUOWEventListener(el);	
	}
}