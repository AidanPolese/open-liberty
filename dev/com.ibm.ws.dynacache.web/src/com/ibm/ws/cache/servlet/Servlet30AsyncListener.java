// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2011
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.ws.cache.servlet;

import java.io.IOException;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * Servlet 3.0 AsyncListener registered automatically for all async requests
 */
public class Servlet30AsyncListener implements AsyncListener {

	private static TraceComponent tc = Tr.register(Servlet30AsyncListener.class,
			"WebSphere Dynamic Cache", "com.ibm.ws.cache.resources.dynacache");
	
	@Override
	public void onStartAsync(AsyncEvent event) throws IOException {
		
		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
			Tr.entry(tc, hashCode() + " onStartAsync", new Object[]{event});
		
		CacheProxyRequest request = getCacheProxyRequest(event);
		CacheProxyResponse response = getCacheProxyResponse(event);		
		if (tc.isDebugEnabled()){
			Tr.debug(tc, hashCode() + " request=" + request + " response= " + response);
			Tr.debug(tc, hashCode() + " FC=" + request.getFragmentComposer());
		}
			
		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
			Tr.exit(tc, hashCode() +" onStartAsync");
	}
	
	@Override
	public void onComplete(AsyncEvent event) throws IOException {
		
		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
			Tr.entry(tc, hashCode() +" onComplete", new Object[]{event});
		
		postProcessResponse(event, false);
		
		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
			Tr.exit(tc, hashCode() +" onComplete");
		
	}

	@Override
	public void onError(AsyncEvent event) throws IOException {
		
		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
			Tr.entry(tc, hashCode() +" onError", new Object[]{event});
		
		//do not cache the response		
		postProcessResponse(event, true);
		
		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
			Tr.exit(tc, hashCode() +" onError");
	}


	@Override
	public void onTimeout(AsyncEvent event) throws IOException {
		
		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
			Tr.entry(tc, hashCode() +" onTimeout", new Object[]{event});
		
		postProcessResponse(event, true);
		
		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
			Tr.exit(tc, hashCode() +" onTimeout");
		
	}
	
	public void postProcessResponse(AsyncEvent event, boolean errorOrTimeout) throws IOException{
		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
			Tr.entry(tc, hashCode()+" postProcessResponse", errorOrTimeout);
		
		CacheProxyRequest request = getCacheProxyRequest(event);
		CacheProxyResponse response = getCacheProxyResponse(event);
		FragmentComposer fc = request.getFragmentComposer();
		if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
			Tr.debug(tc, hashCode() + " request=" + request + "  fc=" + fc);
		
		if (errorOrTimeout){
			fc.setCacheType(FragmentComposer.NOT_CACHED);
			if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
				Tr.debug(tc, hashCode() + " fc should NOT be cached setting cacheType to NOT_CACHED");
		}
		
		if (null != fc && fc.isAsyncDispatch()){  //fc can be null when postProcess is called twice during a timeout or an error
			if (!errorOrTimeout && fc.shouldCacheOutput()){
				CacheHook.putInCache(fc);
			}
			CacheHook.postProcess(request, response, fc, request.getCaching(), errorOrTimeout);
			if (!errorOrTimeout  )
			{
				request.reset();
				response.finished();
			}
		}
		
		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
			Tr.exit(tc, hashCode() +" postProcessResponse");
	}

	private CacheProxyRequest getCacheProxyRequest(AsyncEvent event) {
		return (CacheProxyRequest) event.getSuppliedRequest();
	}
	
	private CacheProxyResponse getCacheProxyResponse(AsyncEvent event) {
		return (CacheProxyResponse) event.getSuppliedResponse();
	}
}
