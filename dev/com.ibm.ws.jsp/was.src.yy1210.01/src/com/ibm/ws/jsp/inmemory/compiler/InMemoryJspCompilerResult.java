//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997-2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

// Change History
// Feature LIDB4293-2 - "In-memory translation/compilation of JSPs" 2006/11/11 Scott Johnson
// PK72039      Add ability to continue to compile the rest of the JSPs during a batch compile failure  2008/11/15  Jay Sartoris

package com.ibm.ws.jsp.inmemory.compiler;

import java.util.List;

import com.ibm.wsspi.jsp.compiler.JspCompilerResult;

public class InMemoryJspCompilerResult implements JspCompilerResult {
    private int rc = 0;
    private String compilerMessage = null;
    private List resourcesList = null;
    private List compilerFailureFileNames=null; //PK72039
    
    public InMemoryJspCompilerResult(int rc, String compilerMessage, List resourcesList) {
        this.rc = rc;
        this.compilerMessage = compilerMessage;
        this.resourcesList = resourcesList;
    }

    public String getCompilerMessage() {
        return compilerMessage;
    }

    public int getCompilerReturnValue() {
        return rc;
    }

    public List getResourcesList() {
        return resourcesList;
    }
    
    //PK72039 start
    public List getCompilerFailureFileNames() {
        return compilerFailureFileNames;
    }
    //PK72039 end

}
