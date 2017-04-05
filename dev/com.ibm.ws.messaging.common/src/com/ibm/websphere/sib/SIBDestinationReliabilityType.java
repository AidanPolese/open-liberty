/*
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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Original
 * ============================================================================
 */

package com.ibm.websphere.sib;

public interface SIBDestinationReliabilityType {

    String BEST_EFFORT_NONPERSISTENT = "BEST_EFFORT_NONPERSISTENT";
    int BEST_EFFORT_NONPERSISTENT_VALUE = 0;

    String EXPRESS_NONPERSISTENT = "EXPRESS_NONPERSISTENT";
    int EXPRESS_NONPERSISTENT_VALUE = 1;

    String RELIABLE_NONPERSISTENT = "RELIABLE_NONPERSISTENT";
    int RELIABLE_NONPERSISTENT_VALUE = 2;

    String RELIABLE_PERSISTENT = "RELIABLE_PERSISTENT";
    int RELIABLE_PERSISTENT_VALUE = 3;

    String ASSURED_PERSISTENT = "ASSURED_PERSISTENT";
    int ASSURED_PERSISTENT_VALUE = 4;
}