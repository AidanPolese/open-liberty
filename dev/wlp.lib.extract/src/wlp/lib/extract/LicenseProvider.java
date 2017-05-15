/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package wlp.lib.extract;

import java.io.InputStream;

/**
 * Implementations of this interface provide information and text for the license of a particular product.
 */
public interface LicenseProvider {

    /**
     * The name of the program this license is for.
     *
     * @return
     */
    public String getProgramName();

    /**
     * The name of the license for this program.
     *
     * @return
     */
    public String getLicenseName();

    /**
     * Returns an input stream to the license agreement.
     *
     * @return
     */
    public InputStream getLicenseAgreement();

    /**
     * Returns an input stream to the license information.
     *
     * @return
     */
    public InputStream getLicenseInformation();
}
