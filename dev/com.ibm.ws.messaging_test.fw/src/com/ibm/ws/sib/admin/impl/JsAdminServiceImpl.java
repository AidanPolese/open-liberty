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
 * Reason           Date  Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 166833.19        150104 gatfora  Initial
 * 186980           150104 caseyj   Disable stats if not in WAS or MP unit tests
 * 190015           090204 caseyj   Implement getMessagingEngineSet()
 * 166833.20        160204 gatfora  Move to getMessagingEngineSet to list all ME's on the bus
 * 186484.2         170304 tevans   Some intial controllable interfaces
 * 196440           300304 caseyj   Do JsAdminService changes in MPs own impl
 * 199749           230404 gatfora  Request/Reply for Publish Subscribe messages.
 * 196277.14        290404 millwood delay 2nd instance of mediation from starting
 * 186484.9         060504 tevans   Extended runtime control implementation
 * 219534.3         060804 millwood Add new method isStandaloneServer()
 * 239367           151004 gatfora  Add new Jmx methods defined by interface.
 * 250746           190105 gatfora  Remove unthrown exception declarations
 * 313843           041105 nottinga added getDefinedBus method.
 * 429651           300307 leonarda add listDefinedBuses()
 * ===========================================================================
 */
package com.ibm.ws.sib.admin.impl;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.ibm.ws.sib.admin.JsAdminService;
import com.ibm.ws.sib.admin.JsBus;
import com.ibm.ws.sib.admin.JsEngineComponent;
import com.ibm.ws.sib.admin.JsMain;
import com.ibm.ws.sib.admin.JsMessagingEngine;
import com.ibm.ws.sib.admin.JsProcessComponent;
import com.ibm.ws.sib.utils.SIBUuid8;

/**

 */
public class JsAdminServiceImpl extends JsAdminService  
{
  public Vector messagingEngines; 
  private HashMap busses;
  
  public JsAdminServiceImpl()
  {
    busses = new HashMap();
  }
  
  public void reset()
  {
    busses = new HashMap();
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#setAdminMain(com.ibm.ws.sib.admin.JsMain)
   */
  public void setAdminMain(JsMain arg0)
  {
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#getAdminMain()
   */
  public JsMain getAdminMain() throws Exception
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#isInitialized()
   */
  public boolean isInitialized()
  {
    // True so that statistic tests use our mimic StatsFactory rather than
    // not registering with PMI.
    return true;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#getProcessComponent(java.lang.String)
   */
  public JsProcessComponent getProcessComponent(String arg0)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#listMessagingEngines()
   */
  public Enumeration listMessagingEngines()
  {
    return null;
  }
  
  /**
   * Sets a neighbour for restart.
   * @param uuid
   * @param busName
   */
  public void setNeighbour(SIBUuid8 uuid, String busName)
  {
    HashSet set = (HashSet)busses.get(busName);
    
    if (set == null)
      set = new HashSet();
    
    busses.put(busName, set);
    
    set.add(uuid.toString());
    
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#listMessagingEngines(java.lang.String)
   */
  public Enumeration listMessagingEngines(String arg0)
  {
    return null;
  }
  
  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#getMessagingEngineSet(java.lang.String)
   */
  public Set getMessagingEngineSet(String busName) 
  {
    return (Set)busses.get(busName);
  }  

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#getMessagingEngine(java.lang.String, java.lang.String)
   */
  public JsMessagingEngine getMessagingEngine(String arg0, String arg1)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#getMessageProcessor(java.lang.String)
   */
  public JsEngineComponent getMessageProcessor(String arg0)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#activateJMSResource()
   */
  public void activateJMSResource()
  {
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#deactivateJMSResource()
   */
  public void deactivateJMSResource()
  {
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#getService(java.lang.Class)
   */
  public Object getService(Class arg0)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#getBus(java.lang.String)
   */
  public JsBus getBus(String name)
  {
    return null;
  }
  
  /**
   * @see JsAdminService#getDefinedBus(String)
   */
  public JsBus getDefinedBus(String name)
  {
    return null;
  }

  public List<String> listDefinedBuses()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#isStandaloneServer()
   */
  public boolean isStandaloneServer()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#quoteJmxPropertyValue(java.lang.String)
   */
  public String quoteJmxPropertyValue(String arg0)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.JsAdminService#unquoteJmxPropertyValue(java.lang.String)
   */
  public String unquoteJmxPropertyValue(String arg0)
  {
    return null;
  }
}
