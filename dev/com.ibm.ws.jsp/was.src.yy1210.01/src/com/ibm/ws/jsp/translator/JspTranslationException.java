//IBM Confidential OCO Source Material
//	5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator;

import org.w3c.dom.Element;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.translator.utils.JspId;

public class JspTranslationException extends JspCoreException {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257566191894410289L;
	protected JspId jspId = null;
    
    public JspTranslationException() {
        super();
    }

    public JspTranslationException(String message) {
        super(message);
    }

    public JspTranslationException(Element jspElement, String message) {
        super(message);
        String jspIdString = jspElement.getAttributeNS(Constants.JSP_NAMESPACE, "id");
        if (jspIdString.equals("") == false) 
            jspId = new JspId(jspIdString);
    }

    public JspTranslationException(String message, Object[] args) {
        super(message, args);
    }

    public JspTranslationException(Element jspElement, String message, Object[] args) {
        super(message, args);
        String jspIdString = jspElement.getAttributeNS(Constants.JSP_NAMESPACE, "id");
        if (jspIdString.equals("") == false) 
            jspId = new JspId(jspIdString);
    }

    public JspTranslationException(String message, Throwable exc) {
        super(message, exc);
    }

    public JspTranslationException(Element jspElement, String message, Throwable exc) {
        super(message, exc);
        String jspIdString = jspElement.getAttributeNS(Constants.JSP_NAMESPACE, "id");
        if (jspIdString.equals("") == false) 
            jspId = new JspId(jspIdString);
    }

    public JspTranslationException(Element jspElement, String message, Object[] args, Throwable exc) {
        super(message, args, exc);
        String jspIdString = jspElement.getAttributeNS(Constants.JSP_NAMESPACE, "id");
        if (jspIdString.equals("") == false) 
            jspId = new JspId(jspIdString);
    }
    
    public JspTranslationException(Throwable exc) {
        super(exc==null ? null : exc.toString(), exc);
    }
    
    public String getLocalizedMessage() {
        String msg = super.getLocalizedMessage();
        if (jspId != null) {
        	// defect 203252
            msg = jspId.getFilePath() + "(" + jspId.getStartSourceLineNum() + "," + jspId.getStartSourceColNum() + ") --> " + msg;
        }
        return (msg);
    }
	// Defect 202493
	public int getStartSourceLineNum() {
		if (jspId != null) {
			return jspId.getStartSourceLineNum();
		}
		return -1;
	}
	
	// defect 203252
	public String getFilePath(){
		if (jspId != null) {
			return jspId.getFilePath();
		}
		return null;
	}
}
