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
package com.ibm.ws.config.xml.internal;

import com.ibm.websphere.config.ConfigEvaluatorException;
import com.ibm.ws.config.xml.internal.metatype.ExtendedAttributeDefinition;

/**
 *
 */
class AttributeValidationException extends ConfigEvaluatorException {

    /**  */
    private static final long serialVersionUID = -8873485148740653410L;

    private final String validateResult;
    private final ExtendedAttributeDefinition attributeDefintion;
    private final String value;

    /**
     * @param attrDef
     * @param validateResult
     * @param validateResult2
     */
    public AttributeValidationException(ExtendedAttributeDefinition inAttrDef, String inValue, String inValidateResult) {
        super(inValidateResult);
        this.value = inValue;
        this.attributeDefintion = inAttrDef;
        this.validateResult = inValidateResult;
    }

    /**
     * @return the validateResult
     */
    public String getValidateResult() {
        return validateResult;
    }

    /**
     * @return the attributeDefintion
     */
    public ExtendedAttributeDefinition getAttributeDefintion() {
        return attributeDefintion;
    }

    /**
     * @return
     */
    public Object getValue() {
        return this.value;
    }

}
