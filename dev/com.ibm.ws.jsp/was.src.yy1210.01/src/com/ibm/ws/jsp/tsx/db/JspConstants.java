// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.jsp.tsx.db;

/* -----------------------------------------------------------------
** Copyright 1997-98 IBM Corporation.  All rights reserved.
**
** -----------------------------------------------------------------
*/
public interface JspConstants
{
//  Error Constants
final static public String IntError                     = "InternalError";
final static public String NotYetImpl                   = "NotYetImpl";
final static public String SQLException                 = "SQLException";
final static public String NullDbDriver                 = "NullDbDriver";
final static public String NullQueryString              = "NullQueryString";
final static public String NullUrl                              = "NullUrl";
final static public String InvalidRowIndex              = "InvalidRowIndex";
final static public String InvalidDbDriver              = "InvalidDbDriver";
final static public String CurrRowNotInit               = "CurrRowNotInit";
final static public String InvalidCurrRowRef            = "InvalidCurrRowRef";
final static public String NamingException              = "NamingException";
final static public String DatasourceException          = "DatasourceException";
final static public String InvalidAttrName="InvalidAttrName";
//  class names
final static String   NlsClass  = "com.ibm.servlet.jsp.db.JspNLS";
final static String   SETracer  = "com.ibm.servlet.debug.SETracer";
                                          
}
