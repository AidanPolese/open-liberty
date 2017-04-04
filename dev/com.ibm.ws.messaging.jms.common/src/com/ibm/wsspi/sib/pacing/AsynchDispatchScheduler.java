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
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 304501.adm       130905 wallisgd Created original
 * ===========================================================================
 */

package com.ibm.wsspi.sib.pacing;

/**
 * @author wallisgd
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * Interface implemented by the XD object returned on from MessagePacing.preAsynchDispatch()
 * method.
**/
public interface AsynchDispatchScheduler {
  /**
   * returns true if the message pacer requires the MDB dispatcher to suspend processing messages.
   * returns false if the message pacer requires the MDB dispatcher to continue processing messages.
   * 
   */
  boolean suspendAsynchDispatcher();
}
