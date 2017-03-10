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
public class DuplicateProductInfoException extends Exception {
    private final ProductInfo productInfo1;
    private final ProductInfo productInfo2;

    DuplicateProductInfoException(ProductInfo productInfo1, ProductInfo productInfo2) {
        super(productInfo1.getId() + ": " + productInfo1.getFile().getAbsoluteFile() + " and " + productInfo2.getFile().getAbsolutePath());
        this.productInfo1 = productInfo1;
        this.productInfo2 = productInfo2;
    }

    public ProductInfo getProductInfo1() {
        return productInfo1;
    }

    public ProductInfo getProductInfo2() {
        return productInfo2;
    }

    private void writeObject(ObjectOutputStream oos) throws NotSerializableException {
        throw new NotSerializableException();
    }
}
