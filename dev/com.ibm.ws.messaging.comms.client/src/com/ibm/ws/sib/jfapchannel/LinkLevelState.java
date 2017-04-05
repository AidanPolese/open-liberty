/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/LinkLevelState.java, SIB.comms, WASX.SIB, uu1215.01 09/03/31 10:45:48 [4/12/12 22:18:15]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70  (C) Copyright IBM Corp. 2009 
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
 * PK83641		   310309 ajw	   reset LinkLevelState when returning from pool
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel;

public interface LinkLevelState {

	/**
	 * Resets the state of this LinkLevelState object so that the same instance can
	 * be used. This allows MFP to maintain the list of LinkLevelState objects.
	 */
	void reset();
}
