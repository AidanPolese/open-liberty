//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997-2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

// Change History
// Feature LIDB4293-2 - "In-memory translation/compilation of JSPs" 2006/11/11 Scott Johnson

package com.ibm.ws.jsp.inmemory.compiler;

import com.ibm.ws.jsp.JspOptions;
import com.ibm.wsspi.jsp.compiler.JspCompiler;
import com.ibm.wsspi.jsp.compiler.JspCompilerFactory;

public class InMemoryJspCompilerFactory implements JspCompilerFactory {
    private ClassLoader loader = null;
    private JspOptions jspOptions = null;

    public InMemoryJspCompilerFactory(ClassLoader loader, JspOptions jspOptions) {
        this.loader = loader;
        this.jspOptions = jspOptions;
    }
    
    public JspCompiler createJspCompiler() {
        return new InMemoryJDTCompiler(loader, jspOptions);
    }
}
