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
 * 182745.8.3       190704 nyoung   Add support for z/OS WLM classifier.
 * 182745.8.3.1     270704 nyoung   Add support for parseDiscriminator.
 * 223281           110804 gatfora  Removing SIException from parseSelector method.
 * 314292           161005 nyoung   Support WSN wildcard topic matching.
 * ===========================================================================
 */
package com.ibm.ws.sib.processor;

import com.ibm.wsspi.sib.core.SelectorDomain;
import com.ibm.wsspi.sib.core.SIBusMessage;
import com.ibm.wsspi.sib.core.exception.SIDiscriminatorSyntaxException;
import com.ibm.wsspi.sib.core.exception.SISelectorSyntaxException;
import com.ibm.ws.sib.matchspace.Selector;

  /**
   A SelectorEvaluator is used to parse selector expressions and evaluate them against
   an SIBusMessage.
   */
  public interface MPSelectorEvaluator 
  {

    /**
     * Method parseSelector
     * Used to parse the string representation of a selector into a MatchSpace selector tree.
     * @param selectorString
     * @param domain
     */

    public Selector parseSelector(String selectorString,
                                  SelectorDomain domain)
      throws SISelectorSyntaxException;
      
    /**
     * Method parseDiscriminator
     * Used to parse the string representation of a discriminator into a MatchSpace selector tree.
     * @param discriminator
     */      
    public Selector parseDiscriminator(String discriminator)
      throws SIDiscriminatorSyntaxException;
            
    /**
     * Method evaluateMessage
     * Used to evaluate a parsed selector expression against a message.
     * @param selectorTree
     * @param msg
     */

    public boolean evaluateMessage(
      Selector selectorTree,
      Selector discriminatorTree,
      SIBusMessage msg);
      
    /**
     * Method evaluateDiscriminator
     * 
     * Used to determine whether a supplied fully qualified discriminator matches
     * a supplied wildcarded discriminator expression.
     * 
     * @param fullTopic
     * @param wildcardTopic
     * @return
     * @throws SIDiscriminatorSyntaxException
     */
    public boolean evaluateDiscriminator(
      String fullTopic,
      String wildcardTopic)
    throws SIDiscriminatorSyntaxException;          
  }
