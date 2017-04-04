// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

/**
 * Event listener interface used for notifications about Filter invocations.
 * Implementors of this interface must be very cautious about the time spent processing
 * these events because they occur on the filter's actual doFilter processing path.
 * 
 * @ibm-api
 */

package com.ibm.websphere.servlet.event;

import java.util.EventListener;


public interface FilterInvocationListener
extends EventListener
{

    /**
     * Triggered just prior to the execution of Filter.doFilter().
     *
     * @see  javax.servlet.Filter
     */	
	public abstract void onFilterStartDoFilter(FilterInvocationEvent filterinvocationevent);

    /**
     * Triggered just after the execution of Filter.doFilter().
     *
     * @see  javax.servlet.Filter
     */	
	public abstract void onFilterFinishDoFilter(FilterInvocationEvent filterinvocationevent);

}
