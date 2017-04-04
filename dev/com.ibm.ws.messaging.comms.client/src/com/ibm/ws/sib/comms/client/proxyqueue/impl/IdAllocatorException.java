/*
 * @start_prolog@
 * Version: @(#) 1.11 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/proxyqueue/impl/IdAllocatorException.java, SIB.comms, WASX.SIB, uu1215.01 05/05/18 05:43:42 [4/12/12 22:14:07]
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
 * Creation        030702 prestona Original
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * LIDB3706-5.195  050211 prestona serialization compatibility for sib.comms.impl
 * D274182         050518 mattheg  Modify serialVersionUID to correct value
 * ============================================================================
 */
package com.ibm.ws.sib.comms.client.proxyqueue.impl;

import com.ibm.websphere.sib.exception.SIResourceException;

/**
 * Thrown by an ID allocator when something goes wrong.
 */
public class IdAllocatorException extends SIResourceException
{
   private static final long serialVersionUID = -9187470254724030964L;  // LIDB3706-5.195, D274182
}
