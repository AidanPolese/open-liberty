/*
 * 
 * ===========================================================================
 * IBM Confidential OCO Source Material
 *
 * IBM WebSphere Advanced, v5.0
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ===========================================================================
 * Change activity:
 *
 * Reason         Date        Origin   Description
 * -------------- ----------- -------- -----------------------------------------
 * d278558         29-Jun-2005 nottinga Initial Code Drop.
 * d317480         37-Oct-2005 nottinga Updated for extension point move.
 */
package com.ibm.wsspi.jms;
 
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

/**
 * <p>This class provides support for setting a MessageListener on a 
 *   MessageConsumer, despite this not being allowed in a container. The intent
 *   is that this is only used by system code. It does not constitute an API.
 * </p>
 * 
 * <p>This class provides extenability points as the mechanism of setting a
 *   message listener may change based on provider and not all providers will
 *   be available at the time this code is written.
 * </p>
 *
 * <p>WAS build component: messaging</p>
 *
 * @author nottinga
 * @version 1.4
 * @since 1.0
 * @ibm-spi
 */
public class JmsMessageListenerSupport
{
  /**
   * <p>JMS providers implement this interface if they wish to allow 
   *   setMessageListener to be called by container services.
   * </p>
   * 
   * @ibm-spi
   */
  public interface MessageListenerSetter
  {
    /* ---------------------------------------------------------------------- */
    /* setMessageListener method                                    
    /* ---------------------------------------------------------------------- */
    /**
     * This method sets the supplied message listener on the supplied consumer.
     * 
     * @param consumer The consumer to set the message listener on.
     * @param listener The message listener to set on the conusmer.
     * @throws JMSException If the message listener cannot be set.
     */
    public void setMessageListener(MessageConsumer consumer, MessageListener listener) throws JMSException;
  }
  }

