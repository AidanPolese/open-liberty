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
 * TBD              260303 astley   First version
 * 166318.3         090603 nyoung   Include trace 
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.impl
 * 171415           100703 gatfora  Removal of complile time warnings.
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace.impl component
 * LIDB3706-5.212   220205 gatfora  Fix trace
 * SIB0155.mspac.1  120606 nyoung   Repackage MatchSpace RAS.
 * SIB0155.msp.4    151106 nyoung   Enable OSGImin support.
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.impl;

// Import required classes.
import com.ibm.ws.sib.matchspace.EvalCache;
import com.ibm.ws.sib.matchspace.utils.MatchSpaceConstants;
import com.ibm.ws.sib.matchspace.utils.Trace;
import com.ibm.ws.sib.matchspace.utils.TraceUtils;

//------------------------------------------------------------------------------
// Class EvalCache
//------------------------------------------------------------------------------
/**
 * EvalCache
 *
 *
 */
//---------------------------------------------------------------------------

public class EvalCacheImpl implements EvalCache
{

  // Standard trace boilerplate
  
  private static final Class cclass = EvalCacheImpl.class;
  private static Trace tc = TraceUtils.getTrace(EvalCacheImpl.class,
      MatchSpaceConstants.MSG_GROUP_LISTS);

  int generation = 1;
  int[] cacheTag;
  Object[] cacheValue;

  //------------------------------------------------------------------------------
  // Method: EvalCache.prepareCache
  //------------------------------------------------------------------------------
  /**  Ensure the cache is big enough and able to handle another message match.
   * Also, increase the "generation" counter to invalidate the cache.
   *
   * Created: 99-01-27
   */
  //---------------------------------------------------------------------------
  public
  void prepareCache(int size){
   if (tc.isEntryEnabled())
      tc.entry(this,cclass, "prepareCache", "size: " + size);

    int oldSize = (cacheTag == null) ? 0 : cacheTag.length;

    if (size <= oldSize) {
      // If the cache is big enough, make sure we are not out of counters
      if (generation == Integer.MAX_VALUE) {
        generation = 1;
        for (int i = 0; i < size; i++)
          cacheTag[i] = 0;
      } else generation++;

    if (tc.isEntryEnabled())
	  tc.exit(this,cclass, "prepareCache");

      return;
    }

    // Allocate fresh

    cacheTag = new int[2*size];
    cacheValue = new Object[2*size];

    // Reset generation since all cache fields are null.
    generation = 1;

    if (tc.isEntryEnabled())
	  tc.exit(this,cclass, "prepareCache");
  } //sizeCache


  //------------------------------------------------------------------------------
  // Method: EvalCache.getExprValue
  //------------------------------------------------------------------------------
  /**
   *
   * Created: 99-01-27
   */
  //---------------------------------------------------------------------------
  public
  Object getExprValue(int id){

   if (tc.isEntryEnabled())
      tc.entry(this,cclass, "getExprValue", "id: " + new Integer(id));
    Object result = null;
    if (cacheTag[id] == generation)
      result = cacheValue[id];

    if (tc.isEntryEnabled())
	  tc.exit(this,cclass, "getExprValue","result: " + result);
    return result;
  } //getExprValue


  //------------------------------------------------------------------------------
  // Method: EvalCache.saveExprValue
  //------------------------------------------------------------------------------
  /**
   *
   * Created: 99-01-27
   */
  //---------------------------------------------------------------------------
  public
  void saveExprValue(int id, Object value){

   if (tc.isEntryEnabled())
      tc.entry(this,cclass, "saveExprValue", "id: " + new Integer(id) +",value: "+value);
    cacheTag[id] = generation;
    cacheValue[id] = value;
   if (tc.isEntryEnabled())
	 tc.exit(this,cclass, "saveExprValue");
  } //saveExprValue

} // EvalCacheImpl
