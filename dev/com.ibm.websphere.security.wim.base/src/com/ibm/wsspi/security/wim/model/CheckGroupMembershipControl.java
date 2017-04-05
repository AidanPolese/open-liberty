package com.ibm.wsspi.security.wim.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.ibm.websphere.security.wim.ras.WIMTraceHelper;

/**
 * <p>Java class for CheckGroupMembershipControl complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CheckGroupMembershipControl">
 * &lt;complexContent>
 * &lt;extension base="{http://www.ibm.com/websphere/wim}Control">
 * &lt;attribute name="level" type="{http://www.w3.org/2001/XMLSchema}int" default="1" />
 * &lt;attribute name="inGroup" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 * &lt;/extension>
 * &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * <p> The CheckGroupMembershipControl object extends the Control object and defines two
 * properties: <b>level</b> and <b>inGroup</b>.
 * 
 * <p> In order to check whether a group contains a member or not, the caller can issue the
 * get() API call with one group object and one member object, and with the CheckGroupMembershipControl.
 * The underlying repository will be checked and a boolean value will be returned in the CheckGroupMembershipControl
 * object of the returned Root object to indicate the membership relationship, i.e, if the member is a part of the group.
 * 
 * <ul>
 * <li><b>level</b>: indicates the level of members to be returned.</li>
 * <li><b>inGroup</b>: indicates the result in the returned Root object after checking the group membership.
 * </ul>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CheckGroupMembershipControl")
public class CheckGroupMembershipControl
                extends Control
{

    @XmlAttribute(name = "level")
    protected Integer level;
    @XmlAttribute(name = "inGroup")
    protected Boolean inGroup;
    private static List propertyNames = null;
    private static HashMap dataTypeMap = null;
    private static ArrayList superTypeList = null;
    private static HashSet subTypeList = null;

    static {
        setDataTypeMap();
        setSuperTypes();
        setSubTypes();
    }

    /**
     * Gets the value of the <b>level</b> property.
     * 
     * @return
     *         possible object is {@link Integer }
     * 
     */
    public int getLevel() {
        if (level == null) {
            return 1;
        } else {
            return level;
        }
    }

    /**
     * Sets the value of the <b>level</b> property.
     * 
     * @param value
     *            allowed object is {@link Integer }
     * 
     */
    public void setLevel(int value) {
        this.level = value;
    }

    /**
     * Returns true if the <b>level</b> property is set; false, otherwise.
     * 
     * @return
     *         returned object is {@link boolean }
     * 
     */

    public boolean isSetLevel() {
        return (this.level != null);
    }

    /**
     * Resets the <b>level</b> property to null.
     * 
     */
    public void unsetLevel() {
        this.level = null;
    }

    /**
     * Gets the value of the inGroup property.
     * 
     * @return
     *         possible object is {@link Boolean }
     * 
     */
    public boolean isInGroup() {
        if (inGroup == null) {
            return false;
        } else {
            return inGroup;
        }
    }

    /**
     * Sets the value of the inGroup property.
     * 
     * @param value
     *            allowed object is {@link Boolean }
     * 
     */
    public void setInGroup(boolean value) {
        this.inGroup = value;
    }

    /**
     * Returns true if the <b>inGroup</b> property is set; false, otherwise.
     * 
     * @return
     *         returned object is {@link boolean }
     * 
     */
    public boolean isSetInGroup() {
        return (this.inGroup != null);
    }

    /**
     * Resets the <b>inGroup</b> property to null.
     * 
     */
    public void unsetInGroup() {
        this.inGroup = null;
    }

    /**
     * Gets the value of the requested property
     * 
     * @param propName
     *            allowed object is {@link String}
     * 
     * @return
     *         returned object is {@link Object}
     * 
     */
    @Override
    public Object get(String propName) {
        if (propName.equals("level")) {
            return getLevel();
        }
        return super.get(propName);
    }

    /**
     * Returns true if the requested property is set; false, otherwise.
     * 
     * @return
     *         returned object is {@link boolean }
     * 
     */
    @Override
    public boolean isSet(String propName) {
        if (propName.equals("level")) {
            return isSetLevel();
        }
        if (propName.equals("inGroup")) {
            return isSetInGroup();
        }
        return super.isSet(propName);
    }

    /**
     * Sets the value of the provided property to the provided value.
     * 
     * @param propName
     *            allowed object is {@link String}
     * @param value
     *            allowed object is {@link Object}
     * 
     */
    @Override
    public void set(String propName, Object value) {
        if (propName.equals("level")) {
            setLevel(((Integer) value));
        }
        if (propName.equals("inGroup")) {
            setInGroup(((Boolean) value));
        }
        super.set(propName, value);
    }

    /**
     * Sets the value of provided property to null.
     * 
     * @param propName
     *            allowed object is {@link String}
     * 
     */
    @Override
    public void unset(String propName) {
        if (propName.equals("level")) {
            unsetLevel();
        }
        if (propName.equals("inGroup")) {
            unsetInGroup();
        }
        super.unset(propName);
    }

    /**
     * Gets the name of this model object, <b>CheckGroupMembershipControl</b>
     * 
     * @return
     *         returned object is {@link String}
     */
    @Override
    public String getTypeName() {
        return "CheckGroupMembershipControl";
    }

    /**
     * Gets a list of all supported properties for this model object, <b>CheckGroupMembershipControl</b>
     * 
     * @param entityTypeName
     *            allowed object is {@link String}
     * 
     * @return
     *         returned object is {@link List}
     */
    public static synchronized List getPropertyNames(String entityTypeName) {
        if (propertyNames != null) {
            return propertyNames;
        } else {
            {
                List names = new ArrayList();
                names.add("level");
                names.add("inGroup");
                names.addAll(Control.getPropertyNames("Control"));
                propertyNames = Collections.unmodifiableList(names);
                return propertyNames;
            }
        }
    }

    private static synchronized void setDataTypeMap() {
        if (dataTypeMap == null) {
            dataTypeMap = new HashMap();
        }
        dataTypeMap.put("level", "Integer");
        dataTypeMap.put("inGroup", "Boolean");
    }

    /**
     * Gets the Java type of the value of the provided property. For example: String, List
     * 
     * @param propName
     *            allowed object is {@link String}
     * 
     * @return
     *         returned object is {@link String}
     */
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
        superTypeList.add("Control");
    }

    /**
     * Gets a list of any model objects which this model object, <b>CheckGroupMembershipControl</b>, is
     * an extension of.
     * 
     * @return
     *         returned object is {@link ArrayList}
     */
    @Override
    public ArrayList getSuperTypes() {
        if (superTypeList == null) {
            setSuperTypes();
        }
        return superTypeList;
    }

    /**
     * Returns a true if the provided model object is one that this
     * model object extends; false, otherwise.
     * 
     * @param superTypeName
     * 
     *            allowed object is {@link String}
     * @return
     *         returned object is {@link boolean}
     */
    @Override
    public boolean isSubType(String superTypeName) {
        return superTypeList.contains(superTypeName);
    }

    private static synchronized void setSubTypes() {
        if (subTypeList == null) {
            subTypeList = new HashSet();
        }
    }

    /**
     * Gets a set of any model objects which extend this model object, <b>CheckGroupMembershipControl</b>
     * 
     * @return
     *         returned object is {@link HashSet}
     */
    public static HashSet getSubTypes() {
        if (subTypeList == null) {
            setSubTypes();
        }
        return subTypeList;
    }

    /**
     * Returns this model object, <b>CheckGroupMembershipControl</b>, and its contents as a String
     * 
     * @return
     *         returned object is {@link String}
     */
    @Override
    public String toString() {
        return WIMTraceHelper.trace(this);
    }

}
