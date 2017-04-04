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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 169897.0        240603 jroots   Updates for Milestone 3 Core API
 * 171399          110703 tevans   Add int values with getter methods
 * 173284          010803 tevans   getDestinationType should be static
 * 181796.0        041103 jroots   Move to com.ibm.wsspi
 * 192759          090304 jroots   Milestone 7 Core SPI changes
 * 195758.0        050404 jroots   Milestone 7.5 Core SPI changes
 * 241350          251104 gatfora  Creation of an Unknown destination type for Foreign Bus.
 * 276259           130505 dware    Improve security related javadoc
 * ============================================================================
 */
 
package com.ibm.wsspi.sib.core;

/**
 DestinationType is a "Java typesafe enum", the values of which represent 
 different types of destination. It can be used by an application to make sure 
 that the destination the app is using is of the type that it is expecting.
 <p>
 This class has no security implications.
*/
public class DestinationType {
	
  /**
   A destination of type Queue is indicated by the value DestinationType.QUEUE
  */
  public final static DestinationType QUEUE 
  	= new DestinationType("Queue",0);
  
  /**
   A destination of type TopicSpace is indicated by the value 
   DestinationType.TOPICSPACE
  */
  public final static DestinationType TOPICSPACE 
  	= new DestinationType("TopicSpace",1);
  
  /**
   A destination of type Service is indicated by the value 
   DestinationType.SERVICE
  */
  public final static DestinationType SERVICE 
    = new DestinationType("Service",2);
  
  /**
   A destination of type Port is indicated by the value 
   DestinationType.PORT
  */
  public final static DestinationType PORT 
    = new DestinationType("Port",3);

  /**
   A destination of type Unknown is indicated by the value 
   DestinationType.UNKNOWN
   This will be used to describe destinations which are 
   foreign.
  */
  public final static DestinationType UNKNOWN 
    = new DestinationType("Unknown",4);
  
  /**
   Returns a string representing the DestinationType value 
   
   @return a string representing the DestinationType value
  */
  public final String toString() {
  	return name;
  }
  
  /**
   * Returns an integer value representing this DestinationType
   * 
   * @return an integer value representing this DestinationType
   */
  public final int toInt()
  {
    return value;
  }
  
  /**
   * Get the DestinationType represented by the given integer value;
   * 
   * @param value the integer representation of the required DestinationType
   * @return the DestinationType represented by the given integer value
   */
  public final static DestinationType getDestinationType(int value)
  {
    return set[value];
  }
  
  private final String name;
  private final int value;  
  private final static DestinationType[] set = {QUEUE,
                                                TOPICSPACE,
                                                SERVICE,
                                                PORT,
                                                UNKNOWN
                                                };	
  private DestinationType(String name, int value) {
  	this.name = name;
    this.value = value;
  }
}

