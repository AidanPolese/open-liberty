package com.ibm.ws.jsp.translator.compiler;

import com.ibm.ws.jsp.JspOptions;
import com.ibm.wsspi.jsp.compiler.JspCompiler;
import com.ibm.wsspi.jsp.compiler.JspCompilerFactory;

public class JDTCompilerFactory implements JspCompilerFactory{
	private ClassLoader loader = null;
    private JspOptions options;
	
	public JDTCompilerFactory(ClassLoader loader, JspOptions options) {
		this.loader = loader;
		this.options = options;
	}
	
	public JspCompiler createJspCompiler() {
		return new JDTCompiler(loader, options);
	}
}
