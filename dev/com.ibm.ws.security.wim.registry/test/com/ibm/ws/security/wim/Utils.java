package com.ibm.ws.security.wim;

import org.osgi.framework.ServiceReference;

import com.ibm.ws.security.wim.ConfigManager;
import com.ibm.ws.security.wim.ProfileManager;
import com.ibm.ws.security.wim.ServiceProvider;

public class Utils {

	public static void doSetConfiguration(ServiceProvider serviceProvider, ServiceReference<ConfigManager> configManagerRef) {
		serviceProvider.setConfiguration(configManagerRef);
	}

	public static void doProfileService(ServiceProvider serviceProvider, ServiceReference<ProfileManager> profileServiceRef) {
		serviceProvider.setProfileservice(profileServiceRef);
    }
}
