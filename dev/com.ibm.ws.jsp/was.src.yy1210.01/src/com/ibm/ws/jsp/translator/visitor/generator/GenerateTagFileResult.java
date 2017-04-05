//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//  Defect 232818  2004/09/22 Strange behaviour when step over JSP taglib lines
// APAR PM12658 2010/04/20  add methods to keep track of doTag method line number for debug mode

package com.ibm.ws.jsp.translator.visitor.generator;

import java.util.HashMap;
import java.util.Map;

import com.ibm.ws.jsp.translator.visitor.JspVisitorResult;

public class GenerateTagFileResult extends JspVisitorResult {
    private Map customTagMethodJspIdMap = new HashMap(); //232818
    private int tagMethodLineNumber=0; //PM12658
    public GenerateTagFileResult(String jspVisitorId) {
        super(jspVisitorId);
    }
    /**
     * @return Returns the customTagMethodJspIdMap.
     */
    public Map getCustomTagMethodJspIdMap() { //232818
        return customTagMethodJspIdMap; //232818
    }
    
    //PM12658 start
    /**
     * @return Returns the tagMethodLineNumber.
     */
    public int getTagMethodLineNumber() {
        return tagMethodLineNumber;
    }
	
    /**
     * @param tagMethodLineNumber The tagMethodLineNumber to set.
     */
    public void setTagMethodLineNumber(int tagMethodLineNumber) {
        this.tagMethodLineNumber = tagMethodLineNumber;
    }
    //PM12658 end
}
