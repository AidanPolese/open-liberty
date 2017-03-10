/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.xml.internal.metatype;

import org.osgi.service.metatype.AttributeDefinition;

import com.ibm.websphere.metatype.AttributeDefinitionProperties;

/**
 *
 */
public class WSAttributeDefinitionImpl implements AttributeDefinition {

    private final AttributeDefinitionProperties properties;

    /**
     * @param props
     */
    public WSAttributeDefinitionImpl(AttributeDefinitionProperties props) {
        this.properties = props;
    }

    @Override
    public int getCardinality() {
        return properties.getCardinality();
    }

    @Override
    public String[] getDefaultValue() {
        return properties.getDefaultValue();
    }

    @Override
    public String getDescription() {
        return properties.getDescription();
    }

    @Override
    public String getID() {
        return properties.getId();
    }

    @Override
    public String getName() {
        return properties.getName();
    }

    @Override
    public String[] getOptionLabels() {
        return properties.getOptionLabels();
    }

    @Override
    public String[] getOptionValues() {
        return properties.getOptionValues();
    }

    @Override
    public int getType() {
        return properties.getType();
    }

    @Override
    public String validate(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getReferencePid() {
        return properties.getReferencePid();
    }

    public String getService() {
        return properties.getService();
    }

    public String getServiceFilter() {
        return properties.getServiceFilter();
    }

    public boolean isFinal() {
        return properties.isFinal();
    }

    public String getVariable() {
        return properties.getVariable();
    }

    public boolean isUnique() {
        return properties.isUnique();
    }

    public String getUnique() {
        return properties.getUnique();
    }

    public boolean isFlat() {
        return properties.isFlat();
    }

    public String getCopyOf() {
        return properties.getCopyOf();
    }

}
