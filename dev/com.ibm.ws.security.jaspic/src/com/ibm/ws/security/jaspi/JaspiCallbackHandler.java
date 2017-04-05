/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
/**
 * Date     Defect/feature CMVC ID   Description
 * -------- -------------- --------- -----------------------------------------------
 * 04/26/10 F743-25523     leou      Initial version
 * 05/10/10 F743-25523.1   leou      Move Jaspi hooks to WebAuthenticator
 * 05/21/10 653582         leou      Need to add Jaspi cacheKey, uniqueId & groups to Hashtable for custom login
 * 06/15/10 656792         leou      CustomRegistryException when group is null
 * 07/13/10 660497         mcthomps  Fixed handle() method to throw UnsupportedCallbackException
 * 08/11/10 665302         leou      Authorization problem with cache key using JASPI authentication
 */
package com.ibm.ws.security.jaspi;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.callback.PasswordValidationCallback;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.security.CustomRegistryException;
import com.ibm.websphere.security.EntryNotFoundException;
import com.ibm.websphere.security.PasswordCheckFailedException;
import com.ibm.websphere.security.UserRegistry;
import com.ibm.websphere.security.WSSecurityException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.authentication.AuthenticationConstants;
import com.ibm.ws.webcontainer.security.JaspiService;
import com.ibm.wsspi.security.registry.RegistryHelper;
import com.ibm.wsspi.security.token.AttributeNameConstants;

/**
 * @author IBM Corp.
 * 
 */
public class JaspiCallbackHandler implements CallbackHandler {

    private static final TraceComponent tc = Tr.register(JaspiCallbackHandler.class, "Security", null);

    private JaspiService jaspiService;

    JaspiCallbackHandler() {
        super();
    }

    public JaspiCallbackHandler(JaspiService jaspiService) {
        this();
        this.jaspiService = jaspiService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
     */
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        if (callbacks == null || callbacks.length == 0) {
            if (tc.isDebugEnabled())
                Tr.debug(tc, "handle", "No Callbacks received, do nothing.");
            return;
        }
        try {
            for (Callback callback : callbacks) {
                if (callback instanceof CallerPrincipalCallback) {
                    handleCallerPrincipalCallback((CallerPrincipalCallback) callback);
                } else if (callback instanceof GroupPrincipalCallback) {
                    handleGroupPrincipalCallback((GroupPrincipalCallback) callback);
                } else if (callback instanceof PasswordValidationCallback) {
                    handlePasswordValidationCallback((PasswordValidationCallback) callback);
                } else {
                    throw new UnsupportedCallbackException(callback);
                }
            }
        } catch (UnsupportedCallbackException e) {
            FFDCFilter.processException(e, getClass().getName() + ".handle", "97");
            throw e;
        } catch (Exception t) {
            FFDCFilter.processException(t, getClass().getName() + ".handle", "100");
            throw new IOException(t);
        }
    }

    /*
     * Callback for PasswordValidation. This callback may be used by an authentication module to employ the password
     * validation facilities of its containing runtime. This Callback would typically be called by a ServerAuthModule
     * during validateRequest processing.
     * 
     * Verify user/password in active user registry. If password is valid, set Callback result = true and add to clientSubject a JaspiCustomCredential
     * with custom data that is used later by JaspiAuthenticator to create a WAS subject.
     */
    protected void handlePasswordValidationCallback(PasswordValidationCallback callback)
                    throws RemoteException, EntryNotFoundException, CustomRegistryException {

        Subject clientSubject = callback.getSubject();
        String userName = callback.getUsername();
        String password = new String(callback.getPassword());
        if (tc.isDebugEnabled())
            Tr.debug(tc, "handlePasswordValidationCallback", new Object[] { callback, userName });
        if (clientSubject != null) {
            UserRegistry registry = getUserRegistry();
            if (registry != null) {
                if (checkUserPassword(userName,
                                      password,
                                      registry,
                                      registry.getRealm(),
                                      clientSubject)) {
                    callback.setResult(true);
                } else {
                    callback.setResult(false);
                }
            }
        }
        if (tc.isDebugEnabled())
            Tr.debug(tc, "handlePasswordValidationCallback", "valid password? " + callback.getResult());
    }

    /*
     * Callback for setting the container's caller (or Remote user) principal. This callback is intended to be called by
     * a serverAuthModule during its validateRequest processing.
     */
    protected void handleCallerPrincipalCallback(CallerPrincipalCallback callback) throws WSSecurityException {

        Subject clientSubject = callback.getSubject();
        String userName = callback.getName();
        Principal userPrincipal = callback.getPrincipal();
        if (tc.isDebugEnabled())
            Tr.debug(tc, "handleCallerPrincipalCallback", new Object[] { "user=" + userName, "principal=" + userPrincipal });
        /*
         * getName() javadoc:
         * "When the values returned by this method and the getPrincipal methods are null, the handler must establish the
         * container's representation of the unauthenticated caller principal within the Subject."
         * 
         * In CallerPrincipalCallback(Subject s, String n) ctor:
         * "When the String argument n is null, the handler will establish the container's representation of the unauthenticated caller principal
         * (which may or may not be equal to null, depending on the requirements of the container type). When the container type requires that
         * a non-null principal be established as the caller principal, the value obtained by calling getName on the principal may not match the
         * argument value."
         * 
         * getPrincipal() javadoc:
         * "When the values returned by this method and the getName methods are null, the handler must establish the container's
         * representation of the unauthenticated caller principal within the Subject."
         * 
         * In CallerPrincipalCallback(Subject s, Principal p) ctor:
         * The CallbackHandler must establish the argument Principal as the caller principal associated with the invocation being processed by the
         * container. When the argument Principal is null, the handler will establish the container's representation of the unauthenticated caller
         * principal.
         */
        Hashtable<String, Object> credData = null;
        if (clientSubject != null) {
            credData = getSubjectCustomData(clientSubject);
            String securityName = null;
            if (userName == null && userPrincipal == null) {
                securityName = JaspiServiceImpl.UNAUTHENTICATED_ID;
                credData.put(AttributeNameConstants.WSCREDENTIAL_SECURITYNAME, securityName);
            } else if (userPrincipal != null) {
                securityName = userPrincipal.getName();
                addUseridAndGroupsKeys(securityName, credData);
                credData.put("com.ibm.wsspi.security.cred.jaspi.principal", userPrincipal);
            } else {
                securityName = userName;
                addUseridAndGroupsKeys(securityName, credData);
            }
            if (tc.isDebugEnabled())
                Tr.debug(tc, "Added securityName: " + securityName);
        }
        if (tc.isDebugEnabled())
            Tr.debug(tc, "handleCallerPrincipalCallback", credData);
    }

    /*
     * Callback establishing group principals within the argument subject. This callback is intended to be called by
     * a serverAuthModule during its validateRequest processing.
     */
    protected void handleGroupPrincipalCallback(GroupPrincipalCallback callback)
                    throws CustomRegistryException, EntryNotFoundException, RemoteException {
        Subject clientSubject = callback.getSubject();
        Hashtable<String, Object> credData = null;
        if (clientSubject != null) {
            String[] groups = callback.getGroups();
            if (groups != null && groups.length > 0) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Group names in Callback: ", Arrays.asList(groups));
                credData = getSubjectCustomData(clientSubject);
                List<String> groupNames = (List<String>) credData.get(AttributeNameConstants.WSCREDENTIAL_GROUPS);
                if (groupNames == null) {
                    groupNames = new ArrayList<String>();
                    credData.put(AttributeNameConstants.WSCREDENTIAL_GROUPS, groupNames);
                }
                UserRegistry registry = getUserRegistry();
                for (int i = 0; i < groups.length; i++) {
                    String group = groups[i];
                    if (group == null || group.isEmpty()) {
                        if (tc.isDebugEnabled())
                            Tr.debug(tc, "Group is null or an empty string, it has been ignored.");
                        continue;
                    }
                    String groupName = null;
                    if (registry != null && registry.isValidGroup(group)) {
                        groupName = registry.getUniqueGroupId(group);
                    } else {
                        groupName = group;
                    }
                    if (!groupNames.contains(groupName)) {
                        groupNames.add(groupName);
                        if (tc.isDebugEnabled())
                            Tr.debug(tc, "Added groupId: " + groupName);
                    } else {
                        if (tc.isDebugEnabled())
                            Tr.debug(tc, groupName + " already exists in custom credential data, avoid duplicates.");
                    }
                }
            } else {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Callback has no groups.");
            }
        }
        if (tc.isDebugEnabled())
            Tr.debug(tc, "handleGroupPrincipalCallback", credData);
    }

    protected void addUseridAndGroupsKeys(String securityName,
                                          Hashtable<String,
                                          Object> credData) {
        try {
            UserRegistry registry = getUserRegistry();
            if (registry != null && registry.isValidUser(securityName)) {
                credData.put(AttributeNameConstants.WSCREDENTIAL_USERID, securityName);
                credData.put(AuthenticationConstants.INTERNAL_ASSERTION_KEY, Boolean.TRUE);
                List<?> groups = registry.getUniqueGroupIds(securityName);
                credData.put(AttributeNameConstants.WSCREDENTIAL_GROUPS, groups);
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Added userid: " + securityName + "  and groups: " + groups);
            }
            else {
                if (registry == null)
                    credData.put(AttributeNameConstants.WSCREDENTIAL_UNIQUEID, "user:defaultRealm/" + securityName);
                else
                    credData.put(AttributeNameConstants.WSCREDENTIAL_UNIQUEID, registry.getRealm() + "/" + securityName);
                credData.put(AttributeNameConstants.WSCREDENTIAL_SECURITYNAME, securityName);
            }
        } catch (Exception e) {
            FFDCFilter.processException(e, getClass().getName() + ".handleCallerPrincipalCallback", "245");
        }
    }

    @FFDCIgnore({ com.ibm.websphere.security.PasswordCheckFailedException.class })
    protected boolean checkUserPassword(String user, @Sensitive String password, UserRegistry registry, String realmName, Subject clientSubject)
                    throws EntryNotFoundException, CustomRegistryException, RemoteException {
        String userSecurityName;
        try {
            userSecurityName = registry.checkPassword(user, password);
            List<String> groupNames = registry.getGroupsForUser(userSecurityName);
            List<String> uniqueGroups = new ArrayList<String>();
            if (groupNames != null)
                for (String group : groupNames) {
                    uniqueGroups.add(registry.getUniqueGroupId(group));
                }
            newCustomCredential(clientSubject, realmName, userSecurityName, uniqueGroups);
        } catch (PasswordCheckFailedException e) {
            if (tc.isDebugEnabled())
                Tr.debug(tc, "checkUserPassword - password is not valid.");
            return false;
        } catch (Exception e) {
            if (tc.isDebugEnabled())
                Tr.debug(tc, "checkUserPassword - registry exception: " + e);
            return false;
        }
        return true;
    }

    protected Hashtable<String, Object> newCustomCredential(Subject clientSubject, String realmName, String securityName, List<?> groupList) {
        Hashtable<String, Object> credData = getSubjectCustomData(clientSubject);
        credData.put(AttributeNameConstants.WSCREDENTIAL_REALM, realmName);
        credData.put(AttributeNameConstants.WSCREDENTIAL_USERID, securityName);
        credData.put(AuthenticationConstants.INTERNAL_ASSERTION_KEY, Boolean.TRUE);
        if (groupList != null && !groupList.isEmpty()) {
            if (tc.isDebugEnabled())
                Tr.debug(tc, "Adding groups found in registry", groupList);
            credData.put(AttributeNameConstants.WSCREDENTIAL_GROUPS, groupList);
        } else {
            credData.put(AttributeNameConstants.WSCREDENTIAL_GROUPS, new ArrayList<String>());
        }
        return credData;
    }

    protected Hashtable<String, Object> getSubjectCustomData(final Subject clientSubject) {
        Hashtable<String, Object> cred = jaspiService.getCustomCredentials(clientSubject);
        if (cred == null) {
            PrivilegedAction<Hashtable<String, Object>> action = new PrivilegedAction<Hashtable<String, Object>>() {

                @Override
                public Hashtable<String, Object> run() {
                    Hashtable<String, Object> newCred = new Hashtable<String, Object>();
                    clientSubject.getPrivateCredentials().add(newCred);
                    return newCred;
                }
            };
            cred = AccessController.doPrivileged(action);
        }
        return cred;
    }

    /**
     * @return
     */
    UserRegistry getUserRegistry() {
        UserRegistry registry = null;
        try {
            registry = RegistryHelper.getUserRegistry(null);
        } catch (WSSecurityException e) {
            // TODO message i guess?
            if (tc.isDebugEnabled())
                Tr.debug(tc, "Internal error getting the user registry", e);
        }
        return registry;
    }

}
