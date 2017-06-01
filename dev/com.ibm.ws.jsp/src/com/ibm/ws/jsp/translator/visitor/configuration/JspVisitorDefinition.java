//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.visitor.configuration;


public class JspVisitorDefinition {
    private String id = null;
    private Class visitorResultClass = null;
    private Class visitorClass = null;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Class getVisitorResultClass() {
        return visitorResultClass;
    }

    public void setVisitorResultClass(Class visitorResultClass) {
        this.visitorResultClass = visitorResultClass;
    }

    public Class getVisitorClass() {
        return (visitorClass);
    }

    public void setVisitorClass(Class visitorClass) {
        this.visitorClass = visitorClass;    
    }

}
