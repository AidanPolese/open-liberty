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

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.metatype.AttributeDefinition;

import com.ibm.ws.config.xml.internal.metatype.ExtendedAttributeDefinition;
import com.ibm.ws.config.xml.internal.metatype.ExtendedAttributeDefinitionImpl;
import com.ibm.ws.config.xml.internal.schema.AttributeDefinitionSpecification;

class TypeMember {

    private final ExtendedAttributeDefinition attribute;
    private String id;
    private Type type;
    private String typeString;
    private String description;
    private boolean required;
    private int cardinality;
    private String[] defaultValue;
    private List<AppInfoEntry> appInfo;

    public TypeMember(ExtendedAttributeDefinition attribute) {
        this.attribute = attribute;
        this.type = Type.fromId(attribute.getType());
    }

    public ExtendedAttributeDefinition getAttribute() {
        return attribute;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isRequired() {
        return required;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setType(String type) {
        this.typeString = type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getType(boolean global) {
        if (type == null) {
            if (typeString == null) {
                return "xsd:string";
            } else {
                return typeString;
            }
        } else if (global) {
            return type.getGlobalSchemaType();
        } else {
            type.getSchemaBaseType();
        }

        return "xsd:string";
    }

    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }

    public int getCardinality() {
        return cardinality;
    }

    public void setDefaultValue(String[] defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String[] getDefaultValue() {
        return defaultValue;
    }

    private void initAppInfo() {
        if (appInfo == null) {
            appInfo = new ArrayList<AppInfoEntry>();
        }
    }

    public void addAppInfoEntry(AppInfoEntry entry) {
        initAppInfo();
        appInfo.add(entry);
    }

    public AppInfoEntry[] getAppInfoEntries() {
        initAppInfo();
        AppInfoEntry[] entries = new AppInfoEntry[appInfo.size()];
        return appInfo.toArray(entries);
    }

    /**
     * @return
     */
    public boolean isMinMaxSet() {

        if (attribute instanceof ExtendedAttributeDefinitionImpl) {
            AttributeDefinition ad = attribute.getDelegate();
            if (ad instanceof AttributeDefinitionSpecification) {
                AttributeDefinitionSpecification ads = (AttributeDefinitionSpecification) ad;

                return ads.getMin() != null || ads.getMax() != null;
            }
        }

        return false;
    }

    /**
     * @return
     */
    public String getMin() {
        if (attribute instanceof ExtendedAttributeDefinitionImpl) {
            AttributeDefinition ad = attribute.getDelegate();
            if (ad instanceof AttributeDefinitionSpecification) {
                AttributeDefinitionSpecification ads = (AttributeDefinitionSpecification) ad;

                return ads.getMin();
            }
        }
        return null;
    }

    /**
     * @return
     */
    public String getMax() {
        if (attribute instanceof ExtendedAttributeDefinitionImpl) {
            AttributeDefinition ad = attribute.getDelegate();
            if (ad instanceof AttributeDefinitionSpecification) {
                AttributeDefinitionSpecification ads = (AttributeDefinitionSpecification) ad;

                return ads.getMax();
            }
        }
        return null;
    }

    /**
     * @return
     */
    public Type getType() {
        return type;
    }
}