/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 *
 * Change activity:
 *
 * Issue       Date        Name     Description
 * ----------- ----------- -------- ------------------------------------
 */

package com.ibm.ws.config.schemagen.internal;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

class AppInfoEntry {

    protected final String tag;
    protected final String text;
    protected final String altText;
    protected final Map<String, String> attributes;

    public AppInfoEntry(String tag) {
        this(tag, null, null);
    }

    public AppInfoEntry(String tag, String text) {
        this(tag, text, null);
    }

    public AppInfoEntry(String tag, String text, String altText) {
        this.tag = tag;
        this.text = (text == null) ? null : text.trim();
        this.altText = (altText == null) ? null : altText.trim();
        this.attributes = new HashMap<String, String>();
    }

    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public void write(XMLStreamWriter writer, boolean alternate) throws XMLStreamException {
        if (text == null) {
            writer.writeEmptyElement(SchemaWriter.IBM_EXT_NS, tag);
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                writer.writeAttribute(entry.getKey(), entry.getValue());
            }
        } else {
            writer.writeStartElement(SchemaWriter.IBM_EXT_NS, tag);
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                writer.writeAttribute(entry.getKey(), entry.getValue());
            }

            String toWrite = text;
            if (alternate && altText != null) {
                toWrite = altText;
            }

            writer.writeCharacters(toWrite);
            writer.writeEndElement();
        }
    }

    public static AppInfoEntry createLabelTag(String text, String string) {
        AppInfoEntry tag = new AppInfoEntry("label", text, string);
        return tag;
    }

    public static AppInfoEntry createRequiresTag(String id, final boolean value) {
        AppInfoEntry tag = new AppInfoEntry("requires");
        tag.addAttribute("id", id);
        tag.addAttribute("value", String.valueOf(value));
        return tag;
    }

    public static AppInfoEntry createGroupTag(String id) {
        AppInfoEntry tag = new AppInfoEntry("group");
        tag.addAttribute("id", id);
        return tag;
    }

    public static AppInfoEntry createGroupDeclarationTag(String id, final String label, final String description) {
        AppInfoEntry tag = new AppInfoEntry("groupDecl", description);
        tag.addAttribute("id", id);
        if (label != null) {
            tag.addAttribute("label", label);
        }
        return tag;
    }

    public static AppInfoEntry createExtraPropertiesTag() {
        AppInfoEntry tag = new AppInfoEntry("extraProperties");
        return tag;
    }

    public static AppInfoEntry createReferenceTag(String name) {
        AppInfoEntry tag = new AppInfoEntry("reference", name);
        return tag;
    }

    /**
     * @param variable
     * @return
     */
    public static AppInfoEntry createVariableTag(String variable) {
        return new AppInfoEntry("variable", variable);
    }

    /**
     * @param unique
     * @return
     */
    public static AppInfoEntry createUniqueTag(String unique) {
        return new AppInfoEntry("unique", unique);
    }

}
