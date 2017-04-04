// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.webcontainer.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ExtDocRootFile{
	public InputStream getIS() throws IOException ;
	public File  getMatch();
}