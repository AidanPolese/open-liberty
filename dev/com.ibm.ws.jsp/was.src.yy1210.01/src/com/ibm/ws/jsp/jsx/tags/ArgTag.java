// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.jsp.jsx.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

public class ArgTag extends TagSupport implements Tag {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257006540509754937L;
	String value;

    /**
     * @see TagSupport#doStartTag()
     */
    public int doStartTag() throws JspException {
        CallTag parent = (CallTag) getParent();
        parent.getArguments().add(value);
        return SKIP_BODY;
    }

    /**
     * @see TagSupport#release()
     */
    public void release() {
        value = null;
    }

    /**
     * Gets the value
     * @return Returns a String
     */
    public String getValue() {
        return value;
    }
    /**
     * Sets the value
     * @param value The value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}