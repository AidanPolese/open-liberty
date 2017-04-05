//IBM Confidential OCO Source Material
//5639-D57,5630-A36,5630-A37,5724-D18 (C) COPYRIGHT International Business Machines Corp. 2008
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//
//Code added as part of PK76117
//
// Change history
// 

package com.ibm.ws.webcontainer.exception;

import javax.servlet.UnavailableException;

/**
 * @author mmulholl
 *
 */
public class WebContainerUnavailableException extends UnavailableException {	
 
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
    static final long serialVersionUID = 7587224098080581230L;
    
    public static WebContainerUnavailableException create(javax.servlet.UnavailableException une)
    {
    	if (une.isPermanent())            		
    		return new WebContainerUnavailableException(une.getMessage(),une);
    	else return new WebContainerUnavailableException(une.getMessage(),une.getUnavailableSeconds(),une);  
	
    }	
    public WebContainerUnavailableException(String msg, int seconds, UnavailableException une)
    { 
        super(msg,seconds);
        initCause(une);
    }	
    	
    public WebContainerUnavailableException(String msg,UnavailableException une)
    { 
        super(msg);
    	initCause(une);
    }	
    	
  
}
