//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997-2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

// Change History
// Feature LIDB4293-2 - "In-memory translation/compilation of JSPs" 2006/11/11 Scott Johnson

package com.ibm.ws.jsp.inmemory.resource;

import java.io.Writer;

public interface InMemoryResources {
    public Writer getGeneratedSourceWriter();
    public char[] getGeneratedSourceChars();
    public byte[] getClassBytes(String className);
    public void setClassBytes(byte[] bytes, String className);
}
