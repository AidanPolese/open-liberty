/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2014
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.cache.servlet;

import org.osgi.service.component.annotations.Component;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

@Component(service=FragmentComposerFactory.class, property = { "service.vendor=IBM" })
public class FragmentComposerFactoryImpl implements FragmentComposerFactory {
	private static TraceComponent tc = Tr.register(FragmentComposerFactoryImpl.class,
			"WebSphere Dynamic Cache", "com.ibm.ws.cache.resources.dynacache");

	public FragmentComposer createFragmentComposer() {
		if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
			Tr.debug(tc, " Create FragmentComposer factory ");

		return new FragmentComposer(); 
	}
}
