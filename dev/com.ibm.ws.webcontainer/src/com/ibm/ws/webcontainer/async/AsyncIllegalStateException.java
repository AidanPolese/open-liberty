package com.ibm.ws.webcontainer.async;

public class AsyncIllegalStateException extends IllegalStateException {

	public AsyncIllegalStateException(String s) {
		 super(s);
    }
	
	public AsyncIllegalStateException(Exception e) {
		super(e);
	}

}
