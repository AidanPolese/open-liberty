/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 *
 */

package com.ibm.ws.config.xml.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.equinox.metatype.EquinoxAttributeDefinition;
import org.eclipse.equinox.metatype.EquinoxObjectClassDefinition;

import com.ibm.ws.config.xml.internal.metatype.ExtendedObjectClassDefinition;

public class MockObjectClassDefinition extends BaseDefinition implements EquinoxObjectClassDefinition {

    private final Map<String, EquinoxAttributeDefinition> requiredAttributeMap;
    private final Map<String, EquinoxAttributeDefinition> optionalAttributeMap;

    public MockObjectClassDefinition(String id) {
        super(id);
        this.requiredAttributeMap = new HashMap<String, EquinoxAttributeDefinition>();
        this.optionalAttributeMap = new HashMap<String, EquinoxAttributeDefinition>();
    }

    public void addAttributeDefinition(EquinoxAttributeDefinition attributeDef) {
        addAttributeDefinition(attributeDef, true);
    }

    public void addAttributeDefinition(EquinoxAttributeDefinition attributeDef, boolean required) {
        if (required) {
            requiredAttributeMap.put(attributeDef.getID(), attributeDef);
        } else {
            optionalAttributeMap.put(attributeDef.getID(), attributeDef);
        }
    }

    public Map<String, EquinoxAttributeDefinition> getAttributeDefinitionMap() {
        Map<String, EquinoxAttributeDefinition> all = new HashMap<String, EquinoxAttributeDefinition>();
        all.putAll(requiredAttributeMap);
        all.putAll(optionalAttributeMap);
        return all;
    }

    @Override
    public EquinoxAttributeDefinition[] getAttributeDefinitions(int filter) {
        Collection<EquinoxAttributeDefinition> attributes;
        if (filter == EquinoxObjectClassDefinition.REQUIRED) {
            attributes = requiredAttributeMap.values();
        } else if (filter == EquinoxObjectClassDefinition.OPTIONAL) {
            attributes = optionalAttributeMap.values();
        } else {
            attributes = new HashSet<EquinoxAttributeDefinition>();
            attributes.addAll(requiredAttributeMap.values());
            attributes.addAll(optionalAttributeMap.values());
        }
        return attributes.toArray(new EquinoxAttributeDefinition[attributes.size()]);
    }

    @Override
    public InputStream getIcon(int arg0) throws IOException {
        return null;
    }

    public void setSupportsExtensions(boolean supportsExtensions) {
        setExtensionAttribute(XMLConfigConstants.METATYPE_EXTENSION_URI, ExtendedObjectClassDefinition.SUPPORTS_EXTENSIONS_ATTRIBUTE,
                              (supportsExtensions) ? "true" : "false");
    }

    public void setSupportsHiddenExtensions(boolean supportsHiddenExtensions) {
        setExtensionAttribute(XMLConfigConstants.METATYPE_EXTENSION_URI, ExtendedObjectClassDefinition.SUPPORTS_HIDDEN_EXTENSIONS_ATTRIBUTE,
                              (supportsHiddenExtensions) ? "true" : "false");
    }

    public void setObjectClass(String objectClass) {
        setExtensionAttribute(XMLConfigConstants.METATYPE_EXTENSION_URI, ExtendedObjectClassDefinition.OBJECT_CLASS, objectClass);
    }

    public void setParentPid(String parentPid) {
        setExtensionAttribute(XMLConfigConstants.METATYPE_EXTENSION_URI, ExtendedObjectClassDefinition.PARENT_PID_ATTRIBUTE, parentPid);
    }

    public void setAlias(String alias) {
        setExtensionAttribute(XMLConfigConstants.METATYPE_EXTENSION_URI, ExtendedObjectClassDefinition.ALIAS_ATTRIBUTE, alias);
    }

    public String getAlias() {
        return getExtensionAttribute(XMLConfigConstants.METATYPE_EXTENSION_URI, ExtendedObjectClassDefinition.ALIAS_ATTRIBUTE);
    }

    public void setExtraProperties(boolean value) {
        setExtensionAttribute(XMLConfigConstants.METATYPE_UI_EXTENSION_URI, ExtendedObjectClassDefinition.METATYPE_EXTRA_PROPERTIES, String.valueOf(value));
    }

    /**
     * @param aliasName
     */
    public void setChildAlias(String aliasName) {
        setExtensionAttribute(XMLConfigConstants.METATYPE_EXTENSION_URI, ExtendedObjectClassDefinition.CHILD_ALIAS_ATTRIBUTE, aliasName);

    }

    public void setExtendsAlias(String aliasName) {
        setExtensionAttribute(XMLConfigConstants.METATYPE_EXTENSION_URI, ExtendedObjectClassDefinition.EXTENDS_ALIAS_ATTRIBUTE, aliasName);

    }
}
