/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.featureverifier.report;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 */
public class ReviewFileFilter implements FilenameFilter {

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    @Override
    public boolean accept(File dir, String name) {
        if (name.startsWith(ReportConstants.REVIEWED_PREFIX) && name.endsWith(ReportConstants.XML_SUFFIX)) {
            return true;
        }
        return false;
    }

}
