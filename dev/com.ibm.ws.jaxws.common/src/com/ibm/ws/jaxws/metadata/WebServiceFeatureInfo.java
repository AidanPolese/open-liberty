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
package com.ibm.ws.jaxws.metadata;

import java.io.Serializable;

import javax.xml.ws.WebServiceFeature;

/**
 *
 */
public interface WebServiceFeatureInfo extends Serializable {

    public WebServiceFeature getWebServiceFeature();

}
