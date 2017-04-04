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

/**
 * @author Neil Young
 *
 * MatchSpace  NLS resources.
 * 
 */
public class NLS
{
  private static com.ibm.ejs.ras.TraceNLS traceNLS = com.ibm.ejs.ras.TraceNLS.getTraceNLS(MatchSpaceConstants.MSG_BUNDLE);
  private static final String defaultMessage = "Default Message: {0} {1} {2} {3} {4} {5} {6} {7} {8} {9}"; 
  
  /**
   * Format the objects into a printable string, if possible.
   * 
   * @param String key of the template.
   * @return String null or a printable string. 
   */
  public static String format(String key
                             )
  {
    if (traceNLS != null) {
      return traceNLS.getString(key);
    } //if (traceTemplates != null).
    else 
      return key;    
  } // format().

  /**
   * Format the objects into a printable string, if possible.
   * 
   * @param String key of the template.
   * @param Object to be formatted into the string.
   * @return String containg the original object or a printable string. 
   */
  public static String format(String key,
                              Object object
                             )
  {
    if (traceNLS != null) {
      return traceNLS.getFormattedMessage(key,new Object[]{object},defaultMessage);
       } //if (traceTemplates != null).
    else 
      return object.toString();    

  } // format().

  /**
   * Format the objects into a printable string, if possible.
   * 
   * @param String key of the template.
   * @param Object[] array of <code>Objects</code>.
   *        to be formatted into the string.
   * @return String containg the original objects or a printable string. 
   */
  public static String format(String key,
                              Object[] objects
                             )
  {
    if (traceNLS != null) {
      return traceNLS.getFormattedMessage(key,objects,defaultMessage);
       } //if (traceTemplates != null).
    else 
      return objectsToString(objects);    
  } // formatInfo().
  
  private static String objectsToString(Object objects)
  {
    java.io.StringWriter stringWriter = new java.io.StringWriter();
    String result = null;
    if (objects == null) {
      result = " ";
    } else if (objects instanceof Object[]) {
      for (int i = 0; i < ((Object[]) objects).length; i++) {
        Object object =  ((Object[]) objects)[i];
        if (object == null)
          stringWriter.write("null\n");
        else {
          stringWriter.write(((Object[]) objects)[i].toString());
          stringWriter.write("\n");
        }
     }
      result = stringWriter.toString();
    } else {
      result = objects.toString();
    }
    return result;
  } // objectsToString().
  
} // class NLS.
