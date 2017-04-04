package com.ibm.wsspi.security.wim.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.ibm.websphere.security.wim.ras.WIMTraceHelper;

/**
 * <p>Java class for RolePlayer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RolePlayer">
 * &lt;complexContent>
 * &lt;extension base="{http://www.ibm.com/websphere/wim}Entity">
 * &lt;sequence>
 * &lt;element name="partyRoles" type="{http://www.ibm.com/websphere/wim}PartyRole" maxOccurs="unbounded" minOccurs="0"/>
 * &lt;/sequence>
 * &lt;/extension>
 * &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * <p> The RolePlay object extends from the Entity object. It has all of the properties defined by the
 * Entity object, and in addition, contains the "partyRoles" property:
 * 
 * <ul>
 * <li><b>partyroles</b>: a containment property which is used to link the party roles the role player is
 * playing. <b>partyRoles</b> may contain multiple PartyRole objects since a role player can play multiple roles.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RolePlayer", propOrder = {
                                           "partyRoles"
})
@XmlSeeAlso({
             com.ibm.wsspi.security.wim.model.PartyRole.class,
             Party.class
})
public class RolePlayer
                extends Entity
{

    protected List<com.ibm.wsspi.security.wim.model.PartyRole> partyRoles;
    private static List mandatoryProperties = null;
    private static List transientProperties = null;
    private static List propertyNames = null;
    private static HashMap dataTypeMap = null;
    private static ArrayList superTypeList = null;
    private static HashSet subTypeList = null;

    static {
        setMandatoryPropertyNames();
        setTransientPropertyNames();
        setDataTypeMap();
        setSuperTypes();
        setSubTypes();
    }

    /**
     * Gets the value of the partyRoles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the partyRoles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     * getPartyRoles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link com.ibm.wsspi.security.wim.model.PartyRole }
     * 
     * 
     */
    public List<com.ibm.wsspi.security.wim.model.PartyRole> getPartyRoles() {
        if (partyRoles == null) {
            partyRoles = new ArrayList<com.ibm.wsspi.security.wim.model.PartyRole>();
        }
        return this.partyRoles;
    }

    public boolean isSetPartyRoles() {
        return ((this.partyRoles != null) && (!this.partyRoles.isEmpty()));
    }

    public void unsetPartyRoles() {
        this.partyRoles = null;
    }

    @Override
    public Object get(String propName) {
        if (propName.equals("partyRoles")) {
            return getPartyRoles();
        }
        return super.get(propName);
    }

    @Override
    public boolean isSet(String propName) {
        if (propName.equals("partyRoles")) {
            return isSetPartyRoles();
        }
        return super.isSet(propName);
    }

    @Override
    public void set(String propName, Object value) {
        if (propName.equals("partyRoles")) {
            getPartyRoles().add(((com.ibm.wsspi.security.wim.model.PartyRole) value));
        }
        super.set(propName, value);
    }

    @Override
    public void unset(String propName) {
        if (propName.equals("partyRoles")) {
            unsetPartyRoles();
        }
        super.unset(propName);
    }

    @Override
    public String getTypeName() {
        return "RolePlayer";
    }

    private static synchronized void setMandatoryPropertyNames() {
        if (mandatoryProperties != null) {
            return;
        }
        mandatoryProperties = new ArrayList();
    }

    private static synchronized void setTransientPropertyNames() {
        if (transientProperties != null) {
            return;
        }
        transientProperties = new ArrayList();
        transientProperties.addAll(Entity.getTransientProperties());
    }

    @Override
    public boolean isMandatory(String propName) {
        if (mandatoryProperties == null) {
            setMandatoryPropertyNames();
        }
        if (mandatoryProperties.contains(propName)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isPersistentProperty(String propName) {
        if (transientProperties == null) {
            setTransientPropertyNames();
        }
        if (transientProperties.contains(propName)) {
            return false;
        } else {
            return true;
        }
    }

    protected static List getTransientProperties()
    {
        if (transientProperties == null) {
            setTransientPropertyNames();
        }
        return transientProperties;
    }

    public static synchronized List getPropertyNames(String entityTypeName) {
        if (propertyNames != null) {
            return propertyNames;
        } else {
            {
                List names = new ArrayList();
                names.add("partyRoles");
                names.addAll(Entity.getPropertyNames("Entity"));
                propertyNames = Collections.unmodifiableList(names);
                return propertyNames;
            }
        }
    }

    private static synchronized void setDataTypeMap() {
        if (dataTypeMap == null) {
            dataTypeMap = new HashMap();
        }
        dataTypeMap.put("partyRoles", "PartyRole");
    }

    @Override
    public String getDataType(String propName) {
        if (dataTypeMap.containsKey(propName)) {
            return ((String) dataTypeMap.get(propName));
        } else {
            return super.getDataType(propName);
        }
    }

    private static synchronized void setSuperTypes() {
        if (superTypeList == null) {
            superTypeList = new ArrayList();
        }
        superTypeList.add("Entity");
    }

    @Override
    public ArrayList getSuperTypes() {
        if (superTypeList == null) {
            setSuperTypes();
        }
        return superTypeList;
    }

    @Override
    public boolean isSubType(String superTypeName) {
        return superTypeList.contains(superTypeName);
    }

    private static synchronized void setSubTypes() {
        if (subTypeList == null) {
            subTypeList = new HashSet();
        }
        subTypeList.add("Group");
        subTypeList.add("Party");
        subTypeList.add("PartyRole");
        subTypeList.add("OrgContainer");
        subTypeList.add("LoginAccount");
        subTypeList.add("Person");
        subTypeList.add("PersonAccount");
    }

    public static HashSet getSubTypes() {
        if (subTypeList == null) {
            setSubTypes();
        }
        return subTypeList;
    }

    @Override
    public String toString() {
        return WIMTraceHelper.trace(this);
    }

}
