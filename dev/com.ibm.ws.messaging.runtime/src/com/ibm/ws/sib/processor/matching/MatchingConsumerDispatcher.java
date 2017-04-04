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
 * SIB0009.mp.02    220805 nyoung   Add support for Consumer Count monitoring
 * ===========================================================================
 */
 
package com.ibm.ws.sib.processor.matching;

import java.util.ArrayList;
import java.util.List;

import com.ibm.ws.sib.processor.impl.ConsumerDispatcher;

/**
 * @author Neil Young
 *
 * <p>The MatchingConsumerDispatcher class is a wrapper that holds a ConsumerDispatcher,
 * but allows a MatchTarget type to be associated with it for storage in the
 * MatchSpace. 

 */
public class MatchingConsumerDispatcher extends MessageProcessorMatchTarget
                                        implements MonitoredConsumer
{

  private ConsumerDispatcher consumerDispatcher;
  private boolean monitored = false;
  private boolean selector = false;
  private boolean wildcarded = false;
  private String topic = null;

  // A list of wildcarded registered monitors that match this subscription
  private ArrayList _matchingWildcardMonitorList;
  
  // A list of non-wildcarded registered monitors that match this subscription
  private ArrayList _matchingExactMonitorList;
  
  MatchingConsumerDispatcher(ConsumerDispatcher cd)
  {
    super(JS_SUBSCRIPTION_TYPE);  	
    consumerDispatcher = cd;
    _matchingWildcardMonitorList = new ArrayList();
    _matchingExactMonitorList = new ArrayList();
  }

  public boolean equals(Object o) 
  {
    boolean areEqual = false;
    if (o instanceof MatchingConsumerDispatcher)
    {
      ConsumerDispatcher otherCD = ((MatchingConsumerDispatcher) o).consumerDispatcher;
      
      if(consumerDispatcher.equals(otherCD))
        areEqual = true;
    }
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
	return consumerDispatcher;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#setMonitored()
   */
  public void setMonitored()
  {
    monitored = true;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#setWildcarded()
   */
  public void setWildcarded()
  {
    wildcarded = true;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#setSelector()
   */
  public void setSelector()
  {
    selector = true;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#isMonitored()
   */
  public boolean isMonitored()
  {
    return monitored;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#isWildcarded()
   */
  public boolean isWildcarded()
  {
    return wildcarded;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#isSelector()
   */
  public boolean isSelector()
  {
    return selector;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#getMonitoredTopicExprList()
   */
  public List getMatchingWildcardMonitorList()
  {
    return _matchingWildcardMonitorList;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#addMonitoredTopicExpr(java.lang.String)
   */
  public void addMatchingWildcardMonitor(String topicExpression)
  {
    _matchingWildcardMonitorList.add(topicExpression);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#addMonitoredTopicExprList(java.util.List)
   */
  public void setMatchingWildcardMonitorList(List topicExprList)
  {
    _matchingWildcardMonitorList.addAll(topicExprList);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#getMonitoredExactTopicExprList()
   */
  public List getMatchingExactMonitorList()
  {
    return _matchingExactMonitorList;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#addMonitoredExactTopicExprList(java.util.List)
   */
  public void setMatchingExactMonitorList(List topicExprList)
  {
    _matchingExactMonitorList.addAll(topicExprList);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#addMonitoredExactTopicExpr(java.lang.String)
   */
  public void addMatchingExactMonitor(String topicExpression)
  {
    _matchingExactMonitorList.add(topicExpression);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#removeMonitoredWildcardTopicExpr(java.lang.String)
   */
  public boolean removeMatchingWildcardMonitor(String topicExpression)
  {
    return _matchingWildcardMonitorList.remove(topicExpression);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#removeMonitoredExactTopicExpr(java.lang.String)
   */
  public boolean removeMatchingExactMonitor(String topicExpression)
  {
    return _matchingExactMonitorList.remove(topicExpression);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#setTopic(java.lang.String)
   */
  public void setTopic(String topic)
  {
    this.topic = topic;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.matching.MonitoredConsumer#getTopic()
   */
  public String getTopic()
  {
    return topic;
  }

}
