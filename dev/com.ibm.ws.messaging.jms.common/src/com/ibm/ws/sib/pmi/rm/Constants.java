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
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------
 * 195720.0.2       030604 caseyj   SIBPmiRm interface initial implementation
 * 195720.0.14      150604 caseyj   Allow comms to know whether RM is active
 * ============================================================================
 */

package com.ibm.ws.sib.pmi.rm;

import com.ibm.ws.sib.utils.TraceGroups;

public class Constants 
{
  /**
   * Currently piggybacking on the util trace group.
   */
  public final static String MSG_GROUP  = TraceGroups.TRGRP_UTILS;
  
  /**
   * No messages for sib.pmi.rm
   */
  public final static String MSG_BUNDLE = null;
  
  /**
   * This is the implementation that will be used for all sib.pmi.rm calls.
   */
  public final static String SIB_PMI_RM_IMPL_CLASS 
    = "com.ibm.ws.sib.pmi.rm.impl.SIBPmiRmWsImpl";
    
  /**
   * This is the WAS RM implementation used for unit tests.
   */    
  public final static String PMI_RM_SIB_IMPL_TEST_CLASS 
    = "com.ibm.ws.sib.pmi.rm.impl.test.PmiRmSIBTestImpl";
}
