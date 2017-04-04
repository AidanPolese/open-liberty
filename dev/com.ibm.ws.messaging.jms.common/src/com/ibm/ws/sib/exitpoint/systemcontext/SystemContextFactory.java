/*
 * COMPONENT_NAME: sib.exitpoint.systemcontext
 *
 *  ORIGINS: 27
 *
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
 * 
 *
 * Change activity:
 *
 * Reason          Date        Origin   Description
 * --------------- ----------- -------- ----------------------------------------
 * d233061         20-Sep-2004 nottinga Initial Code Drop.
 * fSIB0006.ep.1   22-Sep-2005 nottinga Moving some code to SERV1.
 */
package com.ibm.ws.sib.exitpoint.systemcontext;

import java.lang.reflect.Constructor;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.mfp.JsApiMessage;
import com.ibm.ws.sib.utils.TraceGroups;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * <p>This class will create and return an instance of a SystemContext.</p>
 *
 * <p>SIB build component: sib.exitpoint.systemcontext</p>
 *
 * @author nottinga
 * @version 1.8
 * @since 1.0
 */
public final class SystemContextFactory
{
  /** The trace component for this class */
  private static final TraceComponent _tc = SibTr.register(SystemContextFactory.class, TraceGroups.TRGRP_EXITPOINT, null);
  
  /** The constructor for the SystemContextImpl */
  private static Constructor _constructor;
  
  

  /* ------------------------------------------------------------------------ */
  /* createSystemContext method                                    
  /* ------------------------------------------------------------------------ */
  /**
   * This class creates and returns an instance of SystemContext.
   * 
   * @param msg          The message.
   * @return             A systemcontext.
   * @throws NoSuchMethodError if the constructor cannot be located.
   * @throws SIErrorException  if the SystemContext cannot be created.
   */
  
  //lohith liberty change
  /*public static final SystemContext createSystemContext(JsApiMessage msg)
  {
    Object[] arguments = new Object[] {msg};
    if (_tc.isEntryEnabled()) SibTr.entry(_tc, "createSystemContext", arguments);
    
    SystemContext result; 
    
    try
    {
      result = (SystemContext) getConstructor().newInstance(arguments);
    }
    catch (Exception e)
    {
      com.ibm.ws.ffdc.FFDCFilter.processException(e, "com.ibm.ws.sib.exitpoint.systemcontext.SystemContextFactory.createSystemContext", "62");
      
      SIErrorException newE = new SIErrorException(e);
      
      if (_tc.isEntryEnabled()) SibTr.exit(_tc, "createSystemContext", newE);
      throw newE;
    }
    
    if (_tc.isEntryEnabled()) SibTr.exit(_tc, "createSystemContext", result);
    return result;
  }*/
  
  /* ------------------------------------------------------------------------ */
  /* getConstructor method                                    
  /* ------------------------------------------------------------------------ */
  /**
   * This method creates and returns the constructor of the SystemContextImpl.
   * 
   * @return The SystemContextImpl constructor.
   */
  private static Constructor getConstructor()
  {
    if (_tc.isEntryEnabled()) SibTr.entry(_tc, "getConstructor");
    
    if (_constructor == null)
    {
      try
      {
        Class clazz = Class.forName("com.ibm.ws.sib.exitpoint.systemcontext.SystemContextImpl");
        _constructor = clazz.getConstructor(new Class[] {JsApiMessage.class});
      }
      catch (Exception e)
      {
        com.ibm.ws.ffdc.FFDCFilter.processException(e, "com.ibm.ws.sib.exitpoint.systemcontext.SystemContextFactory.getConstructor", "66");
        
        NoSuchMethodError newE = new NoSuchMethodError();
        newE.initCause(e);
        
        if (_tc.isEntryEnabled()) SibTr.exit(_tc, "getConstructor", newE);
        throw newE;
      }
    }
    
    if (_tc.isEntryEnabled()) SibTr.exit(_tc, "getConstructor", _constructor);
    return _constructor;
  }
}
