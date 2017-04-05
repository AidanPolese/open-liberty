/*
 * @start_prolog@
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 * 
 * Change activity:
 * 
 * Issue       Date        Name        Description
 * ----------- ----------- --------    ------------------------------------
 */
package com.ibm.wsspi.application.handler;

import java.util.concurrent.Future;

import com.ibm.wsspi.adaptable.module.InterpretedContainer;

/**
 * @param <T> The type of handler info object that is created by the application handler
 */
public interface ApplicationHandler<T> {

    public Future<Boolean> install(ApplicationInformation<T> applicationInformation);

    public Future<Boolean> uninstall(ApplicationInformation<T> applicationInformation);

    /**
     * <p>
     * This method will be called before {@link #install(ApplicationInformation)} and gives the handler the opportunity to modify what paths will be monitored for updates on a
     * particular application. The container passed in through the {@link ApplicationInformation#getContainer()} method will have the whole application structure as it is at
     * install time but it is possible to monitor paths within it that do not exist yet.
     * </p>
     * <p>
     * If the container is adapted into a new container (such as an {@link InterpretedContainer}) it is possible to set this new container onto the {@link ApplicationInformation}
     * object and the framework will use this version for future operations including calls to {@link #install(ApplicationInformation)} to prevent the need to adapt it twice into a
     * different instance. As well as this any data object that is created by this method can be set on the {@link ApplicationInformation#setData(Object)} method to make it
     * available to the {@link #install(ApplicationInformation)} method without having to create it twice.
     * </p>
     * 
     * @param applicationInformation Information about the application that needs to be monitored
     * @return The information about what to monitor or <code>null</code> if the default monitoring can be used
     */
    public ApplicationMonitoringInformation setUpApplicationMonitoring(ApplicationInformation<T> applicationInformation);

}