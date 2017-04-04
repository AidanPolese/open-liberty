/*
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 *
 * Change activity:
 *
 * Reason          Date      Origin   Description
 * --------------- ------    -------- --------------------------------------------
 * 89424           121212 chetbhat Simplifying metatype.xml for messaging 
 * 92566           05/02/13  Kavitha PMI code removal                                
 * ============================================================================
 */

package com.ibm.ws.sib.admin;

/**
 * @author philip
 * 
 *         To change this generated comment edit the template variable "typecomment":
 *         Window>Preferences>Java>Templates.
 *         To enable and disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public interface JsMain {

    public void initialize(JsMEConfig config) throws Exception;

    public void start() throws Exception;

    public void destroy() throws Exception;

    public void createDestinationLocalization(BaseDestination config) throws Exception;

    public void alterDestinationLocalization(BaseDestination config) throws Exception;

    public void deleteDestinationLocalization(BaseDestination config) throws Exception;

    public void reloadEngine(long highMessageThreshold) throws Exception;

}
