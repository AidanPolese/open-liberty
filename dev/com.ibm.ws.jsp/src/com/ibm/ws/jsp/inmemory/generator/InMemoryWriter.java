//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997-2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

// Change History
// Feature LIDB4293-2 - "In-memory translation/compilation of JSPs" 2006/11/11 Scott Johnson

package com.ibm.ws.jsp.inmemory.generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import com.ibm.ws.jsp.translator.visitor.generator.JavaFileWriter;

public class InMemoryWriter extends JavaFileWriter{
    
    public InMemoryWriter(Writer writer, Map jspElementMap, Map cdataJspIdMap, Map customTagMethodJspIdMap) throws IOException  {
    	super(new PrintWriter(new BufferedWriter(writer)),jspElementMap, cdataJspIdMap, customTagMethodJspIdMap );
    }
}
