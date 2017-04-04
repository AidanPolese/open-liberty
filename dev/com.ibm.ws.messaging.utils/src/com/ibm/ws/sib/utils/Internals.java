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
 * LIDB2117        040319 vaughton Original
 * PK60008         170408 pbroad   Provide access to default threadpool in app server environment
 * ============================================================================
 */

package com.ibm.ws.sib.utils;

interface Internals {

  public boolean isClustered ();
  public boolean isServer ();
  public Object getDefaultServerThreadPool();

}
