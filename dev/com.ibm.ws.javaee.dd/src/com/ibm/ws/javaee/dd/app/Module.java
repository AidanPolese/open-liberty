// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2011
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// F46946    WAS85     20110712 bkail    : New
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.javaee.dd.app;

/**
 *
 */
public interface Module {

    /**
     * Represents &lt;connector> for {@link #getModuleType}.
     */
    int TYPE_CONNECTOR = 0;

    /**
     * Represents &lt;ejb> for {@link #getModuleType}.
     */
    int TYPE_EJB = 1;

    /**
     * Represents &lt;java> for {@link #getModuleType}.
     */
    int TYPE_JAVA = 2;

    /**
     * Represents &lt;web> for {@link #getModuleType}.
     */
    int TYPE_WEB = 3;

    /**
     * @return the type of module
     *         <ul>
     *         <li>{@link #TYPE_CONNECTOR} - connector
     *         <li>{@link #TYPE_EJB} - ejb
     *         <li>{@link #TYPE_JAVA} - java
     *         <li>{@link #TYPE_WEB} - web
     *         </ul>
     */
    int getModuleType();

    /**
     * @return the path of module, or &lt;web-uri> when TYPE_WEB.
     */
    String getModulePath();

    /**
     * @return &lt;web>&lt;context-root> when TYPE_WEB.
     */
    String getContextRoot();

    /**
     * @return &lt;alt-dd>, or null if unspecified
     */
    String getAltDD();
}
