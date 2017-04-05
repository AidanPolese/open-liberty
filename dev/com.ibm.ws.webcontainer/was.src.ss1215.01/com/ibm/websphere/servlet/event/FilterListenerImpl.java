// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.websphere.servlet.event;

import java.util.logging.Logger;
import java.util.logging.Level;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;


public class FilterListenerImpl
 implements FilterInvocationListener, FilterListener, FilterErrorListener
{

protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.websphere.servlet.event");
	private static final String CLASS_NAME="com.ibm.websphere.servlet.event.FilterListenerImpl";

	 public FilterListenerImpl()
	 {
	 }
	
	/* (non-Javadoc)
	 * @see com.ibm.websphere.servlet.event.FilterErrorListener#onFilterDestroyError(com.ibm.websphere.servlet.event.FilterErrorEvent)
	 */
	public void onFilterDestroyError(FilterErrorEvent evt) {
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
	    	logger.logp(Level.FINE, CLASS_NAME,"onFilterDestroyError", "onFilterStartDoFilter -->" + evt.getFilterName() + " error -->" + evt.getError());
	    }

	}
	/* (non-Javadoc)
	 * @see com.ibm.websphere.servlet.event.FilterErrorListener#onFilterDoFilterError(com.ibm.websphere.servlet.event.FilterErrorEvent)
	 */
	public void onFilterDoFilterError(FilterErrorEvent evt) {
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
	    	logger.logp(Level.FINE, CLASS_NAME,"onFilterDoFilterError", "onFilterDoFilterError -->" + evt.getFilterName()+ " error -->" + evt.getError());
	    }

	}
	/* (non-Javadoc)
	 * @see com.ibm.websphere.servlet.event.FilterErrorListener#onServletInitError(com.ibm.websphere.servlet.event.FilterErrorEvent)
	 */
	public void onFilterInitError(FilterErrorEvent evt) {
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
	    	logger.logp(Level.FINE, CLASS_NAME,"onFilterInitError", "onFilterInitError -->" + evt.getFilterName()+ " error -->" + evt.getError());
	    }

	}
	
	 public void onFilterStartDoFilter(FilterInvocationEvent filterinvocationevent)
	 {
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
	    	logger.logp(Level.FINE, CLASS_NAME,"onFilterStartDoFilter", "onFilterStartDoFilter -->" + filterinvocationevent.getFilterName() +" request -->" + filterinvocationevent.getServletRequest());
	    }
	 }
	
	 public void onFilterFinishDoFilter(FilterInvocationEvent filterinvocationevent)
	 {
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
	    	logger.logp(Level.FINE, CLASS_NAME,"onFilterFinishDoFilter", "onFilterFinishDoFilter -->" + filterinvocationevent.getFilterName() +" request -->" + filterinvocationevent.getServletRequest());
	    }
	 }
	
	 public void onFilterStartInit(FilterEvent filterinvocationevent)
	 {
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
	    	logger.logp(Level.FINE, CLASS_NAME,"onFilterStartInit", "onFilterStartInit -->" + filterinvocationevent.getFilterName() );
	    }
	 }
	
	 public void onFilterFinishInit(FilterEvent filterinvocationevent)
	 {
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
	    	logger.logp(Level.FINE, CLASS_NAME,"onFilterFinishInit", "onFilterFinishInit -->" + filterinvocationevent.getFilterName() );
	    }
	 }
	
	 public void onFilterStartDestroy(FilterEvent filterinvocationevent)
	 {
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
	    	logger.logp(Level.FINE, CLASS_NAME,"onFilterStartDestroy", "onFilterStartDestroy -->" + filterinvocationevent.getFilterName() );
	    }
	 }
	
	 public void onFilterFinishDestroy(FilterEvent filterinvocationevent)
	 {
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
	    	logger.logp(Level.FINE, CLASS_NAME,"onFilterFinishDestroy", "onFilterFinishDestroy -->" + filterinvocationevent.getFilterName() );
	    }
	 }
}
