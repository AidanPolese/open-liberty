/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.product.utility.extension.ifix.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a representation of an iFix XML file on disk
 */
@XmlRootElement(name = "fix")
public class IFixInfo implements MetadataOutput {

    private final String aparIdPrefix = "com.ibm.ws.apar.";

    //XmlAttribute - defined on setter below
    private String id;

    @XmlAttribute
    private String version;

    @XmlElement
    private Applicability applicability;

    @XmlElement
    private final Categories categories = new Categories();//unused for now but we need it in xml as a self-closing element

    @XmlElement
    private Information information;

    @XmlElement(name = "property")
    private List<Property> properties;

    @XmlElement
    private Resolves resolves;

    @XmlElement
    private Updates updates;

    public IFixInfo() {
        //needed as Jaxb needs a blank constructor
    }

    /**
     * 
     * @param setId
     * @param setVersion
     * @param aparList
     * @param fixDescription
     * @param offerings
     * @param properties
     * @param includedJars
     */
    public IFixInfo(String setId, String setVersion, Set<String> aparList,
                    String fixDescription, ArrayList<Offering> offerings, List<Property> properties,
                    Set<UpdatedFile> updatedFiles) {
        //set id
        id = setId;
        //set version
        version = setVersion;
        //set applicability
        applicability = new Applicability(offerings);
        information = new Information(setId, setVersion, fixDescription);
        this.properties = properties;

        //create resolves list
        ArrayList<Problem> problems = new ArrayList<Problem>();
        if (aparList != null) {
            for (String apar : aparList) {
                String aparId = apar.toString();
                Problem myProblem = new Problem(aparIdPrefix + aparId, aparId, aparId);
                problems.add(myProblem);
            }
        }
        resolves = new Resolves(problems);

        updates = new Updates(updatedFiles);
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * @param id the id to set
     */
    @XmlAttribute
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the resolves element from the XML or <code>null</code> if there isn't one
     */
    public Resolves getResolves() {
        return resolves;
    }

    /**
     * @return the updates from the XML or <code>null</code> if there isn't one
     */
    public Updates getUpdates() {
        return updates;
    }

    /**
     * @return the applicability element from the XML or <code>null</code> if there isn't one
     */
    public Applicability getApplicability() {
        return applicability;
    }

    /**
     * @return the properties element from the XML or <code>null</code> if there isn't one
     */
    public List<Property> getProperties() {
        return properties;
    }
}
