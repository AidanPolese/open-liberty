// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

/**
 * Generic filter event.
 * 
 * @ibm-api
 */

package com.ibm.websphere.servlet.event;


import java.util.EventObject;
import javax.servlet.FilterConfig;

public class FilterEvent extends EventObject
{

    /**
     * FilterEvent contructor.
     * @param source the object that triggered this event.
     * @param filterConfig the filter's FilterConfig.
     */
 public FilterEvent(Object source, FilterConfig filterConfig)
 {
     super(source);
     _filterConfig = filterConfig;
 }

 /**
  * Return the name of the Filter that this event is associated with.
  */
 public String getFilterName()
 {
     return _filterConfig.getFilterName();
 }

 /**
  * Return the FilterConfig that this event is associated with.
  */
 public FilterConfig getFilterConfig()
 {
     return _filterConfig;
 }

 private FilterConfig _filterConfig;
 private static final long serialVersionUID = 1L;
}
