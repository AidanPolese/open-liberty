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
 * 166833.12        201003 gatfora   Create
 * 184185.1.6       270404 nyoung   Enable delivery time discriminator access checks.
 * 199574.1         040504 gatfora  Remove usage of ArrayLists and use LinkedList instead.
 * 208958           110604 gatfora  Refactor LinkedList/ArrayList use
 * 218436           220704 nyoung   Synchronization defects in MessageProcessorSearchResults.
 * 248333           221204 nyoung   Support proxy sub discriminator access checks.
 * 243187           221204 gatfora  Use cached version of isBusSecure
 * 257841           280205 nyoung   Subscribers may receive duplicate publications.
 * SIB0201b.mp.2    300807 nyoung   Make topic space name available to TopicAuthorization
 * SIB0163.mp.2     171007 nyoung   Code Review Improvements.
 * 482749           141107 nyoung   FINDBUGS: Performance suggestions for .processor.matching.
 * 516346           010508 djvines  Use Arrays.toString
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.matching;

// Import required classes.
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;
import java.util.Set;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.ws.sib.processor.impl.ControllableProxySubscription;
import com.ibm.ws.sib.processor.impl.interfaces.DestinationHandler;

/**
 * The Neighbour flex handler is used to match inbound publications
 * to remote ME's and stores the list of matches.
 */
public class NeighbourFlexHandler
  implements MessageProcessorSearchResults.Handler
{
  // Standard trace boilerplate

  private static final TraceComponent tc =
    SibTr.register(
      NeighbourFlexHandler.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  TopicAuthorization authorization = null;

  /** Create a matching result object of the appropriate class for this handler.
   *
   * @return a result object of the appropriate type
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

  /** Reset a result object of the appropriate class for this handler
   *  to permit its re-use by another invocation of MessageProcessor.
   *
   * @param result the result object to reset
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

  /** Accumulate intermediate results for this handler while traversing
   *  MatchSpace.
   *
   * @param targets vector of MatchTarget to process
   * @param result result object provided by the initResult method of this particular
   * handler
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

  /** Complete processing of results for this handler after completely traversing
   *  MatchSpace.
   *
   *  ACL checking takes place at this point.
   *
   * @param results Vector of results for all handlers; results of MatchTarget types
   * whose index is less than that of this MatchTarget type have already been postprocessed.
   * @param index Index in results vector of accumulated results of this MatchTarget
   * type
   */
  public void postProcessMatches(DestinationHandler topicSpace,
                                 String topic,
                                 Object[] results,
                                 int index)
  {
	if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
	  SibTr.entry(tc, "postProcessMatches","results: "+Arrays.toString(results)+";index: "+index + ";topic"+topic);

	Set subRes = (Set) results[index];

	Iterator itr = subRes.iterator();

	if(authorization != null)
	{
	  // If security is enabled then we'll do the permissions check
      if(authorization.isBusSecure())
      {
        if(topicSpace != null &&
           topicSpace.isTopicAccessCheckRequired()) // Bypass security checks
        {
          while (itr.hasNext())
          {
            ControllableProxySubscription cps = (ControllableProxySubscription) itr.next();
			
            if(cps.isForeignSecuredProxy())
            {
              // The proxy sub originated in a foreign bus
              String userid = cps.getMESubUserId();
              if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, "Foreign bus proxy in a secured env for user, " + userid + ", on topic, " + topic);

              try
              {
                if(!authorization.
                    checkPermissionToSubscribe(topicSpace,
                                               topic,
                                               userid,
                                               (TopicAclTraversalResults)results[MessageProcessorMatchTarget.ACL_TYPE]))
                {
                  // Not authorized to subscribe to topic, need to remove from results
                  if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                      SibTr.debug(tc, "Not authorized, remove from PSOH results");
                  itr.remove();
                }
              }
              catch (Exception e)
              {
                // No FFDC code needed
              }
            }
          }
        }
      }
    }
    else
    {
      // Its a glitch and I need to feed this back. 	
    }
	
	if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
	  SibTr.exit(tc, "postProcessMatches");
  	
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MessageProcessorSearchResults.Handler#setAuthorization(com.ibm.ws.sib.processor.matching.TopicAuthorization)
   */
  public void setAuthorization(TopicAuthorization authorization)
  {
	if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
		SibTr.entry(tc, "setAuthorization","authorization: "+authorization);

	this.authorization = authorization;

	if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
	  SibTr.exit(tc, "setAuthorization");
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MessageProcessorSearchResults.Handler#toString(java.lang.Object[], int)
   */
  public String toString(Object results[], int index)
  {
    StringBuffer buffer = new StringBuffer(" neighb: [");

    Set cpss = (Set) results[index];

    Iterator itr = cpss.iterator();
    boolean first = true;
    while (itr.hasNext())
    {
      ControllableProxySubscription cps = (ControllableProxySubscription) itr.next();
      if(!first)
      {
        buffer.append(", ");
      }
      else
        first = false;
      buffer.append(cps);
    }
    buffer.append("]");
    return buffer.toString();
  }
}
