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
 * SIB0113a.mp.1    240707 cwilkin  Gathered Consumer foundation
 * SIB0163.mp.1     190907 nyoung   XD Integration.
 * ============================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException;
import com.ibm.ws.sib.msgstore.LockingCursor;
import com.ibm.ws.sib.utils.SIBUuid12;

public interface JSConsumerKey extends ConsumerKey 
{
  public boolean isKeyReady();
  public void markNotReady();
  void ready(Reliability unrecoverable) throws SINotPossibleInCurrentConfigurationException ;
  void notReady();
  public boolean getForwardScanning();
  public boolean isSpecific();
  public SIBUuid12 getConnectionUuid();
  public JSConsumerManager getConsumerManager();
  LockingCursor getGetCursor(SIMPMessage msg);
}
