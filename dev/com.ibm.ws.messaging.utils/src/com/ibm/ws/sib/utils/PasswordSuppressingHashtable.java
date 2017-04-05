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
 * 604938          130809 djvines  Original
 * ============================================================================
 */
package com.ibm.ws.sib.utils;

import com.ibm.ws.ffdc.FFDCSelfIntrospectable;
import java.util.Hashtable;
import java.util.Map;

public class PasswordSuppressingHashtable<K,V> extends Hashtable<K,V> implements FFDCSelfIntrospectable
{
  public PasswordSuppressingHashtable()
  {
    super();
  }

  public PasswordSuppressingHashtable(int initialCapacity)
  {
    super(initialCapacity);
  }

  public PasswordSuppressingHashtable(int initialCapacity, float loadFactor)
  {
    super(initialCapacity,loadFactor);
  }

  public PasswordSuppressingHashtable(Map<? extends K, ? extends V> t)
  {
    super(t);
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    boolean first = true;
    for(Map.Entry<K,V> entry : entrySet())
    {
      if (!first)
        sb.append(", ");
      else
        first = false;

      String keyString = "null";
      if (entry.getKey() != null) keyString = entry.getKey().toString();


      sb.append(keyString);
      sb.append("=");
      sb.append(PasswordUtils.replaceValueIfKeyIsPassword(keyString,entry.getValue()));
    }
    sb.append("}");
    return sb.toString();
  }

  public String[] introspectSelf()
  {
    return new String[] { toString() };
  }
}
