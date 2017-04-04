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
 * 196675.1.7.1     030604 tevans   MBean Registration enhancements
 * LIDB3706-5.248   180105 gatfora  Include a serialVersionUid for all serializable objects
 * 343065           070206 gatfora  Include exception reason and arguements in the exception
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.exceptions;

import java.util.Locale;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.sib.processor.SIMPConstants;


public class SIMPRuntimeOperationFailedException extends SIMPException {
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = -5971530104648357624L;
  
  private String messageId;
  
  private Object arguments[];

  public SIMPRuntimeOperationFailedException(String msg) {
    super(msg);
  }
  
  public SIMPRuntimeOperationFailedException(Throwable t) {
    super(t);
  }
  
  public SIMPRuntimeOperationFailedException(String msg, Throwable t) {
    super(msg, t);
  }
 
  public SIMPRuntimeOperationFailedException(String msg, Throwable t, String messageId, Object arguments[]) {
    super(msg, t);
    
    this.messageId = messageId;
    this.arguments = arguments;
  }

  public SIMPRuntimeOperationFailedException(String msg, String messageId, Object arguments[]) {
    super(msg);
    this.messageId = messageId;
    this.arguments = arguments;
  }
  
   public String getReasonText(Locale locale) 
   {
    
    String nlsMsgText = TraceNLS.getFormattedMessage(SIMPConstants.RESOURCE_BUNDLE, 
                                                     messageId, 
                                                     locale, arguments, null);    
    return nlsMsgText; 
   }

}
