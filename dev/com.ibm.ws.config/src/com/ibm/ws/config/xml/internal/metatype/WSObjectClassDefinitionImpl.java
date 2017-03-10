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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

import com.ibm.websphere.metatype.ObjectClassDefinitionProperties;

/**
 *
 */
public class WSObjectClassDefinitionImpl implements ObjectClassDefinition {

    private final ArrayList<AttributeDefinition> requiredAttributeDefinitions = new ArrayList<AttributeDefinition>();
    private final ArrayList<AttributeDefinition> optionalAttributeDefinitions = new ArrayList<AttributeDefinition>();
    private final ObjectClassDefinitionProperties properties;

    /**
     * @param props ocd properties
     * @param requiredAttributes required AttributeDefintions
     * @param optionalAttributes optional AttributeDefintions
     */
    public WSObjectClassDefinitionImpl(ObjectClassDefinitionProperties props, List<AttributeDefinition> requiredAttributes, List<AttributeDefinition> optionalAttributes) {
        if (requiredAttributes == null)
            throw new IllegalArgumentException("An ObjectClassDefinition must have at least one Attribute");

        this.requiredAttributeDefinitions.addAll(requiredAttributes);
        this.optionalAttributeDefinitions.addAll(optionalAttributes);
        this.properties = props;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.service.metatype.ObjectClassDefinition#getAttributeDefinitions(int)
     */
    @Override
    public AttributeDefinition[] getAttributeDefinitions(int filter) {
        List<AttributeDefinition> adList;
        if (filter == ObjectClassDefinition.ALL) {
            adList = new ArrayList<AttributeDefinition>(requiredAttributeDefinitions.size() + optionalAttributeDefinitions.size());
            adList.addAll(requiredAttributeDefinitions);
            adList.addAll(optionalAttributeDefinitions);
        } else if (filter == ObjectClassDefinition.OPTIONAL) {
            adList = optionalAttributeDefinitions;
        } else if (filter == ObjectClassDefinition.REQUIRED) {
            adList = requiredAttributeDefinitions;
        } else {
            throw new IllegalArgumentException("Unexpected filter value: " + filter);
        }
        AttributeDefinition[] ads = new AttributeDefinition[adList.size()];
        return adList.toArray(ads);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.service.metatype.ObjectClassDefinition#getDescription()
     */
    @Override
    public String getDescription() {
        return properties.getDescription();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.service.metatype.ObjectClassDefinition#getID()
     */
    @Override
    public String getID() {
        return properties.getId();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.service.metatype.ObjectClassDefinition#getIcon(int)
     */
    @Override
    public InputStream getIcon(int arg0) throws IOException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.service.metatype.ObjectClassDefinition#getName()
     */
    @Override
    public String getName() {
        return properties.getName();
    }

    public String getAlias() {
        return properties.getAlias();
    }

    public String getChildAlias() {
        return null;
    }

    public String getExtendsAlias() {
        return properties.getExtendsAlias();
    }

    public String getExtends() {
        return properties.getExtends();
    }

    public String getParentPID() {
        return properties.getParentPID();
    }

    public boolean supportsExtensions() {
        return properties.supportsExtensions();
    }

    public boolean supportsHiddenExtensions() {
        return properties.supportsHiddenExtensions();
    }

    public List<String> getObjectClass() {
        return properties.getObjectClass();
    }

    /**
     *
     * @param ad
     */
    public void addAttributeDefinition(AttributeDefinition ad) {
        requiredAttributeDefinitions.add(ad);
    }

}
