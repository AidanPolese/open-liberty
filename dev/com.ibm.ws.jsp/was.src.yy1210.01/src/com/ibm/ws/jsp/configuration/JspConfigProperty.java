//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
// 395182.2  70FVT: make servlet 2.3 compatible with JSP 2.1 for migration 2007/02/07 Scott Johnson

package com.ibm.ws.jsp.configuration;

public class JspConfigProperty { 
    public static final int IS_XML_TYPE = 1;
    public static final int EL_IGNORED_TYPE = 2;
    public static final int SCRIPTING_INVALID_TYPE = 3;
    public static final int PAGE_ENCODING_TYPE = 4;
    public static final int PRELUDE_TYPE = 5;
    public static final int CODA_TYPE = 6;
    public static final int DEFERRED_SYNTAX_ALLOWED_AS_LITERAL_TYPE = 7; // jsp2.1ELwork
    public static final int TRIM_DIRECTIVE_WHITESPACES_TYPE = 8; // jsp2.1work
    public static final int EL_IGNORED_SET_TRUE_TYPE = 9; // jsp2.1work
    public static final int DEFAULT_CONTENT_TYPE = 10;//jsp2.1MR2work
    public static final int BUFFER = 11;//jsp2.1MR2work
    public static final int ERROR_ON_UNDECLARED_NAMESPACE = 12;//jsp2.1MR2work
    
    private Object propertyValue = null;
    private int propertyType = 0;
    
    public JspConfigProperty(int type, Object value) {
        this.propertyType = type;
        this.propertyValue = value;
    }
    
    public int getType() {
        return propertyType;
    }
    
    public Object getValue() {
        return propertyValue;
    }
}
