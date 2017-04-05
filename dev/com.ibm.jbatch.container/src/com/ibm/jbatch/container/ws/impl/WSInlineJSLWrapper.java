package com.ibm.jbatch.container.ws.impl;

public class WSInlineJSLWrapper {
	
	private final String jsl;
	
	WSInlineJSLWrapper(String inlineJSL) {
		jsl = inlineJSL;
	}

	public String getJsl() {
		return jsl;
	}
	
}
