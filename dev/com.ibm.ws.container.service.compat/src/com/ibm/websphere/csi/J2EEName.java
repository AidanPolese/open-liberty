// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// @(#) 1.4 SERV1/ws/code/runtime.fw/src/com/ibm/websphere/csi/J2EEName.java, WAS.runtime.fw, WAS80.SERV1, h1116.09 4/21/06 10:24:06 [4/23/11 20:12:41]
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2000, 2012
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  J2EEName.java
//
// Source File Description:
//
//     J2EEName instances are used to encapsulate the
//     Application-Module-Component name for uniquely identifying EJBs in an
//     application.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d118829   ASV50     20010823 tkb      : New copyright/prologue
// D363517.1 6.1       20060421 ericvn   : Add to websphere-apis.jar   
// F71191    WAS90     20120723 daboshe  : Add javadoc and move to Liberty
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.websphere.csi;

/**
 * J2EEName instances are used to encapsulate the Application-Module-Component name for
 * uniquely identifying EJBs in an application. An object implementing this interface must
 * also correctly implement the hashCode and equals methods.
 * 
 * @ibm-private-in-use
 */
public interface J2EEName extends java.io.Serializable {

    /**
     * Returns a J2EEName in the format app#mod#comp for a component,
     * app#mod for a module, or app for an application.
     */
    @Override
    public String toString();

    /**
     * Returns the application name.
     * 
     * @return application name
     */
    public String getApplication();

    /**
     * Returns the module name, or null for J2EENames representing applications.
     * 
     * @return module name, or null
     */
    public String getModule();

    /**
     * Returns the component name, or null for J2EENames representing
     * applications or modules.
     * 
     * @return component name, or null
     */
    public String getComponent();

    /**
     * Returns a serialized name that can be passed in to {@link J2EENameFactory#create(byte[]) to create the name.
     */
    public byte[] getBytes();

}
