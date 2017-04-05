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

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Ensures that values read from XMLs are trimmed to remove extra white spaces
 */
@Trivial
public class MetagenXmlAdapter extends XmlAdapter<String, String> {
    @Override
    public String unmarshal(String v) throws Exception {
        if (v == null)
            return null;

        return v.trim();
    }

    @Override
    public String marshal(String v) throws Exception {
        if (v == null)
            return null;

        return v.trim();
    }

}
