/*
 * @start_prolog@
 * Version: @(#) 1.4 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/MSSIXAResourceProvidingConnectionProxy.java, SIB.comms, WASX.SIB, uu1215.01 08/04/29 21:35:17 [4/12/12 22:14:05]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2005, 2008
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
 * D318614         051031 prestona Support MSSIXAResourceProvider interface
 * 514462          080421 vaughton Further performance optimisation
 * ============================================================================
 */

package com.ibm.ws.sib.comms.client;

import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.transactions.mpspecific.MSSIXAResourceProvider;
import com.ibm.wsspi.sib.core.SIXAResource;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;

/**
 * Comms implementation of SICoreConnection which is augmented such that it may obtain the XAResource of the Message Store.  This
 * additional capability is used for some recovery scenarios.
 * <p>
 * This implementation of the SICoreConnection interface is returned in preference to ConnectionProxy (which it extends) if the FAP
 * version 4 (WAS 6.1) protocol (or later) is being used.
 *
 * @see com.ibm.ws.sib.transactions.mpspecific.MSSIXAResourceProvider
 */
public class MSSIXAResourceProvidingConnectionProxy extends ConnectionProxy implements MSSIXAResourceProvider {
  public MSSIXAResourceProvidingConnectionProxy (final Conversation c) {
    super(c);
  }

  public MSSIXAResourceProvidingConnectionProxy (final Conversation c, final ConnectionProxy parent) {
    super(c, parent);
  }

  public SIXAResource getMSSIXAResource()throws SIConnectionDroppedException, SIConnectionUnavailableException, SIConnectionLostException, SIResourceException, SIErrorException {
    return _internalGetSIXAResource(true);
  }
}
