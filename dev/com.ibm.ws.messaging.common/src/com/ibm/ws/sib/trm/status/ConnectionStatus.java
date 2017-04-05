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
 * LIDB2117        030721 vaughton Original
 * ============================================================================
 */

package com.ibm.ws.sib.trm.status;

/**
 * This class contains connection status information
 */

public final class ConnectionStatus extends Status {

  public String toString () {
    return "Connection status: "+super.toString();
  }

}
