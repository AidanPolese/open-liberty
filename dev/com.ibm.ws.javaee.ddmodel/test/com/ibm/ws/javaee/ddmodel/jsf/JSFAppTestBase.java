/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.jsf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.jmock.Expectations;
import org.osgi.framework.ServiceReference;

import com.ibm.ws.javaee.dd.jsf.FacesConfig;
import com.ibm.ws.javaee.ddmodel.DDTestBase;
import com.ibm.ws.javaee.version.FacesVersion;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

public class JSFAppTestBase extends DDTestBase {
    protected boolean isWarModule = false;

    protected FacesConfig parse(final String xml) throws Exception {
        return parseJSFApp(xml, FacesConfig.VERSION_2_2);
    }

    FacesConfig parseJSFApp(final String xml, final int maxVersion) throws Exception {
        FacesConfigAdapter adapter = new FacesConfigAdapter();
        final Container root = mockery.mock(Container.class, "root" + mockId++);
        final Entry entry = mockery.mock(Entry.class, "entry" + mockId++);
        final OverlayContainer rootOverlay = mockery.mock(OverlayContainer.class, "rootOverlay" + mockId++);
        final ArtifactContainer artifactContainer = mockery.mock(ArtifactContainer.class, "artifactContainer" + mockId++);
        final Container container = mockery.mock(Container.class, "container" + mockId++);
        final ServiceReference<FacesVersion> versionRef = mockery.mock(ServiceReference.class, "sr" + mockId++);

        mockery.checking(new Expectations() {
            {
                allowing(artifactContainer).getPath();
                will(returnValue(FacesConfig.DD_NAME));

                allowing(rootOverlay).getFromNonPersistentCache(with(any(String.class)), with(any(Class.class)));
                will(returnValue(null));

                allowing(container).getEntry(FacesConfig.DD_NAME);
                will(returnValue(entry));

                allowing(entry).getPath();
                will(returnValue('/' + FacesConfig.DD_NAME));

                allowing(entry).adapt(InputStream.class);
                will(returnValue(new ByteArrayInputStream(xml.getBytes("UTF-8"))));

                allowing(rootOverlay).addToNonPersistentCache(with(any(String.class)), with(any(Class.class)), with(any(Object.class)));

                allowing(versionRef).getProperty(FacesVersion.FACES_VERSION);
                will(returnValue(maxVersion));
            }
        });

        adapter.setVersion(versionRef);

        try {
            return adapter.adapt(root, rootOverlay, artifactContainer, container);
        } catch (UnableToAdaptException e) {
            Throwable cause = e.getCause();
            throw cause instanceof Exception ? (Exception) cause : e;
        }
    }

    protected static final String jsf22() {
        return "<faces-config" +
               " xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"" +
               " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
               " xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-facesconfig_2_2.xsd\"" +
               " version=\"2.2\"" +
               ">";
    }

}
