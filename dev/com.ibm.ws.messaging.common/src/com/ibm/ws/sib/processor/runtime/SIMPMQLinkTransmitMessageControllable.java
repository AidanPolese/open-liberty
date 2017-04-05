/*
 * 
 * 
 * ===========================================================================
 * IBM Confidential OCO Source Material
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ===========================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * SIB0105.mp.1     071106 cwilkin  Link Transmission Controllables
 * SIB0105.mp.6     210607 nyoung   MQLink Controllable changes
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

public interface SIMPMQLinkTransmitMessageControllable extends
  SIMPQueuedMessageControllable {
  
  /**
   * The queue manager this message is currently targetted at
   * 
   * @return Queue manager
   */
  String getTargetQMgr();
  
  /**
   * The MQ queue name to which this message is being transmitted to 
   * @return
   */
  String getTargetQueue();
  
  /**
   * Set the message state.
   * @param state 
   */
  void setState(String state);
  
  /**
   * Set the target queue manager of this message.
   * @param qMgr
   */
  void setTargetQMgr(String qMgr);
  
  /**
   * Set the target queue of this message.
   * @param queue
   */
  void setTargetQueue(String queue);

}
