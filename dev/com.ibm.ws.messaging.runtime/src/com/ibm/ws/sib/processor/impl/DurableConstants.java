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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 187000          150304 astley   Remote durable support
 * 184185.1.8      070504 nyoung   Check Durable Subscription access permission.
 * 219870          280704 astley   Remote durable now retries forever
 * 265506          040405 gatfora  Add mapping for durable mismatch exception
 * 264822           120405 gatfora  deleteRemoteSubs should return correct error
 * ============================================================================
 */
package com.ibm.ws.sib.processor.impl;

/**
 * Constants used by the Durable Input/Output handlers.
 */
public interface DurableConstants {
  // Constants for message status
  public int STATUS_OK                    = 0;
  public int STATUS_SUB_ALREADY_EXISTS    = 1;
  public int STATUS_SUB_GENERAL_ERROR     = 2;
  public int STATUS_SUB_NOT_FOUND         = 3;
  public int STATUS_SUB_CARDINALITY_ERROR = 4;
  public int STATUS_NOT_AUTH_ERROR        = 5;
  public int STATUS_SUB_MISMATCH_ERROR    = 6;
  public int STATUS_SIB_LOCKED_ERROR      = 7;
    
  // Constants for message priority, timeouts, etc.
  public long CREATEDURABLE_RETRY_TIMEOUT = 3000;
  public long DELETEDURABLE_RETRY_TIMEOUT = 3000;
  public long CREATESTREAM_RETRY_TIMEOUT  = 3000;
  
  // 219870 public int  CREATEDURABLE_NUMTRIES      = 3;
  // 219870 public int  DELETEDURABLE_NUMTRIES      = 3;
  // 219870 public int  CREATESTREAM_NUMTRIES       = 3;
  
}
