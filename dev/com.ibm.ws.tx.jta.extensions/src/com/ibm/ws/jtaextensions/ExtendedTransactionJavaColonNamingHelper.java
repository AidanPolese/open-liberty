/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.jtaextensions;

import java.util.Collection;
import java.util.Collections;

import javax.naming.InvalidNameException;
import javax.naming.NameClassPair;
import javax.naming.NamingException;

import com.ibm.websphere.jtaextensions.ExtendedJTATransaction;
import com.ibm.ws.container.service.naming.JavaColonNamingHelper;
import com.ibm.ws.container.service.naming.NamingConstants.JavaColonNamespace;
import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.threadContext.ComponentMetaDataAccessorImpl;

public class ExtendedTransactionJavaColonNamingHelper implements JavaColonNamingHelper {

    /**  */
    private static final String EXTENDED_JTA_TRANSACTION = "ExtendedJTATransaction";

    /** {@inheritDoc} */
    @Override
    public Object getObjectInstance(JavaColonNamespace namespace, String name) throws NamingException {
        // Get the ComponentMetaData for the currently active component.
        // There is no comp namespace if there is no active component.
        ComponentMetaData cmd = ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor().getComponentMetaData();
        if (cmd == null) {
            return null;
        }

        if (JavaColonNamespace.COMP_WS.equals(namespace) && EXTENDED_JTA_TRANSACTION.equals(name)) {
            return ExtendedJTATransactionFactory.createExtendedJTATransaction();
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasObjectWithPrefix(JavaColonNamespace namespace, String name) throws NamingException {
        if (name == null) {
            throw new InvalidNameException();
        }
        boolean result = false;
        // Get the ComponentMetaData for the currently active component.
        // There is no comp namespace if there is no active component.
        ComponentMetaData cmd = ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor().getComponentMetaData();
        if (cmd != null) {
            if (namespace == JavaColonNamespace.COMP_WS && name.isEmpty()) {
                result = true;
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<? extends NameClassPair> listInstances(JavaColonNamespace namespace, String nameInContext) {
        // Get the ComponentMetaData for the currently active component.
        // There is no comp namespace if there is no active component.
        ComponentMetaData cmd = ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor().getComponentMetaData();
        if (cmd == null) {
            return Collections.emptyList();
        }

        if (JavaColonNamespace.COMP_WS.equals(namespace) && "".equals(nameInContext)) {
            NameClassPair pair = new NameClassPair(nameInContext, ExtendedJTATransaction.class.getName());
            return Collections.singletonList(pair);
        }

        return Collections.emptyList();
    }
}