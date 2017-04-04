/*
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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Version X copied from CMVC
 * ============================================================================
 */
package com.ibm.ws.sib.core.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.wsspi.sib.core.SelectionCriteria;
import com.ibm.wsspi.sib.core.SelectionCriteriaFactory;
import com.ibm.wsspi.sib.core.SelectorDomain;

/**
 * @author Neil Young
 */
public class SelectionCriteriaFactoryImpl extends SelectionCriteriaFactory
{
  //trace
  private static final TraceComponent tc =
    SibTr.register(
      SelectionCriteriaFactoryImpl.class,
      SICoreConstants.CORE_TRACE_GROUP,
      SICoreConstants.RESOURCE_BUNDLE);
 
  
  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.SICoreConnection#createSelectionCriteria()
   */  
  public SelectionCriteria createSelectionCriteria()
    throws SIErrorException
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "createSelectionCriteria" );        
    
    SelectionCriteria criteria = new SelectionCriteriaImpl(null,
                                                           "",
                                                           SelectorDomain.SIMESSAGE);
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "createSelectionCriteria", criteria);
      
    return criteria;
  } 
       
  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.SICoreConnection#createSelectionCriteria()
   */
  public SelectionCriteria createSelectionCriteria(String discriminator,
                                                 String selectorString,
                                                 SelectorDomain selectorDomain) 
  
  throws SIErrorException
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "createSelectionCriteria", 
        new Object[]{discriminator, selectorString, selectorDomain});        
    
    SelectionCriteria criteria = new SelectionCriteriaImpl(discriminator,
                                                           selectorString,
                                                           selectorDomain);
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "createSelectionCriteria", criteria);
      
    return criteria;
  }
}
