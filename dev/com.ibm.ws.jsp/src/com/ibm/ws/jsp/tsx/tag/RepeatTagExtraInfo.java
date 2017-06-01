//IBM Confidential OCO Source Material
//5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

package com.ibm.ws.jsp.tsx.tag;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
* @author todd
*
* To change this generated comment edit the template variable "typecomment":
* Window>Preferences>Java>Templates.
* To enable and disable the creation of type comments go to
* Window>Preferences>Java>Code Generation.
*/
public class RepeatTagExtraInfo extends TagExtraInfo {

/**
 * Constructor for RepeatTagExtraInfo.
 */
public RepeatTagExtraInfo() {
    super();
}

public VariableInfo[] getVariableInfo(TagData data) {
    if (data.getAttributeString("index") != null) {
        return (new VariableInfo[] { new VariableInfo(data.getAttributeString("index"),"java.lang.Integer" , true, VariableInfo.NESTED)});
    }
    else {
        return null;
    }
}

}
