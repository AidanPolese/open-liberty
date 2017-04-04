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

import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.ws.sib.processor.MPSelectorEvaluator;
import com.ibm.ws.sib.processor.MPSelectorEvaluatorFactory;

/**
 * @author Neil Young
 */
public class MPSelectorEvaluatorFactoryImpl extends MPSelectorEvaluatorFactory 
{
  //trace
  private static final TraceComponent tc =
    SibTr.register(
      MPSelectorEvaluatorFactoryImpl.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);
  
  
  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.MPSelectorEvaluatorFactory#createMPSelectorEvaluator()
   */  
  public MPSelectorEvaluator createMPSelectorEvaluator()
  {
    if (tc.isEntryEnabled()) 
      SibTr.entry(tc, "createMPSelectorEvaluator" );        
    
    MPSelectorEvaluator mpSelectorEvaluator = new MPSelectorEvaluatorImpl();
    
    if (tc.isEntryEnabled())
      SibTr.exit(tc, "createMPSelectorEvaluator", mpSelectorEvaluator);
      
    return mpSelectorEvaluator;
  } 

}
