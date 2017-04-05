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
 * Reason            Date   Origin   Description
 * ---------------   ------ -------- ------------------------------------------
 *                          matrober Created on 04-Jul-2003
 * 175807            241003 matrober Migrate testEnv to jms.internals
 * 188373            260104 matrober Expose connected ME name through JMS (internals)
 * 197921.1          040504 jhumber  New Javadoc tags
 * 225815            200804 matrober ibm-spi tags in internal files
 * 354537            140306 holdeni  rework synchronizatoion to avoid deadlock closing connection
 *                                   while still consuming messages that throw exceptions
 * PK59962           280108 pbroad   Remove reportExceptionNoSync method
 * ============================================================================
 */
package com.ibm.ws.sib.api.jms;

import javax.jms.*;

/**
 * @author matrober
 *
 * This interface extends the regular Connection interface to provide
 * programmatic access to the unit test methods required to create and
 * delete destinations. Note that these will only function when the test
 * environment is being used.
 * 
 * This class is specifically NOT tagged as ibm-spi because by definition it is not
 * intended for use by either customers or ISV's.
 */
public interface JmsConnInternals extends Connection
{
  
  /**
   * This method is called when a JMS object wishes to pass an exception to the
   * ExceptionListener registered for this connection if one exists. This method
   * handles the serialization of calls to the ExceptionListener.
   * 
   * @param e The JMSException to be reported
   */
  public void reportException(JMSException e);

  /**
   * This method returns the name of the ME to which this Connection has been made.
   * Note that because of the decision making powers of TRM and WLM it may not always
   * be a trivial task to guess which ME a connection will be made to beforehand. 
   *    
   */
  public String getConnectedMEName();
}
