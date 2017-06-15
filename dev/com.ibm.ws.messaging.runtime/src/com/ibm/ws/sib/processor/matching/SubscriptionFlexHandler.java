/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.ibm.ws.sib.processor.matching;

// Import required classes.
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.ws.sib.processor.impl.ConsumerDispatcher;
import com.ibm.ws.sib.processor.matching.TopicAuthorization;
import com.ibm.ws.sib.processor.impl.interfaces.DestinationHandler;
/**
 * @author nyoung
 *
 */
public class SubscriptionFlexHandler
implements MessageProcessorSearchResults.Handler
{
 // Standard trace boilerplate

  private static final TraceComponent tc = SibTr.register(SubscriptionFlexHandler.class,
    SIMPConstants.MP_TRACE_GROUP,
    SIMPConstants.RESOURCE_BUNDLE);		

  TopicAuthorization authorization = null;

  //------------------------------------------------------------------------------
  // Method: DataFlowFlexHandler.initResult
  //------------------------------------------------------------------------------
  /** Create a matching result object of the appropriate class for this handler.
   *
   * @return a result object of the appropriate type
   *///---------------------------------------------------------------------------
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
   *///---------------------------------------------------------------------------
  public void resetResult(Object result)
  {

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
	  SibTr.entry(tc, "resetResult");  	

    if (result != null)
      ((HashSet)result).clear();

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
   *///---------------------------------------------------------------------------
  public void processIntermediateMatches(List targets, Object result)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
	  SibTr.entry(tc, "processIntermediateMatches","targets: "+targets+";results: "+result);

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
   *///---------------------------------------------------------------------------
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
            MatchingConsumerDispatcher mcd = (MatchingConsumerDispatcher) itr.next();
			      ConsumerDispatcher cd = mcd.getConsumerDispatcher();
            String userid = cd.getConsumerDispatcherState().getUser();
            // We test whether the user is privileged only driving the full
            // permission check if not.
            if(!cd.getConsumerDispatcherState().isSIBServerSubject())
            {
              try
              {
            	if(!authorization.
                      checkPermissionToSubscribe(topicSpace,
                                                 topic,
                                                 userid,
                                                 (TopicAclTraversalResults)results[MessageProcessorMatchTarget.ACL_TYPE]))
                {
                  // Not authorized to subscribe to topic, need to remove from results
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
    StringBuffer buffer = new StringBuffer(" subs: [");

    Set mcds = (Set) results[index];

    Iterator itr = mcds.iterator();
    boolean first = true;
    while (itr.hasNext())
    {
      MatchingConsumerDispatcher mcd = (MatchingConsumerDispatcher) itr.next();
      if(!first)
      {
        buffer.append(", ");
      }
      else
        first = false;
      buffer.append(mcd);
    }
    buffer.append("]");
    return buffer.toString();
  }
}
