/*
 * @start_prolog@
 * Version: @(#) 1.11 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/IdTableFullException.java, SIB.comms, WASX.SIB, uu1215.01 05/05/18 05:41:30 [4/12/12 22:14:14]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60          (C) Copyright IBM Corp. 2003, 2005
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
 * Creation        030424 prestona Original
 * F166959         030521 prestona Rebase on non-prototype CF + TCP Channel
 * LIDB3706-5.209  040211 prestona serialization compatibility for sib.jfapchannel.impl
 * D274182         050518 mattheg  Modify serialVersionUID to correct value
 * ============================================================================ 
 */
package com.ibm.ws.sib.jfapchannel.impl;

import com.ibm.websphere.sib.exception.SIResourceException;

/**
 * Exception thrown by the ID table when it is full and a call is made
 * to add another ID to it.
 * @author prestona
 */
public class IdTableFullException extends SIResourceException
{
   private static final long serialVersionUID = 7431059884920849059L;   // LIDB3706-5.209, D274182
}
