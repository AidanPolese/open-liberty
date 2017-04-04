/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jpa.container.osgi.internal;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.ibm.ejs.util.Util;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.jpa.management.JPAApplInfo;
import com.ibm.ws.jpa.management.JPAPXml;
import com.ibm.ws.jpa.management.JPAPuScope;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

@Trivial
public class OSGiJPAPXml extends JPAPXml {
    private static final TraceComponent tc = Tr.register(OSGiJPAPXml.class);

    private final Entry ivPxml;

    /**
     * @param pxmlUrl
     */
    OSGiJPAPXml(JPAApplInfo applInfo, String archiveName, JPAPuScope puScope, URL puRoot, ClassLoader classloader, Entry pxml) {
        super(applInfo, archiveName, puScope, puRoot, classloader);
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "<init> : " + pxml);
        ivPxml = pxml;
    }

    /** {@inheritDoc} */
    @Override
    protected InputStream openStream() throws IOException {
        try {
            return ivPxml.adapt(InputStream.class);
        } catch (UnableToAdaptException ex) {
            throw new IOException(ivPxml.toString(), ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected Schema newSchema(String xsdName) throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);

        // Obtain the xsd file from the jpa container bundle
        String resName = "com/ibm/ws/jpa/schemas/javaee/" + xsdName;
        URL xsdUrl = JPAPXml.class.getClassLoader().getResource(resName);
        if (xsdUrl == null) {
            throw new RuntimeException(resName + " not found");
        }

        Schema schema = schemaFactory.newSchema(xsdUrl);

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "newSchema : " + Util.identity(schema));

        return schema;
    }

    /**
     * Returns the parent of the container that holds the persistence.xml (META-INF)
     */
    Container getPuRootContainer() {
        return ivPxml.getEnclosingContainer();
    }
}
