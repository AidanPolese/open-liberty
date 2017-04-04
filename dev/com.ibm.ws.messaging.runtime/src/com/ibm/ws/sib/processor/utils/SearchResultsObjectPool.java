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
 * Reason           Date  Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 181502           311003 gatfora  Original
 * 184185.1.6       270404 nyoung   Enable delivery time discriminator access checks.
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.utils;

import com.ibm.ws.sib.processor.matching.MessageProcessorSearchResults;
import com.ibm.ws.sib.processor.matching.TopicAuthorization;
import com.ibm.ws.util.ObjectPool;

/**
 * The Search Results object pool is responsible for creating
 * a MessageProcessorSearchResults when one can't be 
 * obtained from the object pool
 */
public final class SearchResultsObjectPool extends ObjectPool
{

  /** Support for discriminator access control */
  private TopicAuthorization topicAuthorization;

  /**
   * @param name  The name of the object pool
   * @param size  The size of the object pool
   */
  public SearchResultsObjectPool(String name, int size)
  {
    super(name, size);
  }
  
  protected Object createObject()
  {
    return new MessageProcessorSearchResults(topicAuthorization);
  }

  /**
   * @param authorization
   */
  public void setTopicAuthorization(TopicAuthorization authorization) 
  {
	  topicAuthorization = authorization;
  }

}
