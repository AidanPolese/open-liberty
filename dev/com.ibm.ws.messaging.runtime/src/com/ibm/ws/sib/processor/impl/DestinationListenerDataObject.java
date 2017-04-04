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
 * 195461.2         290404 ajw      Dynamic destination support for core SPI inbound RA
 * 217733           220704 gatfora  Improve trace logging.
 * SIB0137.mp.1     230507 nyoung   addDestinationListener destination Name pattern matching
 * ============================================================================
 */
package com.ibm.ws.sib.processor.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.DestinationAvailability;
import com.ibm.wsspi.sib.core.DestinationListener;
import com.ibm.wsspi.sib.core.DestinationType;
import com.ibm.wsspi.sib.core.SICoreConnection;

/**
 * Data Object to hold info about the destinationListener
 * 
 */
public class DestinationListenerDataObject
{
  private static final TraceComponent tc =
    SibTr.register(DestinationListenerDataObject.class, SIMPConstants.MP_TRACE_GROUP, SIMPConstants.RESOURCE_BUNDLE);

  private DestinationListener destinationListener;
  private DestinationAvailability destinationAvailability;
  private DestinationType destinationType;
  private DestinationNamePattern destinationNamePattern;
  private SICoreConnection connection;

  /**
   * Constructor
   * 
   * @param destinationListener
   * @param destinationType
   * @param destinationAvailability
   * @param connection
   */
  protected DestinationListenerDataObject(
    DestinationListener destinationListener,
    DestinationNamePattern destinationNamePattern,
    DestinationType destinationType,
    DestinationAvailability destinationAvailability,
    SICoreConnection connection)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(
        tc,
        "DestinationListenerDataObject",
        new Object[] { destinationListener, destinationNamePattern, destinationType, destinationAvailability, connection });

    this.destinationAvailability = destinationAvailability;
    this.destinationListener = destinationListener;
    this.destinationNamePattern = destinationNamePattern;
    this.destinationType = destinationType;
    this.connection = connection;

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "DestinationListenerDataObject", this);
  }

  protected void setDestinationListener(DestinationListener destinationListener)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(
        tc,
        "setDestinationListener",
        new Object[] { destinationListener });
    }
    
    this.destinationListener = destinationListener;

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.exit(tc, "setDestinationListener");
    }
  }

  protected void setDestinationAvailability(DestinationAvailability destinationAvailbility)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(tc, "setDestinationAvailability", new Object[] { destinationAvailability });
    }

    this.destinationAvailability = destinationAvailbility;

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.exit(tc, "setDestinationAvailability");
    }
  }

  protected void setDestinationType(DestinationType destinationType)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(
        tc,
        "setDestinationType",
        new Object[] { destinationType });
    }
    
    this.destinationType = destinationType;
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.exit(tc, "setDestinationType");
    }
  }

  protected DestinationListener getDestinationLister()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(
        tc,
        "getDestinationLister");
        
      SibTr.exit(tc, "getDestinationLister", destinationListener);
    }
    return destinationListener;
  }

  /**
   * @return the destinationAvailability
   */
  protected DestinationAvailability getDestinationAvailability()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(
        tc,
        "getDestinationAvailability");
        
      SibTr.exit(tc, "getDestinationAvailability", destinationAvailability);  
    }
    return destinationAvailability;
  }

  /**
   * @return the destinationType
   */
  protected DestinationType getDestinationType()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(
        tc,
        "getDestinationType");
     
      SibTr.exit(tc, "getDestinationType", destinationType);
    }
    return destinationType;
  }

  /**
   * @return the destinationNamePattern
   */
  protected DestinationNamePattern getDestinationNamePattern()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(
        tc,
        "getDestinationNamePattern");
     
      SibTr.exit(tc, "getDestinationNamePattern", destinationNamePattern);
    }
    return destinationNamePattern;
  }  
  
  /**
   * @return the connection
   */
  public SICoreConnection getConnection()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(
        tc,
        "getConnection");
        
      SibTr.exit(tc, "getConnection", connection);
    }
    return connection;
  }

}
