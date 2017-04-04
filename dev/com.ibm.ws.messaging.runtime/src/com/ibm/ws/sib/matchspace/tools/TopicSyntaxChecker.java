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
 * SIB0155.mspac.2  150605 nyoung   Alternate topic syntax checkers
 * ===========================================================================
 */

package com.ibm.ws.sib.matchspace.tools;

import com.ibm.ws.sib.matchspace.InvalidTopicSyntaxException;

/** Implementations of this interface check the syntax of topic expressions where a 
 * topic is used as root Identifier in a MatchSpace.
 * 
 **/

public interface TopicSyntaxChecker 
{
	  /** checkTopicSyntax: Rules out syntactically inappropriate wildcard usages and
	   * determines if there are any wildcards
	   * @param topic the topic to check
	   * @return true if topic contains wildcards 
	   * @throws InvalidTopicSyntaxException if topic is syntactically invalid
	   */
	  public boolean checkTopicSyntax(String topic)
	    throws InvalidTopicSyntaxException;

	  /**Checks the topic for any wildcards as a Event topic can not 
	   * contain wildcard characters.
	   * 
	   * @param topic  The topic to be checked
	   *
	   * @throws InvalidTopicSyntaxException if topic is syntactically invalid
	   */
	  public void checkEventTopicSyntax(String topic)
	    throws InvalidTopicSyntaxException;
}

