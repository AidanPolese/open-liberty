//IBM Confidential OCO Source Material
//	5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//Revisions:
//Defect 212433  2004/07/20  Stepping into _jspService lands at a unexpected location
//Defect 232818 2004/9/22 Strange behaviour when step over JSP taglib lines


package com.ibm.ws.jsp.translator.visitor.generator;

import java.util.HashMap;
import java.util.Map;

import com.ibm.ws.jsp.translator.visitor.JspVisitorResult;

public class GenerateJspResult extends JspVisitorResult {
    private Map customTagMethodJspIdMap = new HashMap(); //232818
    private int serviceMethodLineNumber=0;
    public GenerateJspResult(String jspVisitorId) {
        super(jspVisitorId);
    }
    
	/**
	 * @return Returns the serviceMethodLineNumber.
	 */
	public int getServiceMethodLineNumber() {
		return serviceMethodLineNumber;
	}
	/**
	 * @param serviceMethodLineNumber The serviceMethodLineNumber to set.
	 */
	public void setServiceMethodLineNumber(int serviceMethodLineNumber) {
		this.serviceMethodLineNumber = serviceMethodLineNumber;
	}
	/**
	 * @return Returns the customTagMethodJspIdMap.
	 */
	public Map getCustomTagMethodJspIdMap() { //232818
		return customTagMethodJspIdMap; //232818
	}
}
