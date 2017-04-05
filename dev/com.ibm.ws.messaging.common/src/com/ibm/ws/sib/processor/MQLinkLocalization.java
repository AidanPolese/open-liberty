/*
 * 
 * 
 * ===========================================================================
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
 * ---------------  ------ -------- ------------------------------------------
 * SIB0211.mp.1     260207 nyoung   Dynamic Link Configuration.
 * SIB0105.mp.6     210607 nyoung   MQLink Controllable changes
 * ===========================================================================
 */

package com.ibm.ws.sib.processor;

import com.ibm.websphere.sib.exception.SIException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.msgstore.ItemStream;
import com.ibm.ws.sib.processor.runtime.SIMPMQLinkQueuePointControllable;

/**
 * Interface that supports access to MQLink resources that are managed by
 * the MessageProcessor component.
 *
 */
public interface MQLinkLocalization 
{
  /**
   * Retrieves the MQLink's State ItemStream
   * 
   * @return itemStream
   * @throws SIException
   */
  public ItemStream getMQLinkStateItemStream()
      throws SIException;
  
  /**
   * Sets the MQLink's State ItemStream
   * 
   * @param mqLinkStateItemStream
   * @throws SIException
   */
  public void setMQLinkStateItemStream(ItemStream mqLinkStateItemStream)
      throws SIException;  
  
  /**
   * Resources associated with the MQLink will be marked for deletion.
   * 
   * @throws SIResourceException
   * @throws SIException
   */
  public void delete()
      throws SIResourceException, SIException;
  
  
  /**
   * Retrieve a reference to the Controllable associated with the MQLink
   * Queue Point.
   * 
   * @return
   * @throws SIException
   */
  public SIMPMQLinkQueuePointControllable getSIMPMQLinkQueuePointControllable() 
    throws SIException;
  
}
