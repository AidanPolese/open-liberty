package com.ibm.ws.jaxrs20.managedbeans;

import java.util.Set;

import com.ibm.ws.jaxrs20.metadata.EndpointInfo;

public class ManagedBeanEndpointInfo extends EndpointInfo {

	public ManagedBeanEndpointInfo(String servletName, String servletClassName,
			String servletMappingUrl, String appClassName, String appPath,
			Set<String> providerAndPathClassNames) {
		super(servletName, servletClassName, servletMappingUrl, appClassName, appPath,
				providerAndPathClassNames);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
}
