/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.feature.internal.cmdline;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import wlp.lib.extract.LicenseProvider;

/**
 * This implementation of {@link LicenseProvider} will obtain all of the information about the license from a Subsystem-License header value.
 */
public class ThirdPartyLicenseProvider implements LicenseProvider {

    private final String programName;
    private final String subsystemLicenseHeader;

    public ThirdPartyLicenseProvider(String programName, String subsystemLicenseHeader) {
        this.programName = programName;
        this.subsystemLicenseHeader = subsystemLicenseHeader;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wlp.lib.extract.LicenseProvider#getProgramName()
     */
    @Override
    public String getProgramName() {
        return this.programName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wlp.lib.extract.LicenseProvider#getLicenseName()
     */
    @Override
    public String getLicenseName() {
        return this.subsystemLicenseHeader;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wlp.lib.extract.LicenseProvider#getLicenseAgreement()
     */
    @Override
    public InputStream getLicenseAgreement() {
        return getInputStream();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wlp.lib.extract.LicenseProvider#getLicenseInformation()
     */
    @Override
    public InputStream getLicenseInformation() {
        return getInputStream();
    }

    /**
     * @return
     */
    private InputStream getInputStream() {
        try {
            return new ByteArrayInputStream(this.subsystemLicenseHeader.getBytes("UTF-16"));
        } catch (UnsupportedEncodingException e) {
            return new ByteArrayInputStream(this.subsystemLicenseHeader.getBytes());
        }
    }
}
