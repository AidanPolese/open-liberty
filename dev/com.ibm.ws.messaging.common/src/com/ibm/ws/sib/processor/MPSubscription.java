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
 * created          190705 rjnorris Initial version  
 * SIB0009.mp.01    210705 rjnorris Add support for multiple selectionCriteria on DurableSub
 * 346723.1         110206 rjnorris Change required for WSN panels
 * ============================================================================
 */

package com.ibm.ws.sib.processor;

import java.util.Map;

import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.processor.exceptions.SIMPSelectionCriteriaNotFoundException;
import com.ibm.wsspi.sib.core.SelectionCriteria;
import com.ibm.wsspi.sib.core.exception.SIDiscriminatorSyntaxException;
import com.ibm.wsspi.sib.core.exception.SISelectorSyntaxException;


  /**
   * 
   */
  public interface MPSubscription 
  {
    /**
      Add an additional selection criterias to the original one 
      (supplied at subscription creation time) to the subscription
      Duplicate selection criterias are ignored
    **/
    public void addSelectionCriteria(SelectionCriteria selCriteria)
    throws SIDiscriminatorSyntaxException, SISelectorSyntaxException, SIResourceException;
    
    /**
      Remove a selection criteria from the subscription
    **/
    public void removeSelectionCriteria(SelectionCriteria selCriteria) 
    throws SIMPSelectionCriteriaNotFoundException, SIResourceException;

    /**
      List existing selection criterias registered with the subscription
    **/
    public SelectionCriteria[] getSelectionCriteria();

    /**
      Store a map of user properties with a subscription
      The map provided on this call will replace any existing map stored with the subscription
    **/
    public void setUserProperties(Map userData)
    throws SIResourceException;

    /**
      Get the map currently stored with the subscription
    **/
    public Map getUserProperties();

    /**
      Get subscriberID for this subscription
    **/
    public String getSubscriberId();
    
    /**
      Get WPMTopicSpaceName for this subscription
    **/
    public String getWPMTopicSpaceName();
        
    /**
      Get MEName for this subscription
    **/
    public String getMEName();
  }
