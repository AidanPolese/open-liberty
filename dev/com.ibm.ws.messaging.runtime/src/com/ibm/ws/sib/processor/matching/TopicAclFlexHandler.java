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
 * 184185.1.2       190204 nyoung   Add basic MatchSpace ACL support.
 * 184185.1.6       270404 nyoung   Enable delivery time discriminator access checks.
 * 218436           220704 nyoung   Synchronization defects in MessageProcessorSearchResults.
 * SIB0163.mp.2     171007 nyoung   Code Review Improvements.
 * 480930           061107 sibcopyr Automatic update of trace guards 
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.matching;

import java.util.List;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.impl.interfaces.DestinationHandler;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * @author Neil Young
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TopicAclFlexHandler 
  implements MessageProcessorSearchResults.Handler
{
  // Standard trace boilerplate

  private static final TraceComponent tc =
    SibTr.register(
      TopicAclFlexHandler.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  //------------------------------------------------------------------------------
  // Method: TopicAclFlexHandler.initResult
  //------------------------------------------------------------------------------
  /** Create a matching result object of the appropriate class for this handler.
   *
   * @return a result object of the appropriate type
   */ //---------------------------------------------------------------------------
  public Object initResult()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "initResult");

    //theResults = new ArrayList();
    Object theResults = new TopicAclTraversalResults();
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "initResult");
    return theResults;
  }

  //------------------------------------------------------------------------------
  // Method: TopicAclFlexHandler.resetResult
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
       ((TopicAclTraversalResults) result).reset();
    //   ((ArrayList) result).clear();

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "resetResult");

  }

  //------------------------------------------------------------------------------
  // Method: TopicAclFlexHandler.processIntermediateMatches
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

    //((List) result).addAll(targets);
    if (result != null)
       ((TopicAclTraversalResults) result).consolidate(targets);    
       
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "processIntermediateMatches");

  }

  //------------------------------------------------------------------------------
  // Method: TopicAclFlexHandler.postProcessMatches
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
    String theString = " tacl: [";
    
    TopicAclTraversalResults tacls = (TopicAclTraversalResults) results[index];
    if(!tacls.accumGroupAllowedToPublish.isEmpty())
      theString = theString + " pGroups - " + tacls.accumGroupAllowedToPublish;
    if(!tacls.accumGroupAllowedToSubscribe.isEmpty())
      theString = theString + " sGroups - " + tacls.accumGroupAllowedToSubscribe;
    if(!tacls.accumUsersAllowedToPublish.isEmpty())
      theString = theString + " pUsers - " + tacls.accumUsersAllowedToPublish;
    if(!tacls.accumUsersAllowedToSubscribe.isEmpty())
      theString = theString + " sUsers - " + tacls.accumUsersAllowedToSubscribe;
    
    theString = theString + "]";
    return theString;
  }    
}
