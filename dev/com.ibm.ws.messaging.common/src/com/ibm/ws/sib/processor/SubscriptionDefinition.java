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
 * 168985.1         110603 prmf     Initial drop
 * 184433           031203 cwilkin  add supportsMultipleConsumer modifiers
 * 187000           170304 astley   Remote durable pub/sub support
 * 207007.1         150604 nyoung   SelectionCriteria replaces selector and 
 *                                  discriminator on Core SPI.
 * SIB0010.mp.1     150705 cwilkin  Internal Subscriptions	
 * ===========================================================================
 */
 
package com.ibm.ws.sib.processor;


public interface SubscriptionDefinition {
    
    /**
     * Returns the destination.
     * @return String
     */
    public String getDestination();

    /**
     * Returns the selector.
     * @return String
     */
    public String getSelector();
    
    /**
     * Returns the messaging domain in which the selector was sspecified.
     * @return int
     */
    public int getSelectorDomain();
    
    /**
     * Returns the topic.
     * @return String
     */
    public String getTopic();

    /**
     * Returns the user.
     * @return String
     */
    public String getUser();

    /**
     * Sets the destination.
     * @param destination The destination to set
     */
    public void setDestination(String destination);

    /**
     * Sets the selector.
     * @param selector The selector to set
     */
    public void setSelector(String selector);

    /**
     * Sets the topic.
     * @param topic The topic to set
     */
    public void setTopic(String topic);

    /**
     * Sets the user.
     * @param user The user to set
     */
    public void setUser(String user);
    
    /**
     * Returns the noLocal.
     * @return boolean
     */
    public boolean isNoLocal();
    
    /**
     * Sets the noLocal.
     * @param noLocal The noLocal to set
     */
    public void setNoLocal(boolean noLocal);

    /**
     * Returns the supportsMultipleConsumers.
     * @return boolean
     */
    public boolean isSupportsMultipleConsumers();
    
    /**
     * Sets the supportsMultipleConsumers.
     * @param supportsMultipleConsumers The supportsMultipleConsumers to set
     */
    public void setSupportsMultipleConsumers(boolean supportsMultipleConsumers);
    
    /**
     * If this is a durable subscription, then get the name of the ME which
     * hosts it.
     * 
     * @return String Name of the ME.
     */
    public String getDurableHome();

    /**
     * Set the name of the ME which hosts ths
     * durable subscription.
     * 
     * @param uuid The name of the home ME.
     */
    public void setDurableHome(String uuid);
    
    /**
     * Set the name of the target destination for 
     * an internal subscription
     * 
     * @param String name of the the target destination
     */
    public void setTargetDestination(String target);
    
    /**
     * Get the name of the target destination for 
     * an internal subscription
     * @return String
     */
    public String getTargetDestination();
  }
    


