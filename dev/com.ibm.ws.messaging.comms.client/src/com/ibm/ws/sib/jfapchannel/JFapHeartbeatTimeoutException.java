/*
 * @start_prolog@
 * Version: @(#) 1.11 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/JFapHeartbeatTimeoutException.java, SIB.comms, WASX.SIB, uu1215.01 05/05/18 05:42:07 [4/12/12 22:14:11]
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
 * F176003         030911 prestona Original
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * LIDB3706-5.211  050211 prestona serialization compatibility for sib.jfapchannel
 * D274182         050518 mattheg  Modify serialVersionUID to correct value
 * ============================================================================  
 */
package com.ibm.ws.sib.jfapchannel;

import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;

/**
 * Thrown if a heart beat timeout occures.
 */
public class JFapHeartbeatTimeoutException extends SIConnectionDroppedException
{
   private static final long serialVersionUID = -7831712835689004896L;  // LIDB3706-5.211, D274182
   
   public JFapHeartbeatTimeoutException(String msg)
   {
      super(msg);
   }
   
   public JFapHeartbeatTimeoutException(String msg, Throwable t)
   {
      super(msg, t);
   }
}
