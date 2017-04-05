// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
//306736.4      11/28/05    todkap              handle duplicate JSESSIONID in the request    WASCC.web.webcontainer
//
package com.ibm.wsspi.webcontainer;


/**
 * 
 *
 * Interface that the webcontainer recognizes as the types of requests that it can handle.
 * The webcontainer will call the methods on this interface during request processing.
 *
 * @ibm-private-in-use
 * 
 * @deprecated v7.0 Application developers requiring this functionality
 *  should implement this using com.ibm.websphere.servlet.request.IRequest.
 * 
 */
public interface IRequest extends com.ibm.websphere.servlet.request.IRequest
{
	//methods moved to com.ibm.websphere.servlet.request.IRequest
}
