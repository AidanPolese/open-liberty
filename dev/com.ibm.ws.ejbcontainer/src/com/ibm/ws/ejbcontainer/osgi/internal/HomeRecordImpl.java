/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal;

import com.ibm.ejs.container.BeanMetaData;
import com.ibm.ejs.container.HomeOfHomes;
import com.ibm.ejs.container.HomeRecord;

public class HomeRecordImpl extends HomeRecord {
    public static HomeRecordImpl cast(HomeRecord hr) {
        return (HomeRecordImpl) hr;
    }

    public final String systemHomeBindingName;
    public Object remoteBindingData;

    public HomeRecordImpl(BeanMetaData bmd, HomeOfHomes homeOfHomes, String systemHomeBindingName) {
        super(bmd, homeOfHomes);
        this.systemHomeBindingName = systemHomeBindingName;
    }
}
