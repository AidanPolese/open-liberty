/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
/**
 * @version 1.0.0
 */
@org.osgi.annotation.versioning.Version("1.0.0")
@TraceOptions(traceGroup = "rarInstall", messageBundle = "com.ibm.ws.jca.utils.internal.resources.JcaUtilsMessages")
@XmlSchema(namespace = "http://xmlns.jcp.org/xml/ns/javaee", elementFormDefault = XmlNsForm.QUALIFIED)
@XmlJavaTypeAdapter(value = MetagenXmlAdapter.class, type = String.class)
package com.ibm.ws.jca.utils.xml.ra;

import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.ibm.websphere.ras.annotation.TraceOptions;
import com.ibm.ws.jca.utils.metagen.internal.MetagenXmlAdapter;

