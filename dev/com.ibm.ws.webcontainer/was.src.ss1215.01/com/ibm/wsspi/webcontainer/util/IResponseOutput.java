// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.wsspi.webcontainer.util;

import java.io.IOException;

public interface IResponseOutput 
{
	/* Has the response object obtained a writer? */
	public boolean writerObtained();

	/* Has the response object obtained an outputStream? */
	public boolean outputStreamObtained();
	
    /*************************************
	 ** Methods added for defect 112206 **
	 *************************************/
	public boolean isCommitted();
	
	public void reset();
	
	public void flushBuffer(boolean flushToWire) throws IOException;
}

