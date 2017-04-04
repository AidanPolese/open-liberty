// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.websphere.servlet.event;

import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;

import com.ibm.wsspi.webcontainer.util.ServletUtil;

/**
 * Event that reports information about a filter invocation.
 * 
 * @ibm-api
 */


public class FilterInvocationEvent extends FilterEvent {
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	private ServletRequest request = null;

    /**
     * FilterEvent contructor.
     * @param source the object that triggered this event.
     * @param filterConfig the filter's FilterConfig.
     * @param request the current request passed into the Filter.doFilter invocation.
     */
	public FilterInvocationEvent(Object source, FilterConfig filterConfig, ServletRequest request) {
		super(source, filterConfig);
		this.request = request;
	}

	/**
	 * @return Returns the current request passed into the Filter.doFilter method.
	 */
	public ServletRequest getServletRequest() {
		return ServletUtil.unwrapRequest(request);
	}
}
