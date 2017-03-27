/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.metatype.provider;

import org.osgi.service.metatype.AttributeDefinition;

/**
 *
 */
public class PlantNameAttr implements AttributeDefinition {

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.AttributeDefinition#getCardinality()
     */
    @Override
    public int getCardinality() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.AttributeDefinition#getDefaultValue()
     */
    @Override
    public String[] getDefaultValue() {
        return new String[] { "orchid" };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.AttributeDefinition#getDescription()
     */
    @Override
    public String getDescription() {
        return "The plant name";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.AttributeDefinition#getID()
     */
    @Override
    public String getID() {
        return "name";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.AttributeDefinition#getName()
     */
    @Override
    public String getName() {
        return "Name";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.AttributeDefinition#getOptionLabels()
     */
    @Override
    public String[] getOptionLabels() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.AttributeDefinition#getOptionValues()
     */
    @Override
    public String[] getOptionValues() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.AttributeDefinition#getType()
     */
    @Override
    public int getType() {
        return AttributeDefinition.STRING;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.AttributeDefinition#validate(java.lang.String)
     */
    @Override
    public String validate(String arg0) {
        return "";
    }

}
