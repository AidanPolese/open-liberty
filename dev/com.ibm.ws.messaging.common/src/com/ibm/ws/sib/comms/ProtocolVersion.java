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
 * 469880          071109 prestona Need to be able to get FAP level from comms
 * 482685          071112 mleming  Fix findbugs issues
 * ============================================================================
 */

package com.ibm.ws.sib.comms;

public class ProtocolVersion implements Comparable<ProtocolVersion>
{
   public static final ProtocolVersion UNKNOWN       = new ProtocolVersion("UNKNOWN",         0);
   public static final ProtocolVersion VERSION_6_0   = new ProtocolVersion("VERSION_6_0",   100);
   public static final ProtocolVersion VERSION_6_0_2 = new ProtocolVersion("VERSION_6_0_2", 200);
   public static final ProtocolVersion VERSION_6_1   = new ProtocolVersion("VERSION_6_1",   300);
   public static final ProtocolVersion VERSION_7     = new ProtocolVersion("VERSION_7",     400);
   
   private final String toStringValue;
   private final int version;
   
   private ProtocolVersion(String humanReadableForm, int version)
   {
      this.toStringValue = humanReadableForm;
      this.version = version;
   }
   
   public int ordinal()
   {
      return version;
   }
   
   public boolean equals(Object other)
   {
      if(other instanceof ProtocolVersion)
      {
         return ((ProtocolVersion)other).version == version;
      }
      
      return false;
   }
   
   public int hashCode()
   {
      return version;
   }

   public int compareTo(ProtocolVersion other)
   {
      return this.version - other.version;
   }
   
   public String toString()
   {
      return toStringValue;
   }
}
