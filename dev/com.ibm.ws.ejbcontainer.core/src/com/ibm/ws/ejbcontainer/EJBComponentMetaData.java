/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer;

import java.util.List;

import com.ibm.ws.runtime.metadata.ComponentMetaData;

public interface EJBComponentMetaData
                extends ComponentMetaData
{
    /**
     * @return the EJB type
     */
    EJBType getEJBType();

    /**
     * @return the EJB implementation class name
     */
    String getBeanClassName();

    /**
     * @return true if this is a reentrant entity bean
     */
    boolean isReentrant();

    /**
     * @param type the interface type
     * @return the list of methods for the interface type, or null if the EJB has
     *         no interfaces of that type
     */
    List<EJBMethodMetaData> getEJBMethodMetaData(EJBMethodInterface type);
}
