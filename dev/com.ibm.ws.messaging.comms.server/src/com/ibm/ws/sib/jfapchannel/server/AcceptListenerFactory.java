/*
 * @start_prolog@
 * Version: @(#) 1.5 SIB/ws/code/sib.jfapchannel.server/src/com/ibm/ws/sib/jfapchannel/AcceptListenerFactory.java, SIB.comms, WASX.SIB, aa1225.01 05/02/04 09:48:17 [7/2/12 05:59:06]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60          (C) Copyright IBM Corp. 2004, 2005
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
 * F189351         040203 prestona CF admin support 
 * ============================================================================ 
 */
package com.ibm.ws.sib.jfapchannel.server;

import com.ibm.ws.sib.jfapchannel.AcceptListener;

public interface AcceptListenerFactory
{
   AcceptListener manufactureAcceptListener();
}
