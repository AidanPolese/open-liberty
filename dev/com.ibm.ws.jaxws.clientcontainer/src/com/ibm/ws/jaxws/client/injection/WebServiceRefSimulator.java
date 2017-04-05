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

package com.ibm.ws.jaxws.client.injection;

import java.lang.annotation.Annotation;

import javax.xml.ws.Service;
import javax.xml.ws.WebServiceRef;

/**
 * This class will implement the @WebServiceRef interace, and it
 * will be used to store metadata for a JAX-WS service-ref that
 * was found in a client deployment descriptor.
 * 
 */
public class WebServiceRefSimulator implements javax.xml.ws.WebServiceRef {

    private final String mappedName;

    private final String name;

    private final Class<?> type;

    private final Class<?> value;

    private final String wsdlLocation;

    private final String lookup;

    public WebServiceRefSimulator(String mappedName, String name, Class<?> type,
                                  Class<?> value, String wsdlLocation, String lookup) {
        this.mappedName = mappedName == null ? "" : mappedName;
        this.name = name == null ? "" : name;
        this.type = type == null ? Object.class : type;
        this.value = value == null ? Service.class : value;
        this.wsdlLocation = wsdlLocation == null ? "" : wsdlLocation;
        this.lookup = lookup == null ? "" : lookup;
    }

    @Override
    public String mappedName() {
        return mappedName;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class type() {
        return type;
    }

    @Override
    public Class value() {
        return value;
    }

    @Override
    public String wsdlLocation() {
        return wsdlLocation;
    }

    @Override
    public String lookup() {
        return lookup;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return WebServiceRef.class;
    }

    @Override
    public String toString() {
        String result = "webServiceRef = " + this.getClass().getName() + " { " +
                        "name= " + name + " type= " + type + " value= " + value + " mappedName= " + mappedName +
                        " lookup=" + lookup + " wsdlLocation= " + wsdlLocation + " }";
        return result;
    }
}