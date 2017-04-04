/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.naming;

/**
 *
 */
public class RemoteObjectInstanceEnumImpl implements RemoteObjectInstance {
    private static final long serialVersionUID = -6704870700191348746L;

    @SuppressWarnings("rawtypes")
    final Class<Enum> clazz;
    final String name;

    public RemoteObjectInstanceEnumImpl(@SuppressWarnings("rawtypes") Class<Enum> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.clientcontainer.remote.common.object.RemoteObjectInstance#getObject(com.ibm.ws.serialization.SerializationService)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object getObject() throws RemoteObjectInstanceException {
        return Enum.valueOf(clazz, name);
    }

}
