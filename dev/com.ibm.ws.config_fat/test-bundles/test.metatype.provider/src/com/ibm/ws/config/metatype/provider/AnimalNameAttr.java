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

import com.ibm.websphere.metatype.MetaTypeFactory;

/**
 *
 */
public class AnimalNameAttr implements AttributeDefinition {

    /**  */
    public static final String ID_ATTRIBUTE = "name";

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
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.AttributeDefinition#getDescription()
     */
    @Override
    public String getDescription() {
        return "The animal name";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.AttributeDefinition#getID()
     */
    @Override
    public String getID() {
        return ID_ATTRIBUTE;
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
        return MetaTypeFactory.TOKEN_TYPE;
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
