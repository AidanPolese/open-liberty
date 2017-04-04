/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.app.manager.ejb.internal;

import java.util.concurrent.Future;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.app.manager.module.DeployedAppInfo;
import com.ibm.ws.app.manager.module.DeployedAppInfoFactory;
import com.ibm.ws.app.manager.module.DeployedAppInfoFailure;
import com.ibm.ws.app.manager.module.internal.DeployedAppInfoBase;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;
import com.ibm.ws.threading.FutureMonitor;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.application.handler.ApplicationHandler;
import com.ibm.wsspi.application.handler.ApplicationInformation;
import com.ibm.wsspi.application.handler.ApplicationMonitoringInformation;

@Component(service = ApplicationHandler.class,
           property = { "service.vendor=IBM", "type:String=ejb" })
public class EJBApplicationHandlerImpl implements ApplicationHandler<DeployedAppInfo> {
    private FutureMonitor futureMonitor;
    private DeployedAppInfoFactory deployedAppFactory;

    @Reference
    protected void setFutureMonitor(FutureMonitor fm) {
        futureMonitor = fm;
    }

    @Reference(target = "(type=ejb)")
    protected void setDeployedAppFactory(DeployedAppInfoFactory factory) {
        deployedAppFactory = factory;
    }

    @Override
    public ApplicationMonitoringInformation setUpApplicationMonitoring(ApplicationInformation<DeployedAppInfo> applicationInformation) {
        /*
         * ApplicationMonitor.addApplication() - "If the handler didn't give us any information about what to monitor then monitor the whole application"
         * Returning an empty collection tells it to monitor nothing besides root, returning null uses default monitoring
         */
        return null;
    }

    @Override
    public Future<Boolean> install(ApplicationInformation<DeployedAppInfo> applicationInformation) {
        final Future<Boolean> result = futureMonitor.createFuture(Boolean.class);

        DeployedAppInfoBase deployedApp;
        try {
            deployedApp = (DeployedAppInfoBase) deployedAppFactory.createDeployedAppInfo(applicationInformation);
        } catch (UnableToAdaptException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof ParseException) {
                futureMonitor.setResult(result, new DeployedAppInfoFailure(cause.getMessage(), cause));
            } else {
                futureMonitor.setResult(result, ex);
            }
            return result;
        }

        if (!deployedApp.deployApp(result)) {
            futureMonitor.setResult(result, false);
            return result;
        }

        return result;
    }

    @Override
    public Future<Boolean> uninstall(ApplicationInformation<DeployedAppInfo> applicationInformation) {
        DeployedAppInfoBase deployedApp = (DeployedAppInfoBase) applicationInformation.getHandlerInfo();
        if (deployedApp == null) {
            // Somebody asked us to remove an app we don't know about
            return futureMonitor.createFutureWithResult(false);
        }

        boolean success = deployedApp.uninstallApp();
        return futureMonitor.createFutureWithResult(success);
    }
}
