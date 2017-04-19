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
 * 298905           051005 gatfora  Make unit tests able to choose filestore or cloudscape
 * ============================================================================
 */
 package com.ibm.ws.sib.unittest;

public class UnitTestConstants
{
  /**
   * Name of the bus used in tests.
   */
  public static String BUS_NAME = "DEFAULT";
    
  /**
   * Name of the ME used in tests.
   */
  public static final String ME_NAME = "SIMPTestCase_ME";

  /**
   * The file store class name
   */
  public static final String FILE_STORE_CLASS = "com.ibm.ws.sib.msgstore.persistence.objectManager.PersistentMessageStoreImpl";
  
  /**
   * The DB class name
   */
  public static final String DB_STORE_CLASS = "com.ibm.ws.sib.msgstore.persistence.impl.PersistentMessageStoreImpl";
 
  /**
   * The store that the tests will use
   */
  public static String USE_DB_CLASS = FILE_STORE_CLASS;
  
}
