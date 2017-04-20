/*
 * COMPONENT_NAME: sib.unittest.ffdc
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
 * d249011         03-Jun-2005 djvines Initial Test Drop.
 * d336163         09-Jan-2006 djvines Add an interesting toString method
 * d412802         02-Jan-2007 djvines Add diagnostic module and incident stream support
 */
package com.ibm.ws.sib.ffdc;


/* ************************************************************************** */
/**
 * A entry to the FFDC Entry - this record the processing of an exception
 * by the FFDCEngine
 *
 */
/* ************************************************************************** */
public class FFDCEntry
{
  private Throwable _throwable;
  private String    _sourceId;
  private String    _probeId;
  private Object    _objectThis;
  private Object[]  _objectArray;
  private IncidentStreamImpl _incidentStream;

  /* -------------------------------------------------------------------------- */
  /* FFDCEntry constructor
  /* -------------------------------------------------------------------------- */
  /**
   * Construct a new FFDCEntry.
   *
   * @param t  The Throwable for the entry
   * @param s  The source id for the entry
   * @param p  The probe id for the entry
   * @param o  The object for the entry
   * @param oa The object array for the entry
   */
  public FFDCEntry(Throwable t, String s, String p, Object o, Object[] oa)
  {
    _throwable = t;
    _sourceId  = s;
    _probeId   = p;
    _objectThis = o;
    _objectArray = oa;
    _incidentStream = new IncidentStreamImpl();
    _incidentStream.processIncident(_throwable != null ? _throwable.getClass().getName() : "null", _sourceId, _probeId, _throwable, _objectThis, _objectArray);
    
  }

  /* -------------------------------------------------------------------------- */
  /* getObjectArray method
  /* -------------------------------------------------------------------------- */
  /**
   * @return Returns the objectArray.
   */
  public Object[] getObjectArray()
  {
    return _objectArray;
  }
  /* -------------------------------------------------------------------------- */
  /* getObjectThis method
  /* -------------------------------------------------------------------------- */
  /**
   * @return Returns the objectThis.
   */
  public Object getObjectThis()
  {
    return _objectThis;
  }
  /* -------------------------------------------------------------------------- */
  /* getProbeId method
  /* -------------------------------------------------------------------------- */
  /**
   * @return Returns the probeId.
   */
  public String getProbeId()
  {
    return _probeId;
  }
  /* -------------------------------------------------------------------------- */
  /* getSourceId method
  /* -------------------------------------------------------------------------- */
  /**
   * @return Returns the sourceId.
   */
  public String getSourceId()
  {
    return _sourceId;
  }
  /* -------------------------------------------------------------------------- */
  /* getThrowable method
  /* -------------------------------------------------------------------------- */
  /**
   * @return Returns the throwable.
   */
  public Throwable getThrowable()
  {
    return _throwable;
  }
  /* -------------------------------------------------------------------------- */
  /* toString method
  /* -------------------------------------------------------------------------- */
  /**
   * @see java.lang.Object#toString()
   * @return The FFDC in a long string that looks like diagnostic output (with line feeds)
   */
  public String toString()
  {
    return _incidentStream.toString();
  }
}
