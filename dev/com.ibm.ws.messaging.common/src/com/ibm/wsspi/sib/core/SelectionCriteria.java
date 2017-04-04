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
 * 207007           140704 jroots   Original
 * 219476.0         240804 jroots   Consolidated Z3 Core SPI changes
 * 276259           130505 dware    Improve security related javadoc
 * 504438.2         270308 nyoung   Flow XPath namespace info through Core and MP
 * 504438.5         220408 nyoung   Demote SelectionCriteria Core SPI change to MP 
 * ===========================================================================
 */
package com.ibm.wsspi.sib.core;

  /**
   A SelectionCriteria object can be used when creating a ConsumerSession or
   BrowserSession, to support durable subscription creation or when using the 
   receive methods of SICoreConnection, to indicate that messages are to be 
   selected according a message properties selector expression and/or a 
   discriminator string. 
   <p> 
   A SelectionCriteria object is created by calling 
   SelectionCriteriaFactory.createMessageSelector. (Note that only SelectionCriteria 
   objects returned from SelectionCriteriaFactory.createMessageSelector may be used; 
   the Core SPI user cannot provide their own SelectionCriteria implementation.)  
   <p>
   Property naming is not identical between the JMS API and the SIMessage API, 
   briefly:
   <ul>
   <li> JMS supports property names of JMSxxxxx or xxxxxx (where xxxxxx are any 
   user property)
   <li>Core SPI supports names of SI_xxxxx, JMSxxxxx or user.xxxxxx
   </ul>
   The SelectionCriteria encapsulates the string selector expression and a 
   SelectorDomain attribute to indicate in which messaging context the selector
   was provided. The SelectorDomain class is also provided on the Core SPI. In
   addition the SelectionCriteria encapsulates a discriminator string. 
  <p>
  This class has no security implications.
   */
  public interface SelectionCriteria 
  {

  	/**
  	 * Returns the discriminator used in matching. The Javadoc overview document 
  	 * for this package describes the syntax and interpretation of discriminators
  	 * in the Core SPI.
  	 *   
  	 * @return the discriminator
  	 */
	public String getDiscriminator();
	
	/**
	 * Setsthe discriminator used in matching. The Javadoc overview document 
	 * for this package describes the syntax and interpretation of discriminators
	 * in the Core SPI.
	 *   
	 * @param discriminator
	 */
	public void setDiscriminator(String discriminator);
	
	/**
	 * Returns the selector string used in matching. The selector is interpreted
	 * according to the value of the SelectorDomain property. Selectors are 
	 * described in the Javadoc overview document for this package.
	 *   
	 * @return the selector string
	 */
	public String getSelectorString();
	
	/**
	 * Sets the selector string used in matching. The selector is interpreted
	 * according to the value of the SelectorDomain property. Selectors are 
	 * described in the Javadoc overview document for this package.
	 *   
	 * @param selectorString
	 */
	public void setSelectorString(String selectorString);
	
	/**
	 * Returns the selector domain that will be used to interpret the selector
	 * string. Selectors are described in the Javadoc overview document for this 
	 * package.
	 *   
	 * @return the selector domain
	 */
	public SelectorDomain getSelectorDomain();
	
	/**
	 * Sets the selector domain that will be used to interpret the selector
	 * string. Selectors are described in the Javadoc overview document for this 
	 * package.
	 *   
	 * @param selectorDomain
	 */
	public void setSelectorDomain(SelectorDomain selectorDomain);
	
  }
