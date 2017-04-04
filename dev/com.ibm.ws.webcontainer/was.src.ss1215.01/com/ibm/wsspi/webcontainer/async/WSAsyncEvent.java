package com.ibm.wsspi.webcontainer.async;

import javax.servlet.AsyncEvent;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.wsspi.webcontainer.servlet.AsyncContext;

/**
 * 
 * Class extending javax AsyncEvent for IBM specific additional functionality
 * @ibm-private-in-use
 */
public class WSAsyncEvent extends AsyncEvent {

	private long elapsedTime;

	public long getElapsedTime() {
		return elapsedTime;
	}

	public WSAsyncEvent(AsyncContext asyncContext,
			ServletRequest servletRequest, ServletResponse servletResponse,
			long elapsedTime) {
		super(asyncContext,servletRequest,servletResponse);
		this.elapsedTime = elapsedTime;
	}

}
