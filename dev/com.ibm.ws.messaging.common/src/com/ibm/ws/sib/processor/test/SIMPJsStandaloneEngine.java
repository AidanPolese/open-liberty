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
 * 185792                  matrober Created on 16-Dec-2003
 * 181718.4         221203 gatfora  Move to the new UUID classes
 * 186445.3         200104 millwood Move to admin destination definition
 * 186445.4         230104 millwood Move to new JsMessagingEngine interfac
 * 193399           050304 gatfora  Updates to the JsMessageEngine interface
 * 186967.7.1       120304 millwood Add new ForeignBusDefinition and VirtualLinkDef.. support
 * 186967.7.7       300304 millwood Use new TRM LinkManager interface
 * 199152           200404 gatfora  Correct javadoc.
 * 181851.11.2.1    060504 caseyj   Report global error on MP startup corruption
 * 215608.4         040804 millwood Change to start() method of JsEngineComponent
 * 236261           140105 gatfora  DynamicConfig concurency enablement for Mediations
 * 406709           201106 tevans   Allow the MessageStore to be started independently
 * SIB0125.adm.3    190107 leonarda RCS setConfig()
 * ============================================================================
 */
package com.ibm.ws.sib.processor.test;


import com.ibm.ws.sib.admin.JsEObject;
import com.ibm.ws.sib.admin.JsEngineComponent;
import com.ibm.ws.sib.admin.JsHealthMonitor;
import com.ibm.ws.sib.admin.JsMessagingEngine;
import com.ibm.ws.sib.msgstore.MessageStore;
import com.ibm.ws.sib.trm.TrmMeMain;
import com.ibm.ws.util.ThreadPool;

/**
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 * 
 * @author matrober
 */
public interface SIMPJsStandaloneEngine 
  extends JsMessagingEngine, JsHealthMonitor
{
  public abstract JsEngineComponent getTRM();
  public abstract void setTRM(TrmMeMain trm);

  public abstract void initialize(
    JsMessagingEngine engine,
    boolean clean,
    boolean reintTRM)
    throws Exception;
  public abstract void start(int mode);
  public abstract void stop(int mode);
  public abstract void destroy();
  public abstract void setCustomProperty(String name, String value);
  public abstract void setConfig(JsEObject config);

  public abstract MessageStore createMessageStoreOnly(boolean clean) throws Exception;
  
  /**
   * This method carries out the necessary actions to initialise the MessageProcessor
   * associated with this engine. By including this method here we avoid the need
   * to expose the MessageProcessor class as an interface. 
   *
   */
  public abstract void initializeMessageProcessor();
  
  
  /** Return the thread pool for mediations */
  public abstract ThreadPool getMediationThreadPool();
  
  /** Set the mediation threadpool */
  public abstract void setMediationThreadPool(ThreadPool pool);

}
