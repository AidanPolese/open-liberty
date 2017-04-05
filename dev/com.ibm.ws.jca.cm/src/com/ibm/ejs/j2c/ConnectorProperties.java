/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.j2c;

/*
 * Class name   : ConnectorProperties
 *
 * Scope        : Name server and EJB server
 *
 * Object model : 1 per deployed resource adapter
 *
 * Each instance is a Vector of configuration properties for a deployed resource adapter.
 */

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public final class ConnectorProperties extends Vector<Object> implements Serializable {

    private static final long serialVersionUID = -248509787807561932L;
    private static final TraceComponent tc = Tr.register(ConnectorProperties.class, J2CConstants.traceSpec, J2CConstants.messageFile); 
    public static final String nl = String.format("%n");

    // override the Vector add method to not add duplicate entries.  That is, entries with the same name.
    @Override
    public boolean add(Object o) {
        ConnectorProperty connectorPropertyToAdd = (ConnectorProperty) o;
        String nameToAdd = connectorPropertyToAdd.getName();

        ConnectorProperty connectorProperty = null;
        String name = null;
        Enumeration<Object> e = this.elements();
        while (e.hasMoreElements()) {

            connectorProperty = (ConnectorProperty) e.nextElement();
            name = connectorProperty.getName();
            if (name.equals(nameToAdd)) {
                if (tc.isDebugEnabled()) { 
                    String value = (String) connectorPropertyToAdd.getValue(); 
                    if (!value.equals("")) { 
                        if (name.equals("UserName") || name.equals("Password")) { 
                            Tr.debug(tc, "DUPLICATE_USERNAME_PASSWORD_CONNECTOR_PROPERTY_J2CA0103", new Object[] { (ConnectorProperty) o });
                        } else {
                            Tr.warning(tc, "DUPLICATE_CONNECTOR_PROPERTY_J2CA0308", new Object[] { (ConnectorProperty) o });
                        } 
                    }
                }
                return true;
            }

        }

        return super.add(o);

    }
    
    /**
     * Given this ConnectorProperties Vector, find the String identified by the
     * input desiredPropertyName. If not found, return the defaultValue.
     * 
     * @param desiredPropertyName Name of com.ibm.ejs.j2c.ConnectorProperty entry to look for.
     * @param defaultValue value to return if the desiredPropertyName is not found, or its value is invalid.
     * @return String
     */
    public String findConnectorPropertyString(String desiredPropertyName, String defaultValue) {

        String retVal = defaultValue;
        String name = null;
        ConnectorProperty property = null;

        Enumeration<Object> e = this.elements();
        while (e.hasMoreElements()) {

            property = (ConnectorProperty) e.nextElement();
            name = property.getName();

            if (name.equals(desiredPropertyName)) {
                retVal = (String) property.getValue();
            }

        }

        return retVal;

    }

    /*
     * toString is overriden to give a resonably readable view of the properties.
     */

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(100);
        ConnectorProperty prop;
        String propName = null; 

        buf.append("[Deployed Resource Adapter Properties]" + nl);

        for (int i = 0; i < elementCount; i++) {
            prop = (ConnectorProperty) elementAt(i);
            propName = prop.getName();

            if (propName.equals("Password")
                || propName.equals(J2CConstants.XA_RECOVERY_PASSWORD)) {
                // occlude the password property value with a fixed no. of asterisks:
                buf.append("\t");
                buf.append(propName);
                buf.append(" \t ");
                buf.append(prop.getType());
                buf.append(" \t ");
                buf.append("********");
                buf.append(nl);
            } else {
                buf.append("\t");
                buf.append(propName);
                buf.append(" \t ");
                buf.append(prop.getType());
                buf.append(" \t ");
                buf.append(prop.getValue());
                buf.append(nl);
            }
        } 

        return buf.toString();
    }
}