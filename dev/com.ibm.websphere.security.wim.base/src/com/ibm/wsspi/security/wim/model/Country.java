package com.ibm.wsspi.security.wim.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.ibm.websphere.security.wim.ras.WIMTraceHelper;

/**
 * <p>Java class for Country complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Country">
 * &lt;complexContent>
 * &lt;extension base="{http://www.ibm.com/websphere/wim}GeographicLocation">
 * &lt;sequence>
 * &lt;element ref="{http://www.ibm.com/websphere/wim}c" minOccurs="0"/>
 * &lt;element ref="{http://www.ibm.com/websphere/wim}countryName" minOccurs="0"/>
 * &lt;element ref="{http://www.ibm.com/websphere/wim}description" maxOccurs="unbounded" minOccurs="0"/>
 * &lt;/sequence>
 * &lt;/extension>
 * &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * <p> The Country object extends the GeographicLocation object, and represents information related to a country.
 * It contains several properties: <b>c</b>, <b>countryName</b> and <b>description</b>.
 * 
 * <ul>
 * <li><b>c</b>: short form for the <b>countryName</b> property.</li>
 * <li><b>countryName</b>: defines the name of the country.</li>
 * <li><b>description</b>: describes this object.</li>
 * </ul>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Country", propOrder = {
                                        "c",
                                        "countryName",
                                        "description"
})
public class Country
                extends GeographicLocation
{

    protected String c;
    protected String countryName;
    protected List<String> description;
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
     * Gets the value of the <b>c</b> property.
     * 
     * @return
     *         possible object is {@link String }
     * 
     */
    public String getC() {
        return c;
    }

    /**
     * Sets the value of the <b>c</b> property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setC(String value) {
        this.c = value;
    }

    /**
     * Returns true if the <b>c</b> property is set; false, otherwise.
     * 
     * @return
     *         returned object is {@link boolean }
     * 
     */
    public boolean isSetC() {
        return (this.c != null);
    }

    /**
     * Gets the value of the <b>countryName</b> property.
     * 
     * @return
     *         possible object is {@link String }
     * 
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Sets the value of the <b>countryName</b> property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setCountryName(String value) {
        this.countryName = value;
    }

    /**
     * Returns true if the <b>countryName</b> property is set; false, otherwise.
     * 
     * @return
     *         returned object is {@link boolean }
     * 
     */
    public boolean isSetCountryName() {
        return (this.countryName != null);
    }

    /**
     * Gets the value of the <b>description</b> property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the description property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     * getDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link String }
     * 
     * 
     */
    public List<String> getDescription() {
        if (description == null) {
            description = new ArrayList<String>();
        }
        return this.description;
    }

    /**
     * Returns true if the <b>description</b> property is set; false, otherwise.
     * 
     * @return
     *         returned object is {@link boolean }
     * 
     */
    public boolean isSetDescription() {
        return ((this.description != null) && (!this.description.isEmpty()));
    }

    /**
     * Resets the <b>description</b> property to null.
     * 
     */
    public void unsetDescription() {
        this.description = null;
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
        if (propName.equals("c")) {
            return getC();
        }
        if (propName.equals("countryName")) {
            return getCountryName();
        }
        if (propName.equals("description")) {
            return getDescription();
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
        if (propName.equals("c")) {
            return isSetC();
        }
        if (propName.equals("countryName")) {
            return isSetCountryName();
        }
        if (propName.equals("description")) {
            return isSetDescription();
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
        if (propName.equals("c")) {
            setC(((String) value));
        }
        if (propName.equals("countryName")) {
            setCountryName(((String) value));
        }
        if (propName.equals("description")) {
            getDescription().add(((String) value));
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
        if (propName.equals("description")) {
            unsetDescription();
        }
        super.unset(propName);
    }

    /**
     * Gets the name of this model object, <b>Country</b>
     * 
     * @return
     *         returned object is {@link String}
     */
    @Override
    public String getTypeName() {
        return "Country";
    }

    /**
     * Gets a list of all supported properties for this model object, <b>Country</b>
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
                names.add("c");
                names.add("countryName");
                names.add("description");
                names.addAll(GeographicLocation.getPropertyNames("GeographicLocation"));
                propertyNames = Collections.unmodifiableList(names);
                return propertyNames;
            }
        }
    }

    private static synchronized void setDataTypeMap() {
        if (dataTypeMap == null) {
            dataTypeMap = new HashMap();
        }
        dataTypeMap.put("c", "String");
        dataTypeMap.put("countryName", "String");
        dataTypeMap.put("description", "String");
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
        superTypeList.add("GeographicLocation");
        superTypeList.add("Entity");
    }

    /**
     * Gets a list of any model objects which this model object, <b>Country</b>, is
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
     * Gets a set of any model objects which extend this model object, <b>Country</b>
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
     * Returns this model object, <b>Country</b>, and its contents as a String
     * 
     * @return
     *         returned object is {@link String}
     */

    @Override
    public String toString() {
        return WIMTraceHelper.trace(this);
    }

}
