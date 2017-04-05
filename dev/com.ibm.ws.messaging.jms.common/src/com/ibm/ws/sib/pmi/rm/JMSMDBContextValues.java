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
* ---------------  ------ -------- -------------------------------------------
* SIB0025.pmir.3   260805 ajw      Refactor and Registration support
* SIB0025.pmir.4   140905 ajw      Add guards to context and transaction info
* 317787.1         031105 ajw      New provider context and isFilterPassed call
* 333814		       191205 ajw 	   Use Default Messaging instead of JetStream
* 335634           060106 ajw      Changed JmsDestiantion param to destination name
* 395641           271106 ajw      Changed context names
*/
package com.ibm.ws.sib.pmi.rm;

/**
 * Cclass to be used by instrumentation points in the JMS layer to create
 * the context values needed on methods on SIBPmiRm class. If the component is
 * not JMS and a MDB this class should not be used.
 */
public class JMSMDBContextValues
{
  private String destinationName;
  private String messageSelector;
  private final String provider = "Default Messaging";

  /**
   * Constructor to create the context values. This should only be invoked
   * when the <code>isComponentEnabled(int)</code> returns true
   * 
   * @param DestinationName
   * @param MessageSelector
   */
  public JMSMDBContextValues(String destinationName, String messageSelector)
  {
    if (destinationName == null)
    {
      this.destinationName = "";
    }
    else
    {
      this.destinationName = destinationName;
    }
    
    if (messageSelector == null)
      messageSelector = "";
    
    this.messageSelector = messageSelector;
  }

  /**
   * @return String[] the values set in the constructor
   */
  public String[] getContextValues()
  {
    return new String[] { destinationName, messageSelector, provider};
  }
  
  /**
   * Returns the MDB context names that will be used for registration
   * of the JMS component
   * @return
   */
  public static String[] getContextNames()
  {
    String[] contextNames = {"DestinationName", "MessageSelector", "Provider" };
    return contextNames;
  }
}
