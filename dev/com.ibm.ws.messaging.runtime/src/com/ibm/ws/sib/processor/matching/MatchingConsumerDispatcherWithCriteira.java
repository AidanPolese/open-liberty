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
 * 226980           310804 nyoung   Need wrapped ConsumerDispatcher in MatchSpace.
 * 257841           280205 nyoung   Subscribers may receive duplicate publications.
 * SIB0009.mp.01    210705 rjnorris Add support for multiple selectionCriteria on DurableSub
 * 309940           031005 gatfora  Missing trace statements
 * ===========================================================================
 */
 
package com.ibm.ws.sib.processor.matching;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.impl.ConsumerDispatcher;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.SelectionCriteria;

/**
 * @author Neil Young
 *
 * <p>The MatchingConsumerDispatcher class is a wrapper that holds a ConsumerDispatcher,
 * but allows a MatchTarget type to be associated with it for storage in the
 * MatchSpace. 

 */
public class MatchingConsumerDispatcherWithCriteira extends MessageProcessorMatchTarget
{

  private static final TraceComponent tc = 
    SibTr.register(MatchingConsumerDispatcherWithCriteira.class, null, null);
  
  private ConsumerDispatcher consumerDispatcher;
  private SelectionCriteria selectionCriteria;
  
  MatchingConsumerDispatcherWithCriteira(ConsumerDispatcher cd, SelectionCriteria sc)
  {    
    super(JS_SUBSCRIPTION_TYPE);
    if (tc.isEntryEnabled())
      SibTr.entry(tc, "MatchingConsumerDispatcherWithCriteira", new Object[] {
          cd, sc });
    consumerDispatcher = cd;
    selectionCriteria = sc;
    if (tc.isEntryEnabled())
      SibTr.exit(tc, "MatchingConsumerDispatcherWithCriteira", this);
  }

  public boolean equals(Object o) 
  {
    if (tc.isEntryEnabled())
      SibTr.entry(tc, "equals", o);
    boolean areEqual = true;
    if (o instanceof MatchingConsumerDispatcherWithCriteira)
    {
      ConsumerDispatcher otherCD = ((MatchingConsumerDispatcherWithCriteira) o).consumerDispatcher;
      SelectionCriteria otherSC  = ((MatchingConsumerDispatcherWithCriteira) o).selectionCriteria;   
      if( !(consumerDispatcher.equals(otherCD)) )
        areEqual = false;
      if( selectionCriteria == null )
      {
        if (otherSC != null) 
          areEqual = false;
      }
      else
      {
         if( otherSC == null )
           areEqual = false;  
         else
           if (!selectionCriteria.equals(otherSC))
             areEqual = false; 
      }     
    }
    else
      areEqual = false;
     
    if (tc.isEntryEnabled())
      SibTr.exit(tc, "equals", new Boolean(areEqual));
    return areEqual;
  }
  
  public int hashCode() 
  {
    return consumerDispatcher.hashCode();
  }  

  /**
   * Returns the consumerDispatcher.
   * @return ConsumerDispatcher
   */
  public ConsumerDispatcher getConsumerDispatcher() 
  {
    if (tc.isEntryEnabled()) 
    {
      SibTr.entry(tc, "getConsumerDispatcher");
      SibTr.exit(tc, "getConsumerDispatcher", consumerDispatcher);	
    }
    return consumerDispatcher;
  }

}
