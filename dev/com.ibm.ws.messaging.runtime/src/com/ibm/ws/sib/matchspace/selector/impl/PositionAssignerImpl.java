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
 * 166318.8         300603 nyoung   First version.
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace.impl component
 * 166318.13        201103 auerbach Remove obsolete/unused list support
 * 166318.14        011203 auerbach Optimized LIKE processing and generalized TOPIC
 * SIB0136b.msp.1   080207 nyoung   Stage 2 implementation of XPath Selector support.
 * 504438           180308 nyoung   XPath support does not handle namespace prefixes
 * 509852           070408 nyoung   Trace isAnyTracingEnabled guards required  
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.selector.impl;
import com.ibm.ws.sib.matchspace.Identifier;
import com.ibm.ws.sib.matchspace.tools.PositionAssigner;
import com.ibm.ws.sib.matchspace.utils.MatchSpaceConstants;
import com.ibm.ws.sib.matchspace.utils.Trace;
import com.ibm.ws.sib.matchspace.utils.TraceComponent;
import com.ibm.ws.sib.matchspace.utils.TraceUtils;

import java.util.HashMap;
import java.util.Map;

/** The PositionAssigner assigns ordinal positions to Identifiers based purely on 
 * when they were first seen.
 *
 * For purposes of ordinal position assignment, two Identifiers are the same if their
 * names are the same and their basic type (STRING vs NUMERIC vs BOOLEAN vs UNKNOWN) is
 * the same.  A LIST identifier will never appear in a SimpleTest so it is not assigned an
 * ordinal position.
 * **/

public final class PositionAssignerImpl implements PositionAssigner
{
  // Standard trace boilerplate
  private static final Class cclass = PositionAssignerImpl.class;
  private static Trace tc = TraceUtils.getTrace(PositionAssignerImpl.class,
    MatchSpaceConstants.MSG_GROUP_LISTS); 
  // A map of all the levels
  private Map levels = new HashMap();
  
  private class LevelEntry
  {
    // Field for assigning ordinal position within a level
    private int nextPosition = 0;
    
    // Counts changes to exactMatcher using the same odd/even discipline as
    // matchTreeGeneration
    private Map positions;
    
    LevelEntry()
    {
      this.nextPosition = 0;
      this.positions = new HashMap();
    }
    
    public int getPosition(Object key)
    {
      Integer op = (Integer) positions.get(key);
      if (op == null)
      {
        op = new Integer(nextPosition++);
        positions.put(key, op);
      }
            
      return op.intValue();
    }
    

  }  
  
  // Implement the assignPosition method

  public void assign(Identifier id)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      tc.entry(
        cclass,
        "assign",
        "identifier: " + id);
    
    // Assign ordinal position
    String key = null;
    switch (id.getType()) 
    {
      case Identifier.STRING :
        // Changed from using the Identifier name to using the full name under defect 504438. The full name is only
        // relevant in the XPath Selector Domain and incorporates the full path for the attribute or element referenced.
        // This is necessary so that we are able to distinguish between common names at the same depth in an XML
        // document. For example you might encounter 2 XPath expressions:
        //
        // /company/department[@name='test'] and /company/location[@name='London']
        //
        // We must ensure that the system can distinguish these attributes.
        //
        key = "S:" + id.getFullName();
        break;
      case Identifier.BOOLEAN :
        key = "B:" + id.getFullName();
        break;
      case Identifier.CHILD :
        key = "C:" + id.getFullName();
        break;
      case Identifier.TOPIC:
        key = "T:" + id.getFullName();
        break;
      case Identifier.UNKNOWN :
      case Identifier.OBJECT :
        key = "U:" + id.getFullName();
        break;
      default :
        key = "N:" + id.getFullName();
    }
 
    // Get the map for the specified level. The level is only relevant in the XPath Selector Domain. It represents the
    // level of a location step in an XPath expression
    Integer level = new Integer(id.getStep());
    LevelEntry positions = (LevelEntry) levels.get(level);
    if (positions == null)
    {
      positions = new LevelEntry();
      levels.put(level, positions);
    }
    
    int levelPos = 0;
    if (key != null)
    {
      levelPos = positions.getPosition(key);
    }
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
      tc.debug(this,cclass, "assign", "level: " + level + ", levelPos: " + Integer.valueOf(levelPos) + ", for key: " + key);   
    
    // Now we're ready to set the OrdinalPosition
    OrdinalPosition ordPos = new OrdinalPosition(level.intValue(), levelPos);
    id.setOrdinalPosition(ordPos);
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      tc.exit(this,cclass, "assign");
  }

}
