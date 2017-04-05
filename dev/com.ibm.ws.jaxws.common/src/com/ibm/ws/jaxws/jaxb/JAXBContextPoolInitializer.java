/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.jaxb;

import org.apache.cxf.Bus;
import org.apache.cxf.databinding.DataBinding;
import org.apache.cxf.jaxb.JAXBDataBinding;

import com.ibm.ws.jaxws.bus.LibertyApplicationBusListener;

/**
 * This class will register the customized JAXBDataBinding in the bus instances, which will enable JAXBContext instance pool
 */
public class JAXBContextPoolInitializer implements LibertyApplicationBusListener {

    @Override
    public void preInit(Bus bus) {
        bus.setProperty(DataBinding.class.getName(), JAXBDataBinding.class);
    }

    @Override
    public void initComplete(Bus bus) {}

    @Override
    public void preShutdown(Bus bus) {}

    @Override
    public void postShutdown(Bus bus) {}

}
