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
 * 186334            050104 matrober Standalone ME coding
 * 186967.7.7       300304 millwood Use new TRM LinkManager interface
 * 215608.4         040804 millwood Change to start() method of JsEngineComponent
 * 406709           201106 tevans   Allow the MessageStore to be started independently
 * 409469           090107 tevans   Fix RMQ unittests
 * ============================================================================
 */
package com.ibm.ws.sib.processor.test;

import com.ibm.ws.sib.admin.JsConstants;
import com.ibm.ws.sib.msgstore.MessageStore;

/**
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 * 
 * @author matrober
 */
public class SIMPJsStandaloneFactoryImpl extends SIMPJsStandaloneFactory
{
  
  private SIMPJsStandaloneEngine _me ;
  
  public SIMPJsStandaloneEngine createNewMessagingEngine(
          String busName,
          String engineName,
          boolean clean,
          boolean initTrm) throws Exception
  {
    
    // Instantiate a standalone version of an ME
    if (initTrm)
    {
      _me = new SIMPJsStandaloneEngineImpl(busName, engineName);
    }
     
    
    // Initialize and start the ME
    _me.initialize(_me, clean, initTrm);
    _me.start(JsConstants.ME_START_DEFAULT);
    
    return _me;
    
  }
  
  public MessageStore createMessageStoreOnly(
      String busName,
      String engineName,
      boolean clean,
      boolean initTrm) throws Exception
  {
//  Instantiate a standalone version of an ME
    if (initTrm)
    {
      _me = new SIMPJsStandaloneEngineImpl(busName, engineName);
    }
    
    return _me.createMessageStoreOnly(clean);
  }
  
  /**
   * Returns the _me.
   * @return SIMPJsStandaloneEngineImpl
   */
  public SIMPJsStandaloneEngine get_me()
  {
    return _me;
  }

}
