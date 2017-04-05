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
package com.ibm.ws.jca.cm;

/**
 * Implemented by JCA managed resources that can be defined by applications.
 * For example, via @DataSourceDefinition, @ConnectionFactoryDefinition and @AdministeredObjectDefinition
 */
public interface AppDefinedResource {
    /**
     * Name of property that identifies the application for application-defined resources.
     */
    static final String APPLICATION = "application";

    /**
     * Name of property that identifies the component for application-defined resources.
     */
    static final String COMPONENT = "component";

    /**
     * Name of property that identifies the module for application-defined resources.
     */
    static final String MODULE = "module";

    /**
     * The common portion of the prefix that we add to unique identifiers for all application-defined resources.
     * Example unique identifiers:
     * application[App1]/dataSource[java:app/env/jdbc/ds1]
     * application[App1]/module[Mod1]/dataSource[java:module/env/jdbc/ds2]
     * application[App1]/module[Mod1]/component[Comp1]/dataSource[java:comp/env/jdbc/ds3]
     */
    static final String PREFIX = APPLICATION + '[';

    /**
     * Returns the name of the application in which the resource is defined.
     * 
     * @return the name of the application in which the resource is defined.
     */
    String getApplication();

    /**
     * Returns the name of the component (if any) in which the resource is defined.
     * 
     * @return the name of the component (if any) in which the resource is defined.
     */
    String getComponent();

    /**
     * Returns the name of the module (if any) in which the resource is defined.
     * 
     * @return the name of the module (if any) in which the resource is defined.
     */
    String getModule();
}
