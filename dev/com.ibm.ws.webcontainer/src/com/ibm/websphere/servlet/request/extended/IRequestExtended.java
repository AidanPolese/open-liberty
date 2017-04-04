/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2015
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.websphere.servlet.request.extended;

import com.ibm.websphere.servlet.request.IRequest;
import com.ibm.ws.util.ThreadPool;

/**
 *  RTC 160610. Contains methods moved from com.ibm.websphere.servlet.request.IRequest 
 *  which should not be spi.
 */
public interface IRequestExtended extends IRequest {

    public ThreadPool getThreadPool();

}
