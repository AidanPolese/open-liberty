/************** Begin Copyright - Do not add comments here **************
 *
 *  
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2015
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag          Person          Defect/Feature      Comments
 * -------      ------          --------------      --------------------------------------------------
 */
package com.ibm.ws.security.java2sec;

import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;

/**
 * This class holds the Java 2 Security permissions configured in the server.xml
 */
@Trivial
public class JavaPermissionsConfiguration {

	/**
	 * The trace component used for logging to the trace.log file.
	 */
	private static final TraceComponent tc = Tr.register(JavaPermissionsConfiguration.class);
	
	/**
	 * Constant for the configuration Id
	 */
	private static final String KEY_ID = "config.id";
	
	/**
	 * Constant for the codebase configuration key.
	 */
	public static final String CODE_BASE = "codebase";
	
	/**
	 * Constant for the signedBy configuration key.
	 */
	public static final String SIGNED_BY = "signedBy";

	/**
	 * Constant for the principalType configuration key.
	 */
	public static final String PRINCIPAL_TYPE = "principalType";

	/**
	 * Constant for the principalName configuration key.
	 */
	public static final String PRINCIPAL_NAME = "principalName";

	/**
	 * Constant for the permission class configuration key.
	 */
	public static final String PERMISSION = "className";

	/**
	 * Constant for the targetName configuration key.
	 */
	public static final String TARGET_NAME = "name";

	/**
	 * Constant for the actions configuration key.
	 */
	public static final String ACTIONS = "actions";

	/**
	 * Constant for the restriction configuration key.
	 */
	public static final String RESTRICTION = "restriction";

	/**
	 * Map to hold the configuration properties for this permission object
	 */
	public volatile Map<String, Object> config;
	
	/**
	 * Activate the Java Permission configuration
	 * @param properties
	 * @param cc
	 */
    @Activate
	protected void activate(Map<String, Object> properties, ComponentContext cc) {
    	config = properties;
    }
    
    /**
     * Deactivate the Java Permission configuration
     */
    @Deactivate
    protected void deactivate(ComponentContext cc) {
    	config = null;
    }
    
    /**
     * Get the data/value associated with the specified key for this permission configuration.
     * 
     * @param key
     */
    public Object get(String key) {
    	if(config != null) {
    		return config.get(key);
    	}
    	else
    		return null;
    }
}
