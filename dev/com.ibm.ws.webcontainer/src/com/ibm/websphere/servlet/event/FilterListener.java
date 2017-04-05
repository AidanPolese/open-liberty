// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.websphere.servlet.event;

import java.util.EventListener;


/**
 * Event listener interface used for notifications about fiters.
 * Most of these event have to do with the state management of a
 * filter's lifecycle.
 * 
 * @ibm-api
 */

public interface FilterListener
extends EventListener
{

    /**
     * Triggered just prior to the execution of Filter.init().
     *
     * @see  javax.servlet.Filter
     */	
	public abstract void onFilterStartInit(FilterEvent filterinvocationevent);

    /**
     * Triggered just after the execution of Filter.init().
     *
     * @see  javax.servlet.Filter
     */	
	public abstract void onFilterFinishInit(FilterEvent filterinvocationevent);

    /**
     * Triggered just prior to the execution of Filter.destroy().
     *
     * @see  javax.servlet.Filter
     */	
	public abstract void onFilterStartDestroy(FilterEvent filterinvocationevent);

    /**
     * Triggered just after the execution of Filter.destroy().
     *
     * @see  javax.servlet.Filter
     */	
	public abstract void onFilterFinishDestroy(FilterEvent filterinvocationevent);
}
