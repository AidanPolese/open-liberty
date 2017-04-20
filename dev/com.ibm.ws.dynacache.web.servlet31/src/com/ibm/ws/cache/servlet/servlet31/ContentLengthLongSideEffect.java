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
 
package com.ibm.ws.cache.servlet.servlet31;

import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.cache.servlet.ResponseSideEffect;


/**
 * @author kortega
 *
 */
public class ContentLengthLongSideEffect implements ResponseSideEffect {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -1359718310565387970L;
	private long length = 0L;
    
    public String toString() {
        StringBuffer sb = new StringBuffer("Content length long side effect: \n\t");
        sb.append("length: ").append(length).append("\n");
        return sb.toString();
     }
    
    /**
     * Constructor with parameter.
     * 
     * @param length The content length.
     */
    public
    ContentLengthLongSideEffect(long length)
    {
        this.length = length;
    }
    
	/* (non-Javadoc)
	 * @see com.ibm.ws.cache.servlet.ResponseSideEffect#performSideEffect(javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void performSideEffect(HttpServletResponse response) {
		 response.setContentLengthLong(length);

	}

}
