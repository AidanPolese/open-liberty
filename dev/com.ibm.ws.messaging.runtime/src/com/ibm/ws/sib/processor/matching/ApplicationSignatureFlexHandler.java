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
 * ---------------  ------ -------- ------------------------------------------
 * SIB0163.mp.1     190907 nyoung   XD Integration.
 * SIB0163.mp.2     171007 nyoung   Code Review Improvements.
 * 482749           141107 nyoung   FINDBUGS: Performance suggestions for .processor.matching.                                                                                        
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.matching;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.impl.interfaces.DestinationHandler;
import com.ibm.ws.sib.utils.ras.SibTr;

public class ApplicationSignatureFlexHandler
  implements MessageProcessorSearchResults.Handler
{
  // Standard trace boilerplate

  private static final TraceComponent tc =
    SibTr.register(
        ApplicationSignatureFlexHandler.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MessageProcessorSearchResults.Handler#initResult()
   */
  public Object initResult()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "initResult");

    Set theResults = new HashSet();
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "initResult");
    return theResults;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MessageProcessorSearchResults.Handler#resetResult(java.lang.Object)
   */
  public void resetResult(Object result)
  {

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "resetResult");

    if (result != null)
       ((HashSet) result).clear();

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "resetResult");

  }
  
  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MessageProcessorSearchResults.Handler#processIntermediateMatches(java.util.List, java.lang.Object)
   */
  public void processIntermediateMatches(List targets, Object result)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(
        tc,
        "processIntermediateMatches",
        "targets: " + targets + ";results: " + result);

    ((Set) result).addAll(targets);

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "processIntermediateMatches");

  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MessageProcessorSearchResults.Handler#postProcessMatches(com.ibm.ws.sib.processor.impl.interfaces.DestinationHandler, java.lang.String, java.lang.Object[], int)
   */
  public void postProcessMatches(DestinationHandler topicSpace,
                                 String topic, 
                                 Object[] results, 
                                 int index)
  {
   // noop for this handler
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MessageProcessorSearchResults.Handler#setAuthorization(com.ibm.ws.sib.processor.matching.TopicAuthorization)
   */
  public void setAuthorization(TopicAuthorization authorization)
  {
    // noop for this handler
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MessageProcessorSearchResults.Handler#toString(java.lang.Object[], int)
   */
  public String toString(Object results[], int index) 
  {
    StringBuffer buffer = new StringBuffer(" appSigs: [");
    
    Set sigs = (Set) results[index];
    
    Iterator itr = sigs.iterator();
    boolean first = true;
    while (itr.hasNext())
    {
      MatchingApplicationSignature sig = (MatchingApplicationSignature) itr.next();
      if(!first)
      {
        buffer.append( ", ");
      }
      else
        first = false;
      buffer.append(sig.getApplicationSignature());
    }
    buffer.append("]");
    return buffer.toString();
  }

}
