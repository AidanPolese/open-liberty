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
public interface MessagePacingControl extends MessagePacing {

  // This interface wraps the MessagePacing interface (implemented by the 
  // external message pacer (e.g. XD) in a control interface that includes
  // the additional registration, callback and state checking methods below 
  
  public void registerMessagePacer(MessagePacing messagePacer) throws IllegalStateException;

  public void resumeAsynchDispatcher(Object dispatcherContext);

  boolean isActive();

}
