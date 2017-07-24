/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.config.cdi;

import java.lang.annotation.Annotation;

import javax.enterprise.util.AnnotationLiteral;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * ConfigPropertyLiteral represents an instance of the ConfigProperty annotation
 */
public class ConfigPropertyLiteral extends AnnotationLiteral<ConfigProperty> implements ConfigProperty {

    /**  */
    private static final long serialVersionUID = 1L;
    public static final Annotation INSTANCE = new ConfigPropertyLiteral();

    /** {@inheritDoc} */
    @Override
    public String name() {
        return "";
    }

    /** {@inheritDoc} */
    @Override
    public String defaultValue() {
        return "";
    }

}
