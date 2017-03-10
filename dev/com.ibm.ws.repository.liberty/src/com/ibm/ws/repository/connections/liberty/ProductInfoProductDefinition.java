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

package com.ibm.ws.repository.connections.liberty;

import com.ibm.ws.kernel.productinfo.ProductInfo;
import com.ibm.ws.repository.connections.ProductDefinition;

/**
 * A {@link ProductDefinition} that is backed by a {@link ProductInfo} object. If the underlying {@link ProductInfo} changes then this class will reflect those changes.
 */
public class ProductInfoProductDefinition implements ProductDefinition {

    private final ProductInfo productInfo;

    /**
     * @param productInfo the {@link ProductInfo} that will supply all the information about the product
     */
    public ProductInfoProductDefinition(ProductInfo productInfo) {
        super();
        this.productInfo = productInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.repository.resolver.ProductDefinition#getId()
     */
    @Override
    public String getId() {
        return this.productInfo.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.repository.resolver.ProductDefinition#getVersion()
     */
    @Override
    public String getVersion() {
        return this.productInfo.getVersion();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.repository.resolver.ProductDefinition#getInstallType()
     */
    @Override
    public String getInstallType() {
        return this.productInfo.getProperty("com.ibm.websphere.productInstallType");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.repository.resolver.ProductDefinition#getLicenseType()
     */
    @Override
    public String getLicenseType() {
        return this.productInfo.getProperty("com.ibm.websphere.productLicenseType");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.repository.resolver.ProductDefinition#getEdition()
     */
    @Override
    public String getEdition() {
        return this.productInfo.getEdition();
    }

}
