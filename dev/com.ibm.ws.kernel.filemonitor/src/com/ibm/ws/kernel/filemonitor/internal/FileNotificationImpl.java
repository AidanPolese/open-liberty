/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.filemonitor.internal;

import java.util.Collection;

import javax.management.DynamicMBean;
import javax.management.StandardMBean;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.kernel.filemonitor.FileNotification;
import com.ibm.ws.kernel.filemonitor.FileNotificationMBean;

@Component(configurationPolicy = ConfigurationPolicy.IGNORE,
           service = DynamicMBean.class,
           immediate = true,
           property = { "service.vendor=IBM", "jmx.objectname=WebSphere:service=com.ibm.ws.kernel.filemonitor.FileNotificationMBean" })
public class FileNotificationImpl extends StandardMBean implements FileNotificationMBean, com.ibm.websphere.filemonitor.FileNotificationMBean {

    /** required injected service */
    FileNotification notificationDelegate;

    public FileNotificationImpl() {
        super(FileNotificationMBean.class, false);
    }

    @Reference
    protected void setNotificationDelegate(FileNotification notificationDelegate) {
        this.notificationDelegate = notificationDelegate;
    }

    /** service uninjection method */
    protected void unsetNotificationDelegate(FileNotification notificationDelegate) {
        if (this.notificationDelegate == notificationDelegate)
            this.notificationDelegate = null;
    }

    /** {@inheritDoc} */
    @Override
    public void notifyFileChanges(Collection<String> createdFiles, Collection<String> modifiedFiles, Collection<String> deletedFiles) {
        try {
            notificationDelegate.notifyFileChanges(createdFiles, modifiedFiles, deletedFiles);
        } catch (NullPointerException notExpectingThis) {
            // this will FFDC because we caught it, no need to do anything else
        }
    }
}
