// 1.2, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.util;

import java.io.StringWriter;
import java.io.PrintWriter;

public class ExceptionUtility {

    //---------------------------------------------------------------------
    // Get stacktrace as string
    //---------------------------------------------------------------------
	static public String getStackTrace(Throwable oThrowable) {
		if (oThrowable == null)
			return null;
		StringWriter oStringWriter = new StringWriter();
		PrintWriter  oPrintWriter  = new PrintWriter(oStringWriter);
		oThrowable.printStackTrace(oPrintWriter);

		return oStringWriter.toString();
	}
}


