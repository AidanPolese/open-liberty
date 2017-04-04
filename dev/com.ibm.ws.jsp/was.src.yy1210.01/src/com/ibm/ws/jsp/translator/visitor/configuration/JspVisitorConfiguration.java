//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.visitor.configuration;

import java.util.HashMap;

public class JspVisitorConfiguration {
    protected HashMap jspVisitorDefinitionMap = new HashMap();
    protected HashMap jspVisitorCollectionMap = new HashMap();
    
    public JspVisitorConfiguration() {
    }
    
    void addJspVisitorDefinition(JspVisitorDefinition jspVisitorDefinition) {
        jspVisitorDefinitionMap.put(jspVisitorDefinition.getId(), jspVisitorDefinition);   
    }
    
    JspVisitorDefinition getJspVisitorDefinition(String id) {
        return ((JspVisitorDefinition)jspVisitorDefinitionMap.get(id));
    }
    
    void addJspVisitorCollection(JspVisitorCollection jspVisitorCollection) {
        jspVisitorCollectionMap.put(jspVisitorCollection.getId(), jspVisitorCollection);   
    }
    
    public JspVisitorCollection getJspVisitorCollection(String id) {
        return ((JspVisitorCollection)jspVisitorCollectionMap.get(id));
    }
}
