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
 * ---------------  ------ -------- -------------------------------------------------
 * SIB0009.mp.03    020905 gatfora  Extend unit test framework to enable restarts of ME 
 * ============================================================================
 */
 package com.ibm.ws.sib.unittest;

public interface UnitTestMEStartCallback
{
  public void stop(boolean mpOnly, int shutdownMode, boolean closeConnection) 
  throws Exception;
  
  public void createME(UnitTestMEStarter starter, boolean coldStart, boolean mpOnly) throws Exception;
  
  public void start(UnitTestMEStarter starter, boolean coldStart, boolean mpOnly) throws Exception;
  
  public void coldStart();
}
