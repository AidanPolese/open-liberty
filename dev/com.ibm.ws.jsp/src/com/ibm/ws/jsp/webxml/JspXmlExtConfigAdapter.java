//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
// defect 400645 "Batchcompiler needs to get webcon custom props"  2004/10/25 Scott Johnson
// defect 395182.2  70FVT: make servlet 2.3 compatible with JSP 2.1 for migration 2007/02/07 Scott Johnson

package com.ibm.ws.jsp.webxml;

import org.osgi.service.component.ComponentContext;

import com.ibm.ws.jsp.configuration.JspXmlExtConfig;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.adaptable.module.adapters.ContainerAdapter;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

public class JspXmlExtConfigAdapter implements ContainerAdapter<JspXmlExtConfig> {

    @Override
    public JspXmlExtConfig adapt(Container root, OverlayContainer rootOverlay, ArtifactContainer artifactContainer, Container containerToAdapt) throws UnableToAdaptException {
        JspXmlExtConfig extConfig = (JspXmlExtConfig) rootOverlay.getFromNonPersistentCache(artifactContainer.getPath(), JspXmlExtConfig.class);
        return extConfig;
    }

    protected void activate(ComponentContext context) {
        
    }
    protected void deactivate(ComponentContext context) {
        
    }
}
