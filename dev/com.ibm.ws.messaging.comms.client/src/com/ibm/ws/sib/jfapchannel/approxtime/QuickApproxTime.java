/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/approxtime/QuickApproxTime.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:04:10 [4/12/12 22:14:17]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61  (C) Copyright IBM Corp. 2003, 2006 
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
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.approxtime;

/**
 * This class abstracts the interface of the TCP channel's QuickApproxTime class. This is needed as 
 * this facility is not always available in the Portly client.
 * 
 * @author Gareth Matthews
 */
public interface QuickApproxTime
{
   /**
    * @return Returns the approximate time.
    */
   public long getApproxTime();
   
   /**
    * Sets the time interval at which the approximate time is gathered.
    * 
    * @param interval The time interval in milliseconds.
    */
   public void setInterval(long interval);
}
