// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//    LIDB-3598       		      todkap              Modified FilterInstanceWrapper to enable Listener support.
//	  292460		      todkap              resolve issues concerning LIDB-3598			
//	  299280.1    	08/22/05      todkap		  cannot add FilterInvocationListener in some case    WAS.webcontainer 
//	  301121    	08/26/05      todkap		  WebApp fails to handle wsspi implementation of IFilterConfig    WASCC.web.webcontainer
//

package com.ibm.ws.webcontainer.filter;

import java.text.MessageFormat;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import java.util.logging.Logger;
import java.util.logging.Level;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.util.ServletUtil;
import com.ibm.websphere.servlet.error.ServletErrorReport;
import com.ibm.websphere.servlet.event.FilterErrorEvent;
import com.ibm.websphere.servlet.event.FilterEvent;
import com.ibm.websphere.servlet.event.FilterInvocationEvent;
import com.ibm.ws.webcontainer.webapp.WebAppEventSource;
import com.ibm.wsspi.webcontainer.filter.IFilterConfig;

/**
 * Represents an instance of a filter.
 *
 */

public class FilterInstanceWrapper		// LIDB-3598: modified class to be public
{
    // filter state variables
    public static final int FILTER_STATE_UNINITIALIZED = 0;
    public static final int FILTER_STATE_INITIALIZING = 1;
    public static final int FILTER_STATE_AVAILABLE = 2;
    public static final int FILTER_STATE_DESTROYING = 3;
    public static final int FILTER_STATE_DESTROYED = 4;
    public static final int FILTER_STATE_UNAVAILABLE = 5;

    private String _filterName;
    private javax.servlet.Filter _filterInstance;
    private int _filterState = FILTER_STATE_UNINITIALIZED;
    
    private volatile int nServicing = 0;

protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.filter");
	private static final String CLASS_NAME="com.ibm.ws.webcontainer.filter.FilterInstanceWrapper";
    
    private FilterEvent _filterEvent;
    private IFilterConfig _filterConfig;
    private WebAppEventSource _eventSource;
    
	public FilterInstanceWrapper(String filterName, Filter filterInstance, WebAppEventSource eventSource) {
        _filterName = filterName;
        _filterInstance = filterInstance;
		_eventSource = eventSource;
	}


    /** public interface **/

    /**
     * Gets the filter name
     *
     * @return a String containing the filter name
     */
    public String getFilterName()
    {
        return _filterName;
    }

    /**
     * Gets the filter instance.
     *
     * @return the filter instance
     */
    public javax.servlet.Filter getFilterInstance()
    {
        return _filterInstance;
    }

    /**
     * Gets the current filter state
     *
     * @return an int representing the current filter state
     */
    public int getFilterState()
    {
        return _filterState;
    }

    /**
     * Sets the current filter state.  The state must be > FILTER_STATE_UNINITIALIZED
     * and <= FILTER_STATE_UNAVAILABLE or the state is set to FILTER_STATE_UNAVAILABLE.
     *
     * @param the state to be set
     */
    /* public void setFilterState(int fState)
    {
        if (fState < FILTER_STATE_UNINITIALIZED || fState > FILTER_STATE_UNAVAILABLE)
            filterState = FILTER_STATE_UNAVAILABLE;
        else
            filterState = fState;
    } */

    /**
     * Initializes the filter wrapper and the underlying filter instance
     *
     * @param fConfig - the filter config object for this filter instance
     */
    public void init(IFilterConfig filterConfig) throws ServletException
    {
        try
        {
            // init the filter instance
            _filterState = FILTER_STATE_INITIALIZING;

    		// LIDB-3598: begin
            this._filterConfig = filterConfig;
    		if(_eventSource != null && _eventSource.hasFilterListeners()){
	    	        
	           	_eventSource.onFilterStartInit(getFilterEvent());
	    		// LIDB-3598: end
	        	_filterInstance.init(filterConfig);
	        	// LIDB-3598: begin
	        	_eventSource.onFilterFinishInit(getFilterEvent());
	    		// LIDB-3598: end
    		}
    		else{
    			_filterInstance.init(filterConfig);
    		}

            _filterState = FILTER_STATE_AVAILABLE;
        }
        catch (Throwable th)
        {
    		if(_eventSource != null && _eventSource.hasFilterErrorListeners()){
	        	FilterErrorEvent errorEvent = getFilterErrorEvent(th);
	        	_eventSource.onFilterInitError(errorEvent);
    		}
        	
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, "com.ibm.ws.webcontainer.filter.FilterInstanceWrapper.init", "111", this);
            _filterState = FILTER_STATE_UNAVAILABLE;
            throw new ServletException(MessageFormat.format("Filter [{0}]: could not be initialized", new Object[] {_filterName}), th);
        }

    }

    /**
     * Inovkes the wrapped filter's doFilter method
     *
     * @param request the servlet request object
     * @param response the servlet response object
     * @param chain the filter chain object
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException
    {
        try
        {
            // invoke the wrapped filter
            if (_filterState == FILTER_STATE_AVAILABLE)
            {
            	synchronized (this)
            	{
            		nServicing++;
            	}
            	try
            	{
            	    if (request.isAsyncSupported())
            	        ServletUtil.unwrapRequest(request).setAsyncSupported(this._filterConfig.isAsyncSupported());
            		// LIDB-3598: begin
            		//_filterInstance.doFilter(request, response, chain);
            		if(_eventSource != null && _eventSource.hasFilterInvocationListeners()){
	                    FilterInvocationEvent event = getFilterInvocationEvent(request);
	                	_eventSource.onFilterStartDoFilter(event);
	                	_filterInstance.doFilter(request, response, chain);
	                	_eventSource.onFilterFinishDoFilter(event);
            		}
            		else{
            			_filterInstance.doFilter(request, response, chain);
            		}
                	// LIDB-3598: end
					
            	}
            	catch (ServletException se)
            	{
            		throw se;
            	}
            	catch (Throwable th)
            	{
            		throw th;
            	}
            	finally
            	{
            		synchronized (this)
            		{
            			nServicing--;
            		}
            	}
            }
            else
            {
                throw new ServletException(MessageFormat.format("Filter [{0}]: filter is unavailable.", new Object[] {_filterName}));
            }
        }
        catch (ServletException se)
        {
    		if(_eventSource != null && _eventSource.hasFilterErrorListeners()){
	        	FilterErrorEvent errorEvent = getFilterErrorEvent(se);
	        	_eventSource.onFilterDoFilterError(errorEvent);
    		}

            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(se, "com.ibm.ws.webcontainer.filter.FilterInstanceWrapper.doFilter", "144", this);
            throw se;
        }
        catch (RuntimeException re){
            throw re;
        }
        catch (Throwable th)
        {
        	logger.logp(Level.SEVERE, CLASS_NAME, "service", "uncaught.filter.exception", new Object[] {_filterName, th});
    		if(_eventSource != null && _eventSource.hasFilterErrorListeners()){
	        	FilterErrorEvent errorEvent = getFilterErrorEvent(th);
	        	_eventSource.onFilterDoFilterError(errorEvent);
    		}
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, "com.ibm.ws.webcontainer.filter.FilterInstanceWrapper.doFilter", "149", this);
            throw new ServletException(th);
        }

    }

    /**
     * Destroys the filter wrapper and the underlying filter instance
     *
     */
    public void destroy() throws ServletException
    {
        try
        {
            // destroy the filter instance
            _filterState = FILTER_STATE_DESTROYING;
            
			for (int i = 0;(nServicing > 0) && i < 60; i++) {
				try {
					if (i == 0)
					{
						logger.logp(Level.INFO, CLASS_NAME,"destroy", "waiting.to.destroy.filter.[{0}]", _filterName);
					}
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {
					com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.servlet.ServletInstance.destroy", "377", this);
				}
			}

			if(_eventSource != null && _eventSource.hasFilterListeners()){
				// LIDB-3598: begin
				_eventSource.onFilterStartDestroy(getFilterEvent());
				// LIDB-3598: end
				_filterInstance.destroy();
				// LIDB-3598: begin
				_eventSource.onFilterFinishDestroy(getFilterEvent());
				// LIDB-3598: end
			}
			else{
				_filterInstance.destroy();
			}

			_filterState = FILTER_STATE_DESTROYED;
        }
        catch (Throwable th)
        {
    		if(_eventSource != null && _eventSource.hasFilterErrorListeners()){
	        	FilterErrorEvent errorEvent = getFilterErrorEvent(th);
	        	_eventSource.onFilterDestroyError(errorEvent);
    		}
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, "com.ibm.ws.webcontainer.filter.FilterInstanceWrapper.destroy", "173", this);
            _filterState = FILTER_STATE_UNAVAILABLE;
            throw new ServletException(MessageFormat.format("Filter [{0}]: could not be destroyed", new Object[] {_filterName}), th);
        }
    }
    
	// LIDB-3598: begin
    private FilterEvent getFilterEvent()
    {
        if(_filterEvent != null)
        {
            return _filterEvent;
        } else
        {
            _filterEvent = new FilterEvent(this, _filterConfig);
            return _filterEvent;
        }
    }

    private FilterInvocationEvent getFilterInvocationEvent(ServletRequest req)
    {
    	return  new FilterInvocationEvent(this, _filterConfig, req);
    }
	// LIDB-3598: end
    
    private FilterErrorEvent getFilterErrorEvent (Throwable error){
    	return new FilterErrorEvent ( this, _filterConfig, error);
    }
    
    
}
