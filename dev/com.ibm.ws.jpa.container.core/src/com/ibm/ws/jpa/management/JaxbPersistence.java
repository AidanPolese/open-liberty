// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009, 2010
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  JaxbPersistence.java
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// F1879-16302
//           WAS80     20091116 tkb      : support 2.0 and 1.0 xml separately
// F743-8155 WAS80     20100305 bkail    : Use SchemaHelper
// d656864   WAS80     20100730 bkail    : Accept schema and package in constructor;
//                                         add setResult
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa.management;

import java.util.List;

/**
 * This interface is an abstraction of <persistence> in persistence.xml. <p>
 * 
 * Get methods are provided for all attributes of <persistence> for all
 * schema versions of persistence.xml (i.e. 1.0 and 2.0, etc.). This allows
 * the client of this interface to be coded (and compiled) independent of the
 * schema version. <p>
 * 
 * A different implementation of this interface will be provided for each schema
 * version of persistence.xml and will wrap the JAXB generated class that
 * represents a <persistence>; generally Persistence. <p>
 * 
 * Get methods on the generated JAXB class which return other JAXB generated
 * classes will instead return either a java primitive or javax.persistence
 * representation of that data or another abstraction interface; allowing the
 * client of this interface to be coded independent of the JAXB implementation. <p>
 **/
abstract class JaxbPersistence
{
    static final ClassLoader svClassLoader = JaxbPersistence20.class.getClassLoader();

    /** Information about the persistence.xml file **/
    final JPAPXml ivPxml;

    /** The package name for the JAXB classes needed by this object. */
    final String ivJAXBPackageName; // d656864

    /** The name of the XSD needed for validation. */
    final String ivXSDName; // d656864

    /**
     * Constructs an instance representing the specified persistence.xml file.
     **/
    JaxbPersistence(JPAPXml pxml, String jaxbPackageName, String xsdName)
    {
        ivPxml = pxml;
        ivJAXBPackageName = jaxbPackageName; // d656864
        ivXSDName = xsdName; // d656864
    }

    /**
     * Sets the root JAXB element object after unmarshalling.
     * 
     * @param result the root JAXB element object
     */
    abstract void setResult(Object result); // d656864

    /**
     * Gets the <persistence-unit>s defined in this <persistence> stanza.
     * 
     * @return <persistence-unit>s defined in this <persistence> stanza.
     */
    abstract List<JaxbPUnit> getPersistenceUnit();

    /**
     * Gets the persistence.xml schema version.
     * 
     * @return persistence.xml schema version.
     */
    abstract String getVersion();

    @Override
    public String toString()
    {
        return (getClass().getSimpleName() + "(" + ivPxml.getRootURL() + ")@" + Integer.toHexString(hashCode()));
    }
}
