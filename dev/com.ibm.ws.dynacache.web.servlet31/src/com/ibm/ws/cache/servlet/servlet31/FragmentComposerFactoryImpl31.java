/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2014
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.cache.servlet.servlet31;

import org.osgi.service.component.annotations.Component;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.cache.servlet.CacheProxyRequest;
import com.ibm.ws.cache.servlet.CacheProxyResponse;
import com.ibm.ws.cache.servlet.FragmentComposer;
import com.ibm.ws.cache.servlet.FragmentComposerFactory;

@Component(service=FragmentComposerFactory.class, property = { "service.vendor=IBM", "service.ranking:Integer=10" })
public class FragmentComposerFactoryImpl31 implements FragmentComposerFactory {
	private static TraceComponent tc = Tr.register(FragmentComposerFactoryImpl31.class,
			"WebSphere Dynamic Cache", "com.ibm.ws.cache.resources.dynacache");

	public FragmentComposer createFragmentComposer() {
		if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
			Tr.debug(tc, " Create FragmentComposer factory 31");

		return new FragmentComposerServlet31(); 
	}
	
	public FragmentComposer createFragmentComposer(CacheProxyResponse response, CacheProxyRequest request) {
		if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
			Tr.debug(tc, " Create FragmentComposer factory 31");

		return new FragmentComposerServlet31(request,response); 
	}
}

