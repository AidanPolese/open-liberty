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
 * 418673           080207 tevans   Tidy up standalone me
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.test.utils;

import com.ibm.ws.sib.admin.JsEngineComponent;

// Object to represent a component within this ME
public class ComponentList
{
  private String _className;
  private JsEngineComponent _componentRef;

  public ComponentList(String className, JsEngineComponent c)
  {
    _className = className;
    _componentRef = c;
  }

  // Get the name of the class
  public String getClassName()
  {
    return _className;
  }

  // Get a reference to the instantiated class
  public JsEngineComponent getRef()
  {
    return _componentRef;
  }
}