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
 * 192832.15        070404 mcobbett Initial Creation
 * 334543           221205 cwilkin  Synch unittests with asynchdeletion
 * 337071           130106 cwilkin  Fix hang in unittests
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.ws.sib.processor.impl.AsynchDeletionThread;

/**
 * The unit tests requiring call-backs from MP code implement this interface,
 * then set it into the MessageProcessor.
 * 
 */
public interface MPCallsToUnitTestHandler
{
  /**
   * Used by the code to report a failure in a unit test, if the system
   * is running within the junit test framework.
   * <p>
   * If the system is not running in the junit framework, then nothing 
   * happens.
   * 
   * @param e The exception which is causing the failure.
   */
  public void unitTestFailure( String textDescription , Exception e );

  /**
   * Used by the async deletion thread to tell the unit tests that it is
   * ready to run.
   * <p>
   * The unit tests can choose to block this thread to finely control 
   * when things get deleted.
   */
  public void asyncDeletionThreadReadyToStart();
  
  /**
   * Used by the async deletion thread to synchronize with teardown
   */
  public Object getAsynchLock(AsynchDeletionThread adt);
  
}
