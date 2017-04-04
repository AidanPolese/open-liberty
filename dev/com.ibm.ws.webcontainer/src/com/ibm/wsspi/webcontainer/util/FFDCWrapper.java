package com.ibm.wsspi.webcontainer.util;

public class FFDCWrapper {
	
	public static void processException(Throwable th, String method, String id, Object obj){
		com.ibm.ws.ffdc.FFDCFilter.processException(th,method,id,obj);
	}

	public static void processException(Throwable th, String method, String id) {
		com.ibm.ws.ffdc.FFDCFilter.processException(th,method,id);
	}
}
