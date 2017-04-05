package com.ibm.tx.jta.impl;
/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2009 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  YY-MM-DD   Programmer  Defect      Description                                   */
/*  ---------  ----------  ------      -----------                                   */
/*  03-05-16   ehadley     -           Creation                                      */
/*  03-07-18   johawkes    LIDB2110.12 JCA 1.5                                       */
/*  03-07-28   johawkes    LIDB2110.12 Validate Xids                                 */
/*  03-08-18   johawkes    174376      Add isValid(Xid) for context handler          */
/*  03-08-19   johawkes    174593      Resume local tran on dissociate               */
/*  03-08-22   johawkes    174726      Allow null Xid on associate                   */
/*  03-09-25   johawkes    177245      Allow commit_one_phase during recovery        */
/*  03-09-25   johawkes    177208      Rename TransactionWrapper                     */
/*  03-09-30   johawkes    178038      Use local association methods                 */
/*  03-10-28   johawkes    181147      Make XATerminators specific to RA             */
/*  03-11-07   johawkes    182128      throw WCE when already associated             */
/*  27/11/03   johawkes    178502      Start an RA during XA recovery                */
/*  05/12/03   johawkes    184903      Refactor PartnerLogTable                      */
/*  07/01/04   johawkes    LIDB2110    RA Uninstall                                  */
/*  04/02/04   johawkes    189497      Pass recovery data on prepare                 */
/*  04/03/04   johawkes    191316      Log resources when setting LPS state          */
/*  17/03/04   johawkes    192653      Cancel timeouts on RA uninstall               */
/*  03/06/04   johawkes    207033      Delay registerJCAProvider                     */
/*  27/07/04   johawkes    219412      Fix shutdown for JCA imported transactions    */
/*  10/08/04   kaczyns     LIDB2110    z/OS JCA 1.5 support                          */
/*  11/01/05   hursdlg     249308      Allow zos formatids                           */
/*  02/02/05   mezarin     250302      z/OS check gtrid length when validating XID   */
/*  06/08/07   johawkes    451213.1    Moved into JTM                                */
/*  26/10/07   johawkes    463185      Static analysis                               */
/*  02/06/09   mallam      596067      package move                                  */
/*  10/30/09   mezarin     623196      Fix z/OS JCA recovery issues                  */
/* ********************************************************************************* */


import java.util.HashMap;

import javax.resource.spi.XATerminator;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import com.ibm.tx.jta.*;
import com.ibm.tx.TranConstants;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.ws.Transaction.JTA.Util;
import com.ibm.ws.Transaction.JTA.XAReturnCodeHelper;

/**
 * An implementation of the javax.resource.spi.XATerminator interface
 */
public class TxXATerminator implements XATerminator
{
    private static final TraceComponent tc = Tr.register(TxXATerminator.class,
                                                         TranConstants.TRACE_GROUP,
                                                         TranConstants.NLS_FILE);

	public static final HashMap<String, Object> _txXATerminators = new HashMap<String, Object>();

    // partner log entry for this provider
	private JCARecoveryData _JCARecoveryData;
    
    private final String _providerId;

	/**
	 * Construct a TxXATerminator instance specific to the given providerId
	 * 
     * @param providerId
     */
    protected TxXATerminator(String providerId)
	{
		if (tc.isEntryEnabled()) Tr.entry(tc, "TxXATerminator", providerId);
        
        _providerId = providerId;

		if (tc.isEntryEnabled()) Tr.exit(tc, "TxXATerminator", _providerId);
	}

	/**
	 * Return the TxXATerminator instance specific to the given providerId
	 * 
     * @param providerId
	 * @return
	 */
	public static TxXATerminator instance(String providerId)
	{
		if (tc.isEntryEnabled()) Tr.entry(tc, "instance", providerId);
		
		final TxXATerminator xat;
			
		synchronized(_txXATerminators)
		{
			if(_txXATerminators.containsKey(providerId))
			{
				xat = (TxXATerminator)_txXATerminators.get(providerId);
			}
			else
			{
				xat = new TxXATerminator(providerId);
				_txXATerminators.put(providerId, xat);
			}
		}

		if (tc.isEntryEnabled()) Tr.exit(tc, "instance", xat);
		return xat;
	}

    /* (non-Javadoc)
	 * @see javax.resource.spi.XATerminator#commit(javax.transaction.xa.Xid, boolean)
	 */
    public void commit(Xid xid, boolean onePhase) throws XAException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "commit", new Object[] {xid, onePhase});

		final JCATranWrapper txWrapper;
		
		try
		{
			validateXid(xid);
			
			// Get the wrapper adding an association in the process
			txWrapper = getTxWrapper(xid, true);
		}
		catch (XAException e)
		{
			if (tc.isEntryEnabled()) Tr.exit(tc, "commit", "caught XAException: " + XAReturnCodeHelper.convertXACode(e.errorCode));
			throw e;
		}

		try
		{
			if (onePhase)
			{
				// Perform one-phase commit
				txWrapper.commitOnePhase();
			}
			else
			{
				// Perform ordinary commit (prepare has already been called)
				txWrapper.commit();
			}
		}
		catch (XAException e)
		{
			TxExecutionContextHandler.removeAssociation(txWrapper);
			
			if (tc.isEntryEnabled()) Tr.exit(tc, "commit", "rethrowing XAException: " + XAReturnCodeHelper.convertXACode(e.errorCode));
			throw e;
		}

		TxExecutionContextHandler.removeAssociation(txWrapper);

		if (tc.isEntryEnabled()) Tr.exit(tc, "commit");
    }

    /* (non-Javadoc)
	 * @see javax.resource.spi.XATerminator#forget(javax.transaction.xa.Xid)
	 */
    public void forget(Xid xid) throws XAException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "forget", xid);

		try
		{
			validateXid(xid);

			final JCATranWrapper txWrapper = getTxWrapper(xid, false);

			txWrapper.forget();
		}
		catch (XAException e)
		{
			if (tc.isEntryEnabled()) Tr.exit(tc, "forget", new Object[] {"rethrowing XAException", e});
			throw e;
		}

		if (tc.isEntryEnabled()) Tr.exit(tc, "forget");
    }

    /* (non-Javadoc)
	 * @see javax.resource.spi.XATerminator#prepare(javax.transaction.xa.Xid)
	 */
    public int prepare(Xid xid) throws XAException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "prepare", xid);

		final JCATranWrapper txWrapper;

		try
		{
			validateXid(xid);

			// Get the wrapper adding an association in the process
			txWrapper = getTxWrapper(xid, true);
		}
		catch(XAException e)
		{
			if (tc.isEntryEnabled()) Tr.exit(tc, "prepare", new Object[] {"caught XAException", e});
			throw e;
		}

		final int result;
		
		try
		{
			result = txWrapper.prepare(this);
		}
		catch (XAException e)
		{
			TxExecutionContextHandler.removeAssociation(txWrapper);

			if (tc.isEntryEnabled()) Tr.exit(tc, "prepare", new Object[] {"prepare threw XAException", e});
			throw e;
		}

		TxExecutionContextHandler.removeAssociation(txWrapper);

		if (tc.isEntryEnabled()) Tr.exit(tc, "prepare", result);
		return result;
    }

    /* (non-Javadoc)
	 * @see javax.resource.spi.XATerminator#recover(int)
	 */
    public Xid[] recover(int flag) throws XAException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "recover", Util.printFlag(flag));

		final Xid[] result = TxExecutionContextHandler.recover(flag);
			
		if (tc.isEntryEnabled()) Tr.exit(tc, "recover", result);
		return result;
    }

    /* (non-Javadoc)
	 * @see javax.resource.spi.XATerminator#rollback(javax.transaction.xa.Xid)
	 */
    public void rollback(Xid xid) throws XAException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "rollback", xid);

		final JCATranWrapper txWrapper;
		
		try
		{
			validateXid(xid);

			// Get the wrapper adding an association in the process
			txWrapper = getTxWrapper(xid, true);
		}
		catch (XAException e)
		{
			if (tc.isEntryEnabled()) Tr.exit(tc, "rollback", "caught XAException: " + XAReturnCodeHelper.convertXACode(e.errorCode));
			throw e;
		}

		try
		{
			txWrapper.rollback();
		}
		catch (XAException e)
		{
			TxExecutionContextHandler.removeAssociation(txWrapper);

			if (tc.isEntryEnabled()) Tr.exit(tc, "rollback", "rethrowing XAException: " + XAReturnCodeHelper.convertXACode(e.errorCode));
			throw e;
		}

		TxExecutionContextHandler.removeAssociation(txWrapper);

        if (tc.isEntryEnabled()) Tr.exit(tc, "rollback");
    }

    /**
     * Gets the Transaction from the TxExecutionContextHandler that imported it
	 *
     * @param xid
     * @param addAssociation
     * @return
     * @throws XAException (XAER_NOTA) - thrown if the specified XID is invalid
     */
    protected JCATranWrapper getTxWrapper(Xid xid, boolean addAssociation) throws XAException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "getTxWrapper", xid);

        final JCATranWrapper txWrapper = TxExecutionContextHandler.getTxWrapper(xid, addAssociation);
   
        if(txWrapper.getTransaction() == null)
        {
            // there was no Transaction for this XID
            if(tc.isEntryEnabled()) Tr.exit(tc, "getTxWrapper", "no transaction was found for the specified XID");
            throw new XAException(XAException.XAER_NOTA);
        }

        if(tc.isEntryEnabled()) Tr.exit(tc, "getTxWrapper", txWrapper);
        return txWrapper;
    }

    /**
     * @param xid
     * @throws XAException
     */
    private void validateXid(Xid xid) throws XAException
    {
        if(!isValid(xid))
        {
            throw new XAException(XAException.XAER_INVAL);
        }
    }
	
    /**
     * @param xid
     * @return
     */
    public static boolean isValid(Xid xid)
    {
        final boolean result = (null != xid && -1 != xid.getFormatId() && null != xid.getGlobalTransactionId() && 0 <= xid.getGlobalTransactionId().length && Xid.MAXGTRIDSIZE >= xid.getGlobalTransactionId().length); // @250302C

        if (tc.isDebugEnabled()) Tr.debug(tc, "isValid", result);
        return result;
    }
	
    public String toString()
    {
        return "TxXATerminator: providerId=" + _providerId;
    }

    /**
     * @return
     */
    public JCARecoveryData getJCARecoveryData()
    {
        if(_JCARecoveryData == null)
        {
            _JCARecoveryData = (JCARecoveryData)((TranManagerSet)TransactionManagerFactory.getTransactionManager()).registerJCAProvider(_providerId);
        }

        return _JCARecoveryData;
    }
}
