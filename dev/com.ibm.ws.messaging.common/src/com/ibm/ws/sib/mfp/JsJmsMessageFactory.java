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
 * 158444          030207 susana   Original
 * 158589          030303 susana   Move factory creation to first getInstance()
 * 166493          030515 susana   Move factory creation back to clinit
 * 166631          030519 susana   Add Exceptions
 * 167577          030922 susana   Change Tr calls to SibTr
 * 189857          040206 susana   Add 'No FFDC code needed' comment
 * 195445.26       040514 susana   Change message prefix from SIFP to CWSIF
 * 240085          051018 kgoodson FFDC on class init failure
 * 442933          070601 susana   Add trace guard
 * ============================================================================
 */
package com.ibm.ws.sib.mfp;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 *  A singleton JsJmsMessageFactory is created at static initialization
 *  and is subsequently used for the creation of all new JmsMessages
 *  of any type.
 *  <p>
 *  Inbound messages are not created using this factory as they are
 *  created from an existing JsMessage using the methods provided by
 *  the JsMessage interface.
 *
 */
public abstract class JsJmsMessageFactory {

  private static TraceComponent tc = SibTr.register(JsJmsMessageFactory.class, MfpConstants.MSG_GROUP, MfpConstants.MSG_BUNDLE);

  private static JsJmsMessageFactory instance = null;
  private static Exception  createException = null;

  static {
    
    /* Create the singleton factory instance                                  */
    try {
      createFactoryInstance();
    }
    catch (Exception e) {
      FFDCFilter.processException(e, "com.ibm.ws.sib.mfp.JsJmsMessageFactory.<clinit>", "62");
      createException = e;
    }
  }

  /**
   *  Get the singleton JsJmsMessageFactory which is to be used for
   *  creating JsJmsMessage instances.
   *
   *  @return The JsJmsMessageFactory
   *
   *  @exception Exception The method rethrows any Exception caught during
   *                       creaton of the singleton factory.
   */
  public static JsJmsMessageFactory getInstance() throws Exception {

    /* If instance creation failed, throw on the Exception                    */
    if (instance == null) {
      throw createException;
    }

    /* Otherwise, return the singleton                                        */
    return instance;
  }

  /**
   *  Create a new, empty null-bodied JMS Message.
   *  To be called by the API component.
   *
   *  @return The new JsJmsMessage
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract JsJmsMessage createJmsMessage() throws MessageCreateFailedException;

  /**
   *  Create a new, empty JMS BytesMessage.
   *  To be called by the API component.
   *
   *  @return The new JsJmsBytesMessage
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract JsJmsBytesMessage createJmsBytesMessage() throws MessageCreateFailedException;

  /**
   *  Create a new, empty JMS MapMessage.
   *  To be called by the API component.
   *
   *  @return The new JsJmsMapMessage
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract JsJmsMapMessage createJmsMapMessage() throws MessageCreateFailedException;

  /**
   *  Create a new, empty JMS ObjectMessage.
   *  To be called by the API component.
   *
   *  @return The new JsJmsObjectMessage
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract JsJmsObjectMessage createJmsObjectMessage() throws MessageCreateFailedException;

  /**
   *  Create a new, empty JMS StreamMessage.
   *  To be called by the API component.
   *
   *  @return The new JsJmsStreamMessage
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract JsJmsStreamMessage createJmsStreamMessage() throws MessageCreateFailedException;

  /**
   *  Create a new, empty JMS TextMessage.
   *  To be called by the API component.
   *
   *  @return The new JsJmsTextMessage
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract JsJmsTextMessage createJmsTextMessage() throws MessageCreateFailedException;

  /**
   *  Create the singleton Factory instance.
   *
   *  @exception Exception The method rethrows any Exception caught during
   *                       creaton of the singleton factory.
   */
  private static void createFactoryInstance() throws Exception {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "createFactoryInstance");
    try {
      Class cls = Class.forName(MfpConstants.JS_JMS_MESSAGE_FACTORY_CLASS);
      instance = (JsJmsMessageFactory) cls.newInstance();
    }
    catch (Exception e) {
      FFDCFilter.processException(e, "com.ibm.ws.sib.mfp.JsJmsMessageFactory.createFactoryInstance", "133");
      SibTr.error(tc,"UNABLE_TO_CREATE_JMSFACTORY_CWSIF0011",e);
      throw e;
    }
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "createFactoryInstance");
  }

}
