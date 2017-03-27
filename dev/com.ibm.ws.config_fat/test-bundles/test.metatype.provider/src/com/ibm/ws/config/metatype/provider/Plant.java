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
package com.ibm.ws.config.metatype.provider;

import java.util.Dictionary;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 *
 */
@Component(service = { Plant.class, ManagedServiceFactory.class, MetaTypeProvider.class }, immediate = true,
           configurationPolicy = ConfigurationPolicy.IGNORE,
           property = { Constants.SERVICE_VENDOR + "=" + "IBM",
                       Constants.SERVICE_PID + "=" + Plant.PLANT_PID })
public class Plant implements ManagedServiceFactory, MetaTypeProvider {

    public static final String PLANT_PID = "test.metatype.provider.plant";

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.MetaTypeProvider#getLocales()
     */
    @Override
    public String[] getLocales() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.metatype.MetaTypeProvider#getObjectClassDefinition(java.lang.String, java.lang.String)
     */
    @Override
    public ObjectClassDefinition getObjectClassDefinition(String arg0, String arg1) {
        return new PlantObjectClassDefinition();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.ManagedServiceFactory#deleted(java.lang.String)
     */
    @Override
    public void deleted(String arg0) {
        System.out.println("Deleted");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.ManagedServiceFactory#getName()
     */
    @Override
    public String getName() {
        return "Plant MetatypeProvider";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.ManagedServiceFactory#updated(java.lang.String, java.util.Dictionary)
     */
    @Override
    public void updated(String arg0, Dictionary<String, ?> arg1) throws ConfigurationException {
        System.out.println("Updated");

    }

}
