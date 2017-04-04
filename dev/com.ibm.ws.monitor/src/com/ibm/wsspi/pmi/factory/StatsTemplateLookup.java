// IBM Confidential OCO Source Material
// 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/*
 * @(#)author    Srini Rangaswamy
 * @(#)version   1.1
 * @(#)date      07/15/04
 */

package com.ibm.wsspi.pmi.factory;

import com.ibm.websphere.pmi.PmiModuleConfig;

/**
 * Interface to lookup Stats template configuration.
 * 
 * @ibm-spi
 */

public interface StatsTemplateLookup {
    /**
     * Returns the {@link com.ibm.websphere.pmi.PmiModuleConfig} for a given template.
     * 
     * @param templateName Stats template name
     * @return an instance of PmiModuleConfig that corresponds to the template name
     */
    public PmiModuleConfig getTemplate(String templateName);
}
