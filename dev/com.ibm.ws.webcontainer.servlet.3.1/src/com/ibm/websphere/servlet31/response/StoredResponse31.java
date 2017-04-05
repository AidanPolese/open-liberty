/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2014
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.websphere.servlet31.response;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.servlet.IExtendedResponse;

/**
 *
 */
public class StoredResponse31 extends com.ibm.websphere.servlet.response.StoredResponse {

    protected static final Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.websphere.servlet.response");
    private static final String CLASS_NAME = "com.ibm.websphere.servlet.response.StoredResponse";    
    
    /**  */
    private static final long serialVersionUID = 6332168752731823366L;

    /**
     * 
     */
    public StoredResponse31() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param isInclude
     */
    public StoredResponse31(boolean isInclude) {
        super(isInclude);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param extResponse
     * @param isInclude
     */
    public StoredResponse31(IExtendedResponse extResponse, boolean isInclude) {
        super(extResponse, isInclude);
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    public void setContentLengthLong(long len) {
        if (!dummyResponse)
            super.setContentLengthLong(len);
        else if (isInclude)
        {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"setContentLengthLong", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "setContentLengthLong length --> " + len);  
            }
        }
        else {
            // ** TODO Servlet 3.1 setLongHeader???
            // setIntHeader("content-length", len);  
        }    
    }
        
}
