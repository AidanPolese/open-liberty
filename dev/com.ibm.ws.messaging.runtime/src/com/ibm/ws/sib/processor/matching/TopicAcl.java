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
 * 246746           081204 gatfora  Removal of unused code.
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.matching;

import java.security.Principal;

/**
 * @author Neil Young
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TopicAcl extends MessageProcessorMatchTarget
{ 
  private String topic;
  
  private int operationType;

  private Principal principal;
  
	/**
	 * Constructor for TopicAcl.
	 * @param type
	 */
	public TopicAcl(String topic, 
                  int operationType, 
                  Principal principal) 
  {
		super(ACL_TYPE);
    this.topic = topic;
    this.operationType = operationType;
    this.principal = principal;
  }
  
  public String toString()
  {
    String theString = topic + ", " + operationType + ", ";
    if(principal == null)
    {
      theString = theString + "INHERIT-BLOCKER";
    }
    else
    {
      theString = theString + principal.toString();
    }
    return theString;
  }


/**
 * Returns the operationType.
 * @return int
 */
public int getOperationType() {
	return operationType;
}

/**
 * Returns the principal.
 * @return Principal
 */
public Principal getPrincipal() {
	return principal;
}

/**
 * Returns the topic.
 * @return String
 */
public String getTopic() {
	return topic;
}

}
