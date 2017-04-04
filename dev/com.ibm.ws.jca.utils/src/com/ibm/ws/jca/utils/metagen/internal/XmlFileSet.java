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
package com.ibm.ws.jca.utils.metagen.internal;

import java.io.File;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.jca.utils.xml.ra.RaConnector;
import com.ibm.ws.jca.utils.xml.wlp.ra.WlpRaConnector;

/**
 * Used to hold RAR, ra.xml, wlp-ra.xml, and so on files for
 * a generator instance.
 */
@Trivial
public class XmlFileSet {
    public File rarFile;
    public File raXmlFile;
    public File wlpRaXmlFile;
    public RaConnector parsedXml;
    public WlpRaConnector parsedWlpXml;
    public String rarRaXmlFilePath;
    public String rarWlpRaXmlFilePath;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("XmlFileSet{");

        if (rarFile != null)
            sb.append("rarFile='").append(rarFile.getAbsolutePath()).append("' ");
        if (raXmlFile != null)
            sb.append("raXmlFile='").append(raXmlFile.getAbsolutePath()).append("' ");
        if (wlpRaXmlFile != null)
            sb.append("wlpRaXmlFile='").append(wlpRaXmlFile.getAbsolutePath()).append("' ");
        if (rarRaXmlFilePath != null)
            sb.append("rarRaXmlFilePath='").append(rarRaXmlFilePath).append("' ");
        if (rarWlpRaXmlFilePath != null)
            sb.append("wlpRaXmlFilePath='").append(rarWlpRaXmlFilePath).append("'");

        sb.append('}');
        return sb.toString();
    }
}
