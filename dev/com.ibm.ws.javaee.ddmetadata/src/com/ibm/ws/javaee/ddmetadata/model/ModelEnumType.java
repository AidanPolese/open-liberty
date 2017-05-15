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
package com.ibm.ws.javaee.ddmetadata.model;

import java.util.List;

/**
 * Models a Java enum type.
 */
public class ModelEnumType extends ModelClassType {
    public static class Constant {
        public final String name;
        public final String xmiName;
        private boolean libertyNotInUse = false;

        public Constant(String name, String xmiName) {
            this.name = name;
            this.xmiName = xmiName;
        }

        public boolean isLibertyNotInUse() {
            return this.libertyNotInUse;
        }

        /**
         * @param tr
         */
        public void setLibertyNotInUse(boolean value) {
            this.libertyNotInUse = value;

        }
    }

    public final List<Constant> constants;

    public ModelEnumType(String className, List<Constant> constants) {
        super(className);
        this.constants = constants;
    }

    public boolean hasConstantName() {
        for (Constant constant : constants) {
            if (constant.name != null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasXMIConstantName() {
        for (Constant constant : constants) {
            if (constant.xmiName != null) {
                return true;
            }
        }
        return false;
    }

    public String getParseXMIAttributeValueMethodName() {
        return "parseXMI" + className.substring(className.lastIndexOf('.') + 1) + "AttributeValue";
    }

    @Override
    public String getDefaultValue(String string) {
        if (string == null) {
            return null;
        }

        for (Constant constant : constants) {
            if (constant.name.equals(string)) {
                return getJavaImplTypeName() + '.' + string;
            }
        }

        throw new IllegalArgumentException("invalid default value \"" + string + "\" for type " + className);
    }
}
