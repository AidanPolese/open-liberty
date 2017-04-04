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
import java.util.HashMap;
import java.util.Map;

public class PasswordSuppressingHashMap<K,V> extends HashMap<K,V> implements FFDCSelfIntrospectable
{
  public PasswordSuppressingHashMap()
  {
    super();
  }

  public PasswordSuppressingHashMap(int initialCapacity)
  {
    super(initialCapacity);
  }

  public PasswordSuppressingHashMap(int initialCapacity, float loadFactor)
  {
    super(initialCapacity,loadFactor);
  }

  public PasswordSuppressingHashMap(Map<? extends K, ? extends V> m)
  {
    super(m);
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
