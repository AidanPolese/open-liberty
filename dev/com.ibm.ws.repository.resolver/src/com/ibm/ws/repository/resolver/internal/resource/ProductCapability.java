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

package com.ibm.ws.repository.resolver.internal.resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ibm.ws.repository.connections.ProductDefinition;
import com.ibm.ws.repository.resolver.internal.LibertyVersion;
import com.ibm.ws.repository.resolver.internal.namespace.ProductNamespace;

/**
 * This class represents product capabilities.
 */
public class ProductCapability extends CapabilityImpl {

    final Map<String, Object> attributes = new HashMap<String, Object>();

    public ProductCapability(ProductDefinition productDefinition) {
        this.attributes.put(ProductNamespace.CAPABILITY_PRODUCT_ID_ATTRIBUTE, productDefinition.getId());
        this.attributes.put(ProductNamespace.CAPABILITY_VERSION_ATTRIBUTE, LibertyVersion.valueOf(productDefinition.getVersion()));
        this.attributes.put(ProductNamespace.CAPABILITY_INSTALL_TYPE_ATTRIBUTE, productDefinition.getInstallType());
        this.attributes.put(ProductNamespace.CAPABILITY_LICENSE_TYPE_ATTRIBUTE, productDefinition.getLicenseType());
        this.attributes.put(ProductNamespace.CAPABILITY_EDITION_ATTRIBUTE, productDefinition.getEdition());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.resource.Capability#getNamespace()
     */
    @Override
    public String getNamespace() {
        return ProductNamespace.PRODUCT_NAMESPACE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.resource.Capability#getDirectives()
     */
    @Override
    public Map<String, String> getDirectives() {
        return Collections.emptyMap();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.resource.Capability#getAttributes()
     */
    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

}
