//IBM Confidential OCO Source Material
//   5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//   The source code for this program is not published or otherwise divested
//   of its trade secrets, irrespective of what has been deposited with the
//   U.S. Copyright Office.

package com.ibm.ws.jsp.taglib.annotation;

import java.util.EventListener;

import javax.servlet.jsp.tagext.JspTag;

public class DefaultAnnotationHandler extends AnnotationHandler {
     public void doPostConstructAction (JspTag tag) {
     }
     
     public void doPostConstructAction (EventListener listener) {
     }
     
     public void doPreDestroyAction (JspTag tag) {
     }
}
