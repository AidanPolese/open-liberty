/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.productinfo;

import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

@SuppressWarnings("serial")
public class ProductInfoReplaceException extends Exception {
    private final ProductInfo productInfo;

    ProductInfoReplaceException(ProductInfo productInfo) {
        super(productInfo.getId() + " replaces " + productInfo.getReplacesId());
        this.productInfo = productInfo;
    }

    public ProductInfo getProductInfo() {
        return productInfo;
    }

    private void writeObject(ObjectOutputStream oos) throws NotSerializableException {
        throw new NotSerializableException();
    }
}
