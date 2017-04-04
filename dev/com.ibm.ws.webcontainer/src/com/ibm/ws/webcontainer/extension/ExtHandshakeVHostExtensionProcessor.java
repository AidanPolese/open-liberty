// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.extension;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.wsspi.webcontainer.extension.ExtensionProcessor;
import com.ibm.wsspi.webcontainer.metadata.WebComponentMetaData;
import com.ibm.wsspi.webcontainer.servlet.IServletWrapper;

public class ExtHandshakeVHostExtensionProcessor implements ExtensionProcessor {

    public ExtHandshakeVHostExtensionProcessor() {
        
    }
    
    /* (non-Javadoc)
     * @see com.ibm.wsspi.webcontainer.extension.ExtensionProcessor#getPatternList()
     */
    @SuppressWarnings("unchecked")
    public List getPatternList() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.wsspi.webcontainer.RequestProcessor#handleRequest(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    public void handleRequest(ServletRequest req, ServletResponse res) throws Exception {
 
    }

	public String getName() {
		return "ExtHandshakeVHostExtensionProcessor";
	}

	public boolean isInternal() {
		return false;
	}

	public IServletWrapper getServletWrapper(ServletRequest req,
			ServletResponse resp) {
		// TODO Auto-generated method stub
		return null;
	}

	public WebComponentMetaData getMetaData() {
		// TODO Auto-generated method stub
		return null;
    }

}
