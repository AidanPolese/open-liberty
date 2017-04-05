/*
 * @start_prolog@
 * Version: @(#) 1.18 SIB/ws/code/sib.trm.client.impl/src/com/ibm/ws/sib/trm/client/ClientHandler.java, SIB.trm, WASX.SIB, aa1225.01 05/11/02 03:20:56 [7/2/12 05:58:42]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70  (C) Copyright IBM Corp. 2004, 2005
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * LIDB2117        030509 vaughton Original
 * 281683.1        050926 gelderd  SVC: Improved diagnostic on bus connect failure - part 1
 * 290290.3        051101 gelderd  Improved entry/exit trace for sib.trm.client.impl
 * ============================================================================
 */

/*
 * Provides an abstract implementation of the Comms ClientComponentHandshake
 * interface
 */

package com.ibm.ws.sib.trm.client;

import com.ibm.ws.sib.comms.ClientComponentHandshake;
import com.ibm.ws.sib.comms.ClientConnection;
import com.ibm.ws.sib.mfp.trm.TrmFirstContactMessage;
import com.ibm.ws.sib.trm.TrmConstants;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;


abstract class ClientHandler implements ClientComponentHandshake {

  private static final TraceComponent tc = SibTr.register(ClientHandler.class, TrmConstants.MSG_GROUP, TrmConstants.MSG_BUNDLE);
  
  ClientAttachProperties cap;
  TrmFirstContactMessage fcm;
  Exception exception;
  CredentialType credentialType;

  /*
   * Constructor
   */

  public ClientHandler (ClientAttachProperties cap, CredentialType credentialType) {
    if (tc.isEntryEnabled()) SibTr.entry(tc, "ClientHandler", new Object[] { cap, credentialType });
    
    this.cap = cap;
    this.credentialType = credentialType;
    
    if (tc.isEntryEnabled()) SibTr.exit(tc, "ClientHandler", this);
  }

  /*
   * Method called when a client attachment fails
   */

  public void fail (ClientConnection cc, Throwable t) {
    if (tc.isEntryEnabled()) {
      SibTr.entry(tc, "fail", new Object[]{ cc, t });
      SibTr.exit(tc, "fail");
    }
  }

  /*
   * Get the generic form of reply message
   */

  TrmFirstContactMessage getReply () {
    return fcm;
  }

  /*
   * Accessor methods for 'exception' variable
   */
  
  Exception getException() {
    return exception;
  }

  void setException(Exception exception) {
    this.exception = exception;
  }
  
}
