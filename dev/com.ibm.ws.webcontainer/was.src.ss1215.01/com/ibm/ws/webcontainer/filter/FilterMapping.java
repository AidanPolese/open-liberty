// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//			301121    	08/26/05		todkap				WebApp fails to handle wsspi implementation of IFilterConfig    WASCC.web.webcontainer    
//          318414      11/01/05        mmolden             RRD Security and Filter changes
//
//Code added as part of LIDB 2283-4
package com.ibm.ws.webcontainer.filter;

import javax.servlet.DispatcherType;

import com.ibm.wsspi.webcontainer.WCCustomProperties;
import com.ibm.wsspi.webcontainer.filter.IFilterConfig;
import com.ibm.wsspi.webcontainer.filter.IFilterMapping;
import com.ibm.wsspi.webcontainer.servlet.IServletConfig;

public class FilterMapping implements IFilterMapping {
    private String urlPattern;
    private DispatcherType[] dispatchMode = { DispatcherType.REQUEST }; // Default is
                                                                   // request
    private IFilterConfig filterConfig;
    private IServletConfig sconfig;
    private int mappingType;

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.webcontainer.filter.IFilterMapping#getMappingType()
     */
    public int getMappingType() {
        return mappingType;
    }

    public FilterMapping(String urlPattern, IFilterConfig fconfig, IServletConfig sconfig) {
        filterConfig = fconfig;
        if (urlPattern != null)
            setUrlPattern(urlPattern);
        this.sconfig = sconfig;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.webcontainer.filter.IFilterMapping#getFilterConfig()
     */
    public IFilterConfig getFilterConfig() {
        return filterConfig;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.webcontainer.filter.IFilterMapping#getUrlPattern()
     */
    public String getUrlPattern() {
        return urlPattern;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.ws.webcontainer.filter.IFilterMapping#setFilterConfig(com.ibm
     * .wsspi.webcontainer.filter.IFilterConfig)
     */
    public void setFilterConfig(IFilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.ws.webcontainer.filter.IFilterMapping#setUrlPattern(java.lang
     * .String)
     */
    public void setUrlPattern(String filterURI) {
        // determine what type of filter uri we have and set the mapping type
        if (filterURI.equals("/"))
            mappingType = WebAppFilterManager.FMI_MAPPING_SINGLE_SLASH;
        else if (filterURI.startsWith("/") && filterURI.endsWith("/*")) {
            mappingType = WebAppFilterManager.FMI_MAPPING_PATH_MATCH;

            // go ahead and strip the /* for later matching
            filterURI = filterURI.substring(0, filterURI.length() - 2);
        } else if (filterURI.startsWith("*."))
            mappingType = WebAppFilterManager.FMI_MAPPING_EXTENSION_MATCH;
        // PK57083 START
        else if (WCCustomProperties.MAP_FILTERS_TO_ASTERICK && filterURI.equals("*")) {
            mappingType = WebAppFilterManager.FMI_MAPPING_PATH_MATCH;
            filterURI = "";
        }
        // PK57083 END
        else
            mappingType = WebAppFilterManager.FMI_MAPPING_EXACT_MATCH;

        this.urlPattern = filterURI;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.webcontainer.filter.IFilterMapping#getServletConfig()
     */
    public IServletConfig getServletConfig() {
        return sconfig;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.webcontainer.filter.IFilterMapping#getDispatchMode()
     */
    public DispatcherType[] getDispatchMode() {
        return dispatchMode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.webcontainer.filter.IFilterMapping#setDispatchMode(int[])
     */
    public void setDispatchMode(DispatcherType[] dispatchMode) {
        this.dispatchMode = dispatchMode;
    }

}
