// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//  CHANGE HISTORY
// Defect       Date        Modified By     Description
//--------------------------------------------------------------------------------------
// 329029       12/15/05    mmolden         IServletConfig.setStatisticsEnabled does not work                                                                                        
 
package com.ibm.wsspi.webcontainer.servlet;

import java.util.HashMap;

/**
 * 
 * ServletConfigParmMap is an spi for a map that can be passed in to 
 * configure various parts of a ServletConfig. It restricts the values that
 * can be placed in the map based on the ServletConfigParmKey keys.
 * 
 * @ibm-private-in-use
 * 
 * @since   WAS7.0
 *
 */
@SuppressWarnings("unchecked")
public class ServletConfigParmMap {
	static class ServletConfigParmKey
	{
		String key = null;
	     public ServletConfigParmKey(String key)
	     {
	     	this.key = key;
	     }
	     public String getKey (){
	     	return key;
	     }
	}
	public static final ServletConfigParmKey ATTRIBUTE = new ServletConfigParmKey("Attribute");
	public static final ServletConfigParmKey CLASSNAME = new ServletConfigParmKey("ClassName");
	public static final ServletConfigParmKey FILENAME = new ServletConfigParmKey("FileName");
	public static final ServletConfigParmKey CACHINGENABLED = new ServletConfigParmKey("CachingEnabled");
	public static final ServletConfigParmKey INITPARAMS = new ServletConfigParmKey("InitParams");
	public static final ServletConfigParmKey DISPLAYNAME = new ServletConfigParmKey("DisplayName");
	public static final ServletConfigParmKey ISJSP = new ServletConfigParmKey("IsJsp");
	public static final ServletConfigParmKey SERVLETCONTEXT = new ServletConfigParmKey("ServletContext");
	public static final ServletConfigParmKey STARTUPWEIGHT = new ServletConfigParmKey("StartUpWeight");
	public static final ServletConfigParmKey SERVLETNAME= new ServletConfigParmKey("ServletName");
	public static final ServletConfigParmKey STATISTICSENABLED = new ServletConfigParmKey("StatisticsEnabled");
	private HashMap _map=null;
	
	public ServletConfigParmMap(){
		_map = new HashMap (10,1);
	}
	
	public void put(ServletConfigParmKey key,Object value){
		_map.put(key.getKey(),value);
	}
	
	public Object get(ServletConfigParmKey key){
		return _map.get(key.getKey());
	}
	
}
