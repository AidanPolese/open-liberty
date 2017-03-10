/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.repository.resolver.internal.namespace;

import org.osgi.resource.Namespace;

/**
 * Namespace definition for core product kernel capabilities and requirements such as the product edition.
 */
public class ProductNamespace extends Namespace {

    /** Namespace name for core product kernel capabilities and requirements */
    public static final String PRODUCT_NAMESPACE = "com.ibm.ws.repo.product";
    /** The attribute name for the product version */
    public static final String CAPABILITY_VERSION_ATTRIBUTE = "version";
    /** The attribute name for the product ID */
    public static final String CAPABILITY_PRODUCT_ID_ATTRIBUTE = "productId";
    /** The attribute name for the install type */
    public static final String CAPABILITY_INSTALL_TYPE_ATTRIBUTE = "installType";
    /** The attribute name for the license type */
    public static final String CAPABILITY_LICENSE_TYPE_ATTRIBUTE = "licenseType";
    /** The attribute name for the edition */
    public static final String CAPABILITY_EDITION_ATTRIBUTE = "edition";

}
