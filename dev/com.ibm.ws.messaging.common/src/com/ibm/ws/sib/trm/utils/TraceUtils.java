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
 * 609077          091009 mleming  Prevent issues with trace and java 2 security
 * ============================================================================
 */
package com.ibm.ws.sib.trm.utils;

import javax.security.auth.Subject;

/**
 * Utility class for tracing purposes.
 */
public final class TraceUtils 
{
   /**
    * @param subject
    * @return a stringified Subject. Needed as Subject.toString is a privileged action.
    */
   public static final String subjectToString(final Subject subject)
   {
      String subj = "<null>";
      if (subject != null)
      {
        subj = "Subject hashcode=0x" + Integer.toHexString(subject.hashCode());
      }
      return subj;
   }
}
