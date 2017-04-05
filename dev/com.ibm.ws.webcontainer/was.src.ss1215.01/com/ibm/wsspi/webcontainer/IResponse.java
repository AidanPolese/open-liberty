// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//  CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
// PK22392      04/25/06    mmolden             FLUSH() IS NOT WORKING AS EXPECTED ON SERVLETOUTPUTSTREAM   //Code added as part of APAR PK22392 added flushBufferedContent() and getFlushMode()  by rksingh
package com.ibm.wsspi.webcontainer;


/**
 * 
 * Interface that the webcontainer expects the response objects to implement. The methods
 * on this interface will be called by the webcontainer in the process of writing back
 * the response.
 *
 * @ibm-private-in-use
 * 
 * @deprecated v7.0 Application developers requiring this functionality
 *  should implement this using com.ibm.websphere.servlet.response.IResponse.
 */

public interface IResponse extends com.ibm.websphere.servlet.response.IResponse {

    //methods moved to com.ibm.websphere.servlet.response.IResponse
}
