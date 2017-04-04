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
 * 166318.5         190603 nyoung   Integrate MatchSpace with MP.
 * 171415           100703 gatfora  Removal of complile time warnings.
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matching package
 * 179880           151003 gatfora  Remove compile time warnings
 * 184185.1.6       270404 nyoung   Enable delivery time discriminator access checks.
 * 199574.1         040504 gatfora  Remove usage of ArrayLists and use LinkedList instead.
 * 208958           110604 gatfora  Refactor LinkedList/ArrayList use
 * 218436           220704 nyoung   Synchronization defects in MessageProcessorSearchResults.
 * 257841           280205 nyoung   Subscribers may receive duplicate publications.
 * SIB0163.mp.2     171007 nyoung   Code Review Improvements.
 * 480930           061107 sibcopyr Automatic update of trace guards
 * 482749           141107 nyoung   FINDBUGS: Performance suggestions for .processor.matching.   
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.matching;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.ws.sib.processor.impl.interfaces.DestinationHandler;
/**
 * @author nyoung
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ConsumerFlexHandler
  implements MessageProcessorSearchResults.Handler
{
  // Standard trace boilerplate

  private static final TraceComponent tc =
    SibTr.register(
      ConsumerFlexHandler.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  //------------------------------------------------------------------------------
  // Method: DataFlowFlexHandler.initResult
  //------------------------------------------------------------------------------
  /** Create a matching result object of the appropriate class for this handler.
   *
   * @return a result object of the appropriate type
   */ //---------------------------------------------------------------------------
  public Object initResult()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "initResult");

    Set theResults = new HashSet();
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "initResult");
    return theResults;
  }

  //------------------------------------------------------------------------------
  // Method: DataFlowFlexHandler.resetResult
  //------------------------------------------------------------------------------
  /** Reset a result object of the appropriate class for this handler
   *  to permit its re-use by another invocation of MessageProcessor.
   *
   * @param result the result object to reset
   */ //---------------------------------------------------------------------------
  public void resetResult(Object result)
  {

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "resetResult");

    if (result != null)
       ((HashSet) result).clear();

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "resetResult");

  }

  //------------------------------------------------------------------------------
  // Method: DataFlowFlexHandler.processIntermediateMatches
  //------------------------------------------------------------------------------
  /** Accumulate intermediate results for this handler while traversing
   *  MatchSpace.
   *
   * @param targets vector of MatchTarget to process
   * @param result result object provided by the initResult method of this particular
   * handler
   */ //---------------------------------------------------------------------------
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

  //------------------------------------------------------------------------------
  // Method: DataFlowFlexHandler.postProcessMatches
  //------------------------------------------------------------------------------
  /** Complete processing of results for this handler after completely traversing
   *  MatchSpace.
   *
   *  ACL checking takes place at this point.
   *
   * @param results Vector of results for all handlers; results of MatchTarget types
   * whose index is less than that of this MatchTarget type have already been postprocessed.
   * @param index Index in results vector of accumulated results of this MatchTarget
   * type
   */ //---------------------------------------------------------------------------
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
    StringBuffer buffer = new StringBuffer(" cons: [");
    
    Set mcps = (Set) results[index];
    
    Iterator itr = mcps.iterator();
    boolean first = true;
    while (itr.hasNext())
    {
      MatchingConsumerPoint mcp = (MatchingConsumerPoint) itr.next();
      if(!first)
      {
        buffer.append(", ");
      }
      else
        first = false;
      buffer.append(mcp.getConsumerPointData().getConsumerManager().getDestination().getName());
      buffer.append(":");
      buffer.append(mcp.getConsumerPointData().getSelector());
    }
    buffer.append("]");
    return buffer.toString();
  }
}
