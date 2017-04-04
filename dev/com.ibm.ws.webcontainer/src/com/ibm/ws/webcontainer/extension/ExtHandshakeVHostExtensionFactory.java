// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.extension;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wsspi.webcontainer.extension.ExtensionFactory;
import com.ibm.wsspi.webcontainer.extension.ExtensionProcessor;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

@SuppressWarnings("unchecked")
public class ExtHandshakeVHostExtensionFactory implements ExtensionFactory {
    private ArrayList patList = new ArrayList();
    
    public ExtHandshakeVHostExtensionFactory() {
        patList.add("_WS_EH*");
    }
    /* (non-Javadoc)
     * @see com.ibm.wsspi.webcontainer.extension.ExtensionFactory#createExtensionProcessor(com.ibm.wsspi.webcontainer.servlet.IServletContext)
     */
    public ExtensionProcessor createExtensionProcessor(IServletContext webapp) throws Exception {
        return new ExtHandshakeVHostExtensionProcessor();
    }

    /* (non-Javadoc)
     * @see com.ibm.wsspi.webcontainer.extension.ExtensionFactory#getPatternList()
     */
    public List getPatternList() {
        return patList;
    }

}
