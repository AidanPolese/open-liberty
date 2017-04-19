/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.security.internal.jacc;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EnterpriseBean;
import javax.security.auth.Subject;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ejbcontainer.EJBComponentMetaData;
import com.ibm.ws.ejbcontainer.EJBMethodMetaData;
import com.ibm.ws.ejbcontainer.EJBRequestData;
import com.ibm.ws.ejbcontainer.security.internal.EJBAccessDeniedException;
import com.ibm.ws.ejbcontainer.security.internal.EJBAuthorizationHelper;
import com.ibm.ws.ejbcontainer.security.internal.TraceConstants;
import com.ibm.ws.security.authentication.principals.WSPrincipal;
import com.ibm.ws.security.authorization.jacc.JaccService;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

/**
 * Encapsulate jacc related methods which are consumed by EJBSecurityCollaborator.
 */
public class EJBJaccAuthorizationHelper implements EJBAuthorizationHelper {
    private static final TraceComponent tc = Tr.register(EJBJaccAuthorizationHelper.class);

    private AtomicServiceReference<JaccService> jaccServiceRef = null;

    public EJBJaccAuthorizationHelper(AtomicServiceReference<JaccService> jaccServiceRef) {
        this.jaccServiceRef = jaccServiceRef;
    }

    /**
     * Authorizes the subject to call the given EJB by using JACC, based on the given method info.
     * If the subject is not authorized, an exception is thrown. The following checks are made:
     * <li>is the bean method excluded (denyAll)</li>
     * <li>are the required roles null or empty</li>
     * <li>is EVERYONE granted to any of the required roles</li>
     * <li>is the subject authorized to any of the required roles</li>
     * 
     * @param methodMetaData the info on the EJB method to call
     * @param subject the subject authorize
     * @throws EJBAccessDeniedException when the subject is not authorized to the EJB
     */
    @Override
    public void authorizeEJB(EJBRequestData request, Subject subject) throws EJBAccessDeniedException {
        EJBMethodMetaData methodMetaData = request.getEJBMethodMetaData();
        Object[] methodArguments = request.getMethodArguments();
        String applicationName = methodMetaData.getEJBComponentMetaData().getJ2EEName().getApplication();
        String moduleName = methodMetaData.getEJBComponentMetaData().getJ2EEName().getModule();
        String methodName = methodMetaData.getMethodName();
        String methodInterface = methodMetaData.getEJBMethodInterface().specName();
        String methodSignature = methodMetaData.getMethodSignature();
        String beanName = methodMetaData.getEJBComponentMetaData().getJ2EEName().getComponent();
        List<Object> methodParameters = null;

        Object bean = request.getBeanInstance();
        EnterpriseBean ejb = null;
        if (bean instanceof EnterpriseBean) {
            ejb = (EnterpriseBean) bean;
        }

        if (methodArguments != null && methodArguments.length > 0) {
            methodParameters = Arrays.asList(methodArguments);
        }

        boolean isAuthorized = jaccServiceRef.getService().isAuthorized(applicationName, moduleName, beanName, methodName, methodInterface, methodSignature, methodParameters, ejb,
                                                                        subject);

        if (!isAuthorized) {
            String authzUserName = subject.getPrincipals(WSPrincipal.class).iterator().next().getName();
            Tr.audit(tc, "EJB_JACC_AUTHZ_FAILED", authzUserName, methodName, applicationName);
            throw new EJBAccessDeniedException(TraceNLS.getFormattedMessage(this.getClass(),
                                                                            TraceConstants.MESSAGE_BUNDLE,
                                                                            "EJB_JACC_AUTHZ_FAILED",
                                                                            new Object[] { authzUserName, methodName, applicationName },
                                                                            "CWWKS9406A: Authorization by the JACC provider failed. The user is not granted access to any of the required roles."));
        }
    }

    @Override
    public boolean isCallerInRole(EJBComponentMetaData cmd, EJBRequestData request, String roleName, String roleLink, Subject subject) {
        // roleLink is not used.
        String applicationName = cmd.getJ2EEName().getApplication();
        String moduleName = cmd.getJ2EEName().getModule();
        String beanName = cmd.getJ2EEName().getComponent();
        String methodName = request.getEJBMethodMetaData().getMethodName();
        Object[] methodArguments = request.getMethodArguments();
        List<Object> methodParameters = null;
        if (methodArguments != null && methodArguments.length > 0) {
            methodParameters = Arrays.asList(methodArguments);
        }
        EnterpriseBean bean = null;
        if (request.getBeanInstance() instanceof EnterpriseBean) {
            bean = (EnterpriseBean) request.getBeanInstance();
        }
        return jaccServiceRef.getService().isSubjectInRole(applicationName, moduleName, beanName, methodName, methodParameters, roleName, bean, subject);
    }

}
