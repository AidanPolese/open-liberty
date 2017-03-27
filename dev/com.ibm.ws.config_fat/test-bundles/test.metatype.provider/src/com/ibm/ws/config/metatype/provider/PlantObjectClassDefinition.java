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

import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 *
 */
public class PlantObjectClassDefinition implements ObjectClassDefinition {

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.ObjectClassDefinition#getAttributeDefinitions(int)
     */
    @Override
    public AttributeDefinition[] getAttributeDefinitions(int arg0) {
        AttributeDefinition nameAttr = new PlantNameAttr();
        return new AttributeDefinition[] { nameAttr };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.ObjectClassDefinition#getDescription()
     */
    @Override
    public String getDescription() {
        return "A Plant.";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.ObjectClassDefinition#getID()
     */
    @Override
    public String getID() {
        return "test.metatype.provider.plant";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.ObjectClassDefinition#getIcon(int)
     */
    @Override
    public InputStream getIcon(int arg0) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.ObjectClassDefinition#getName()
     */
    @Override
    public String getName() {
        return "test.metatype.provider.plant";
    }

}
