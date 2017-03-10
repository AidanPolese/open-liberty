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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Representation of the &lt;resolves&gt; XML element in an iFix XML file.
 */
public class Resolves {

    @XmlAttribute
    private final boolean showList = true;
    @XmlAttribute
    private final String description = "This fix resolves APARS:";
    @XmlAttribute
    private int problemCount;
    @XmlElement(name = "problem")
    private List<Problem> problems;

    /**
     * @return the problems in this resolves element or <code>null</code> if there aren't any
     */
    public List<Problem> getProblems() {
        return problems;
    }

    public Resolves() {
        //needed as Jaxb needs a blank constructor
    }

    public Resolves(ArrayList<Problem> problems) {
        this.problems = problems;
        if (problems != null) {
            problemCount = problems.size();
        } else {
            problemCount = 0;
        }
    }

}
