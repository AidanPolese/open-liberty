// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

//CHANGE HISTORY
//Defect        Date        Modified By     Description
//--------------------------------------------------------------------------------------
//341303        01/25/06    mmolden         Change WebContainer APIs to allow modification of ServletConfig
//PK31043       09/06/06    ekoonce         expose addServletMapping method
//465095        09/06/07    ekoonce         add isServlet2_5 method
//PK66137       05/19/08    jebergma(mmolden) GLOBAL LISTENERS ARE NOT INVOKED

package com.ibm.wsspi.webcontainer.webapp;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.wsspi.webcontainer.filter.IFilterMapping;
import com.ibm.wsspi.webcontainer.servlet.IServletConfig;

/**
 * 
 * @ibm-private-in-use
 */
@SuppressWarnings("unchecked")
public interface WebAppConfig {
    /**
     * Returns the JSP Attributes for the web module that this config is
     * associated with
     * 
     * @return
     */
    public Map getJspAttributes();

    /**
     * Determines whether or not this web module is compliant with the Servlet
     * 2.4 specification
     * 
     * @return
     */
    public boolean isServlet2_4();

    /**
     * Determines whether or not this web module is compliant with the Servlet
     * 2.5 specification
     * 
     * @return
     */
    public boolean isServlet2_5();

    /**
     * Determines whether or not this web module is compliant with the Servlet
     * 2.4 specification or later
     * 
     * @return
     */

    public boolean isServlet2_4OrHigher();

    /**
     * Returns the name of the module that this config is associated with
     * 
     * @return
     */
    public String getModuleName();

    /**
     * Returns the name of the application this config is associated with
     * 
     * @return String
     */
    public String getApplicationName();

    /**
     * Returns an iterator containing all the ServletConfig instances which
     * represent all the targets that are present and loaded (not necessarily
     * initialized) in this web module
     * 
     * @return
     */
    public Iterator<IServletConfig> getServletInfos();
    
    /**
     * Returns a map whose keys are the servlet names and whose values are each servlet's servlet mappings list
     * @return
     */
    public Map<String, List<String>> getServletMappings();
    
    /**
     * Returns an iterator containing all the ServletNames
     * @return
     */
    public Iterator getServletNames();

    /**
     * Returns a list of all the TagLib definitions for this web module
     * 
     * @return
     */
    public java.util.List getTagLibs();

    /**
     * Returns the context root for the application which this web module is a
     * part of
     * 
     * @return
     */
    public String getContextRoot();

    /**
     * Returns the Module metadata associated with this config
     * 
     * @return
     */
    // RTC 160610. Moved to com.ibm.ws.webcontainer.webapp.WebAppConfigExtended.java
    // because WebModuleMetaData should not be exposed as spi.
    //public WebModuleMetaData getMetaData();

    /**
     * Determines whether or not this web module has enabled IBM custom
     * extension autoResponseEncoding.
     * 
     * @return
     */
    public boolean isAutoResponseEncoding();

    /**
     * Returns an iterator containing all the FilterConfig instances which
     * represent all the targets that are present and loaded (not necessarily
     * initialized) in this web module
     * 
     * @return
     */
    public Iterator getFilterInfos();

    public int getLastIndexBeforeDeclaredFilters();

    public void setLastIndexBeforeDeclaredFilters(int lastIndexBeforeDeclaredFilters);
    
    public List<IFilterMapping> getFilterMappings();

    // begin LIDB2356.1: WebContainer work for incorporating SIP
    /**
     * Returns the list of the virtual host mappings the web module has been
     * associated with.
     * 
     * @return
     */
    public List getVirtualHostList();

    /**
     * Returns the name of the virtual host that the web module has been
     * associated with.
     * 
     * @return
     */
    public String getVirtualHostName();

    /**
     * Returns the startup weight specified for the enterprise application
     * 
     * @return int
     */
    public int getAppStartupWeight();

    /**
     * Returns the startup weight specified for the web module
     * 
     * @return int
     */
    public int getModuleStartupWeight();

    /**
     * Returns whether the app is distributable. Can be used to indicate the
     * need for replication of session data.
     * 
     * @return boolean
     */
    public boolean isDistributable();

    // end LIDB2356.1: WebContainer work for incorporating SIP

    public boolean isSystemApp();

    public IServletConfig getServletInfo(String servletName);

    public void addServletInfo(String name, IServletConfig info);

    // PK31043
    /**
     * Adds a new urlPattern for the specified servlet to the list of mappings
     * 
     * @return
     */
    public void addServletMapping(String servletName, String urlPattern);

    public boolean isArdEnabled();

    public void setArdDispatchType(String ardDispatchType);

    public String getArdDispatchType();

    public void setMetadataComplete(boolean b);

    public boolean isMetadataComplete();

    public void addClassesToScan(List<Class<?>> list);

    public List<Class<?>> getClassesToScan();

    public void addUriMappedFilterInfo(IFilterMapping fmInfo);

    public void addServletMappedFilterInfo(IFilterMapping fmInfo);

    /**
     * Returns the listeners.
     * 
     * @return List
     */
    public List getListeners();

    /**
     * Adds a listener to be processed
     * 
     * @param listenerClass the name of the listener class to add
     */
    public void addListener(String listenerClass);

	List<String> getLibBinPathList();
	
	public void setJCDIEnabled(boolean b);
	public boolean isJCDIEnabled();
	
	public boolean isErrorPagePresent();

}
