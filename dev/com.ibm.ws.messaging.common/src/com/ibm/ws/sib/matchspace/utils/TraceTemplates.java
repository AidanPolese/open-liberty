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
 *  Reason           Date    Origin     Description
 * --------------- -------- ---------- ----------------------------------------
 * SIB0155.mspac.1 120606   nyoung   Repackage MatchSpace RAS.
 * ============================================================================
 */

package com.ibm.ws.sib.matchspace.utils;

// default (English language, United States)
public class TraceTemplates 
       extends java.util.ResourceBundle {
  final String templateValues[][]  = {
   {"com.ibm.ws.sib.matchspace.Matching.<init>.entry","logFileName={0}\nmatchspace={1}\nlogFileType={2}"},
                                     };
  java.util.HashMap templates = new java.util.HashMap();
  
  {
    for (int i=0; i<templateValues.length; i++){
      templates.put(templateValues[i][0],templateValues[i][1]);
    }
  }
  
  public Object handleGetObject(String key) {
    return templates.get(key);
  }

    /* (non-Javadoc)
     * @see java.util.ResourceBundle#getKeys()
     */
    public java.util.Enumeration getKeys()
    {
      // TODO Auto-generated method stub
      // return templates.keySet().iterator();
      return null;
    }
}
