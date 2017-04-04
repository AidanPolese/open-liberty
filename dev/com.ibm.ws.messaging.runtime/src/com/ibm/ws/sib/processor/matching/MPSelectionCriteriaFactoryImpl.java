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
package com.ibm.ws.sib.processor.matching;

import java.util.Map;

import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.ws.sib.processor.MPSelectionCriteria;
import com.ibm.ws.sib.processor.MPSelectionCriteriaFactory;
import com.ibm.wsspi.sib.core.SelectorDomain;

/**
 * @author Neil Young
 */
public class MPSelectionCriteriaFactoryImpl extends MPSelectionCriteriaFactory 
{
  //trace
  private static final TraceComponent tc =
    SibTr.register(
      MPSelectionCriteriaFactoryImpl.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);
  
  @Override
  public MPSelectionCriteria createSelectionCriteria(String discriminator,
      String selectorString, SelectorDomain selectorDomain,
      Map<String, Object> selectorProperties)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "createSelectionCriteria" );        
    
    MPSelectionCriteria mpSelectionCriteria = 
      new MPSelectionCriteriaImpl(discriminator,
                                  selectorString, 
                                  selectorDomain,
                                  selectorProperties);
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "createSelectionCriteria", mpSelectionCriteria);
      
    return mpSelectionCriteria;
  } 

}
