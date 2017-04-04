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

import com.ibm.websphere.servlet.response.IResponse;

/**
 *
 */
public interface IResponse31 extends IResponse {

    
    /**
     * Sets the length of the content body in the response In HTTP servlets, this method sets the HTTP Content-Length header.
     * @param length
     */
    public void setContentLengthLong(long length);
    

}
