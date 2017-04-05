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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * LIDB2117        030728 vaughton Original
 * ============================================================================
 */

package com.ibm.ws.sib.trm.contact;

import com.ibm.ws.sib.comms.MEConnection;

/**
 * An implementation of this interface is used to pass on information about
 * errors with communication connections.
 */

public interface CommsErrorListener {

  /**
   * Method called to inform of a communications error
   *
   * @param m The MEConnection on which the error occurred
   *
   * @param t The Throwable object associated with the error
   */

  public void error (MEConnection m, Throwable t);

}
