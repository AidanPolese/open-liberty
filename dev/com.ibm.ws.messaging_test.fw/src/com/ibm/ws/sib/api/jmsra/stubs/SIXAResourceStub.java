/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date      Origin    Description
 * --------------- ------    --------  --------------------------------------------
 * 174369.10       21-Oct-03 djhoward  Created
 * 181796.6        05-Nov-03 djhoward  Core SPI move to com.ibm.wsspi.sib.core
 * 199220          20-Apr-04 pnickoll  Corrected javadoc
 * ============================================================================
 */
package com.ibm.ws.sib.api.jmsra.stubs;

import java.util.ArrayList;
import java.util.List;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.ibm.wsspi.sib.core.SIXAResource;

/**
 * Package: com.ibm.ws.sib.api.jmsra.stubs
 * Class definition: 
 */
public class SIXAResourceStub implements SIXAResource
{
  static List enlisted = new ArrayList();
  static int transactionTimeout = 30;

  /**
   * @see com.ibm.wsspi.sib.core.SIXAResource#isEnlisted()
   */
  public boolean isEnlisted()
  {
    return (enlisted.size() > 0);
  }

  /**
   * @see javax.transaction.xa.XAResource#commit(Xid, boolean)
   */
  public void commit(Xid xid, boolean onePhase) throws XAException
  {
    enlisted.remove(xid);
  }

  /**
   * @see javax.transaction.xa.XAResource#end(Xid, int)
   */
  public void end(Xid xid, int flags) throws XAException
  {
    enlisted.remove(xid);
  }

  /**
   * @see javax.transaction.xa.XAResource#forget(Xid)
   */
  public void forget(Xid xid) throws XAException
  {
  }

  /**
   * @see javax.transaction.xa.XAResource#getTransactionTimeout()
   */
  public int getTransactionTimeout() throws XAException
  {
    return transactionTimeout;
  }

  /**
   * @see javax.transaction.xa.XAResource#isSameRM(XAResource)
   */
  public boolean isSameRM(XAResource xares) throws XAException
  {
    return (this.equals(xares));
  }

  /**
   * @see javax.transaction.xa.XAResource#prepare(Xid)
   */
  public int prepare(Xid xid) throws XAException
  {
    return 0;
  }

  /**
   * @see javax.transaction.xa.XAResource#recover(int)
   */
  public Xid[] recover(int flags) throws XAException
  {
    return null;
  }

  /**
   * @see javax.transaction.xa.XAResource#rollback(Xid)
   */
  public void rollback(Xid xid) throws XAException
  {
    enlisted.remove(xid);    
  }

  /**
   * @see javax.transaction.xa.XAResource#setTransactionTimeout(int)
   */
  public boolean setTransactionTimeout(int seconds) throws XAException
  {
    transactionTimeout = seconds;
    return true;
  }

  /**
   * @see javax.transaction.xa.XAResource#start(Xid, int)
   */
  public void start(Xid xid, int flags) throws XAException
  {
    enlisted.add(xid);
  }
}
