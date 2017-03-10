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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.resource.Capability;

import com.ibm.ws.repository.connections.ProductDefinition;

/**
 * This resource represents an installation of the main product.
 */
// No need to implement comparable as we only ever have on product
public class ProductResource extends ResourceImpl {

    /**
     * Create an instance of this class using the supplied product infos to supply information about the product
     * 
     * @param productDefinitions The information about the product
     * @return The instance
     */
    public static ProductResource createInstance(Collection<ProductDefinition> productDefinitions) {
        List<Capability> capabilities = new ArrayList<Capability>();
        for (ProductDefinition productDefinition : productDefinitions) {
            capabilities.add(new ProductCapability(productDefinition));
        }

        return new ProductResource(capabilities);
    }

    /**
     * Construct a new instance of this class with the supplied capabilities
     * 
     * @param capabilities
     */
    private ProductResource(List<Capability> capabilities) {
        // The product is already installed so has no requirements and is never in massive so pass null as the final arg
        super(capabilities, null, LOCATION_INSTALL, null);
    }

}
