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

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;

/**
 * DefaultLiteral represents an instance of the Default annotation
 */
public class DefaultLiteral extends AnnotationLiteral<Default> implements Default {

    public static final Annotation INSTANCE = new DefaultLiteral();

}
