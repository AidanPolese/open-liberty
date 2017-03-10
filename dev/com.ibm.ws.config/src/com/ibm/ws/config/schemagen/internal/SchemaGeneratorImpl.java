/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.schemagen.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.equinox.metatype.EquinoxMetaTypeService;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.MetaTypeInformation;

import com.ibm.websphere.metatype.SchemaGenerator;
import com.ibm.websphere.metatype.SchemaGeneratorOptions;

/**
 * 
 */
public class SchemaGeneratorImpl implements SchemaGenerator {

    private EquinoxMetaTypeService metaTypeService;

    ComponentContext ctxt;

    protected void activate(ComponentContext ctxt) throws Exception {
        this.ctxt = ctxt;
    }

    @Override
    public void generate(OutputStream out, SchemaGeneratorOptions options) throws IOException {
        String encoding = options.getEncoding();
        Writer writer = (encoding == null) ? new OutputStreamWriter(out) : new OutputStreamWriter(out, encoding);
        generate(writer, options);
    }

    @Override
    public void generate(Writer writer, SchemaGeneratorOptions options) throws IOException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            XMLStreamWriter xmlWriter = new IndentingXMLStreamWriter(factory.createXMLStreamWriter(writer), writer);
            generate(xmlWriter, options);
        } catch (XMLStreamException e) {
            throw new IOException("Error generating schema", e);
        }
    }

    private void generate(XMLStreamWriter xmlWriter, SchemaGeneratorOptions options) throws XMLStreamException {
        SchemaWriter schemaWriter = new SchemaWriter(xmlWriter);
        schemaWriter.setEncoding(options.getEncoding());
        schemaWriter.setGenerateDocumentation(true);
        schemaWriter.setLocale(options.getLocale());
        schemaWriter.setIgnoredPids(options.getIgnoredPids());
        schemaWriter.setIsRuntime(options.isRuntime());

        for (Bundle bundle : options.getBundles()) {
            MetaTypeInformation info = metaTypeService.getMetaTypeInformation(bundle);
            schemaWriter.add(info);
        }

        schemaWriter.generate(true);
    }

    protected void setMetaTypeService(EquinoxMetaTypeService ref) {
        this.metaTypeService = ref;
    }

    protected void unsetMetaTypeService(EquinoxMetaTypeService ref) {
        this.metaTypeService = null;
    }

}
