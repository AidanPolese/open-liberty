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
 * SIB0211.comms.1 070104 mleming  Original
 * 464715.1        080718 timoward Allow MQLink to dynamically reload config
 * ============================================================================
 */
package com.ibm.ws.sib.comms.mq;

import com.ibm.websphere.sib.exception.SIException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.admin.JsMessagingEngine;
import com.ibm.ws.sib.admin.MQLinkDefinition;

/**
 * Interface to an object that represents an MQLink. 
 * Allows MP to perform 'admin' style operations on an MQLink.
 * 
 * An instance of an object that implements MQLinkObject can be obtained by calling MQLinkManager.create(). 
 * For more information @see com.ibm.ws.sib.comms.mq.MQLinkManager#create(MQLinkDefinition, MQLinkLocalization, MBeanFactory, boolean)
 * 
 * @author matt
 */
public interface MQLinkObject 
{
   /**
    * Allows MP to alert the MQLink component when it has finished configuring resources for a specific MQLink.
    * 
    * @param startMode the mode the ME is starting up in
    * @param me the messaging engine
    * 
    * @throws SIResourceException
    * @throws SIException
    */
   public void mpStarted(int startMode, JsMessagingEngine me) throws SIResourceException, SIException;

   /**
    * Tells the MQLink referenced by this MQLinkObject to perform required ME stop time processing
    * 
    * @throws SIResourceException
    * @throws SIException
    */
   public void stop() throws SIResourceException, SIException;

   /**
    * Tells the MQLink referenced by this MQLinkObject to perform required ME destroy time processing
    * 
    * @throws SIResourceException
    * @throws SIException
    */
   public void destroy() throws SIResourceException, SIException;

   /**
    * Tells the MQLink referenced by this MQLinkObject that a dynamic config update has occured.
    * The config changes are supplied in the MQLinkDefinition object.
    * 
    * @param linkDefinition
    * @throws SIResourceException
    * @throws SIException
    */
   public void update(MQLinkDefinition linkDefinition) throws SIResourceException, SIException;
   
   /**
    * Tells the MQLink referenced by this MQLinkObject that a dynamic config update has
    * occurred, but only at the bus scope. This means that there is no MQLinkDefinition,
    * but we should reload bus scoped config.
    */
   public void busReloaded();
}
