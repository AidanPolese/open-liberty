// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %Z% %I% %W% %G% %U% [%H% %T%]
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2000, 2012
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  J2EENameFactory.java
//
// Source File Description:
//
//     J2EENameFactory is used to create J2EEName instances from a byte array.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// f118829   ASV50     20020307 tkb      : New copyright/prologue
// D363517.1 6.1       20060421 ericvn   : Add to websphere-apis.jar   
// F71191    WAS90     20120723 daboshe  : Add javadoc      
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.websphere.csi;

/**
 * J2EENameFactory is used to create J2EEName instances from a byte array.
 * 
 * @ibm-private-in-use
 */
public interface J2EENameFactory {

    /**
     * Creates a J2EEName from a serialized name using {@link J2EEName#getBytes()}.
     */
    public J2EEName create(byte[] bytes);

    /**
     * Creates a J2EEName using the application, module,
     * and component name.
     * 
     * @param app the application name
     * @param module the module name, or null for an application J2EEName
     * @param component the component name, or null for an application or
     *            module J2EEName
     */
    public J2EEName create(String app, String module, String component);

}
