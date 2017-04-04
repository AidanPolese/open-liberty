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
package com.ibm.websphere.servlet31.request;

import com.ibm.websphere.servlet.request.extended.IRequestExtended;

/**
 *
 */
public interface IRequest31 extends IRequestExtended {

    
    /**
     * Method for getting the Content Length of the Request
     * @return long the length of data in the request. Added
     * for Servlet 3.1 support.
     **/
    public long getContentLengthLong();    
}
