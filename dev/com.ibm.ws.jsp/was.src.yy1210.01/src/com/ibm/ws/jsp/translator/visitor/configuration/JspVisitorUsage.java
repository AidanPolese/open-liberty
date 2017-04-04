//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.visitor.configuration;

public class JspVisitorUsage {
    private int order = 0;
    private int visits = 0;
    private JspVisitorDefinition visitorDefinition = null;
    
    public JspVisitorUsage(int order, int visits, JspVisitorDefinition visitorDefinition) {
        this.order = order;
        this.visits = visits;
        this.visitorDefinition = visitorDefinition;
    }

    public int getOrder() {
        return order;
    }
    
    public int getVisits() {
        return visits;
    }

    public JspVisitorDefinition getJspVisitorDefinition() {
        return (visitorDefinition);
    }
}
