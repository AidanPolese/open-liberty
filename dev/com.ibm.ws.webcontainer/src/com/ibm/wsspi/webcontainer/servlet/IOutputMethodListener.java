// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.webcontainer.servlet;

import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;

/**
 * 
 * 
 * Interface to alert what type of output is being sent in a response.
 * @ibm-private-in-use
 */
public interface IOutputMethodListener {
	public void notifyWriterRetrieved (PrintWriter pw);
	public void notifyOutputStreamRetrieved(ServletOutputStream sos);
}
