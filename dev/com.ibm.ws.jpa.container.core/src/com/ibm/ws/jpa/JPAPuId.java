// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2007, 2008
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  JPAPuId.java
//
// Source File Description:
//
//     An object to identify a persistence unit.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d416151.2 EJB3      20070220 leealber : Initial Release
// d416151.3 EJB3      20070314 leealber : Update toString text.
// d416151.3.9 EJB3    20070504 leealber : hashCode performance improvement.
// d437828   EJB3      20070509 leealber : Guard against pu=null in construction.
// d442457   EJB3      20070529 leealber : Reset puName only if no puName is specified.
// d510184   WAS70     20080505 tkb      : Create seperate EMF for each java:comp
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa;

import java.io.Serializable;

/**
 * An object to identify a persistence unit specification based on where the
 * persistence.xml is defined.
 */
public class JPAPuId implements Serializable
{
    private static final long serialVersionUID = 662612505904938066L;

    private String ivAppName;

    private String ivModJarName;

    private String ivPuName;

    private int ivCurHashCode; // d416151.3.9

    /**
     * Null Constructor, required and used by serialization.
     */
    public JPAPuId()
    {
        // Intentionally left blank
    }

    /**
     * Constructor.
     * Asserts appName, modJarName and puName can NOT be null.
     */
    public JPAPuId(String appName, String modJarName, String puName)
    {
        ivAppName = appName;
        ivModJarName = modJarName;
        ivPuName = puName;
        reComputeHashCode(); // d416151.3.9
    }

    /**
     * Application name getter.
     */
    public final String getApplName()
    {
        return ivAppName;
    }

    /**
     * Module/jar name getter.
     */
    public final String getModJarName()
    {
        return ivModJarName;
    }

    /**
     * Persistence unit name getter.
     */
    public final String getPuName()
    {
        return ivPuName;
    }

    /**
     * Persistence unit name setter.
     */
    public void setPuName(String puName)
    {
        // re-initialize puName only if it has not been set to avoid
        //   overriding valid relative puName defined in annotation/dd.
        if (ivPuName == null || ivPuName.length() == 0) // d442457
        {
            ivPuName = puName;
            reComputeHashCode(); // d416151.3.9
        }
    }

    /**
     * Overloaded equals method using a JPAPuId argument.
     */
    public boolean equals(JPAPuId puId)
    {
        return (ivCurHashCode == puId.ivCurHashCode) // d416151.3.9
               && ivAppName.equals(puId.ivAppName)
               && ivModJarName.equals(puId.ivModJarName)
               && ivPuName.equals(puId.ivPuName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        boolean rtnValue = false;
        if (obj instanceof JPAPuId)
        {
            rtnValue = equals((JPAPuId) obj);
        }
        return rtnValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return ivCurHashCode; // d416151.3.9
    }

    // d416151.3.9 Begins
    /**
     * Compute and cache the current hashCode.
     */
    private void reComputeHashCode()
    {
        ivCurHashCode = (ivAppName != null ? ivAppName.hashCode() : 0) // d437828
                        + (ivModJarName != null ? ivModJarName.hashCode() : 0)
                        + (ivPuName != null ? ivPuName.hashCode() : 0);
    }

    // d416151.3.9 Ends

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "PuId=" + ivAppName + "#" + ivModJarName + "#" + ivPuName;
    }
}
