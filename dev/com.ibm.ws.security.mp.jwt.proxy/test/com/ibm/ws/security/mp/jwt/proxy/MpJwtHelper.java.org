/*
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.mp.jwt.proxy;

import java.security.Principal;
import java.util.Hashtable;
import java.util.Map;

import javax.security.auth.Subject;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.kernel.service.util.JavaInfo;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

/*
 * This is a utility service for MicroProfile JsonWebToken in a subject
 */
@Component(service = MpJwtHelper.class, name = "MpJwtHelper", immediate = true, property = "service.vendor=IBM")
public class MpJwtHelper {
	private static final TraceComponent tc = Tr.register(MpJwtHelper.class);
	static final String JSON_WEB_TOKEN_UTIL_REF = "JsonWebTokenUtil";
	protected final static AtomicServiceReference<JsonWebTokenUtil> JsonWebTokenUtilRef = new AtomicServiceReference<JsonWebTokenUtil>(
			JSON_WEB_TOKEN_UTIL_REF);

	public static Principal getJsonWebTokenPricipal(Subject subject) {
		JsonWebTokenUtil jsonWebTokenUtil = getJsonWebTokenUtil();
		if (jsonWebTokenUtil == null) {
			return null;
		}
		return jsonWebTokenUtil.getJsonWebTokenPrincipal(subject);
	}

	public static void addJsonWebToken(Subject subject, Hashtable<String, ?> customProperties, String key) {
		JsonWebTokenUtil jsonWebTokenUtil = getJsonWebTokenUtil();
		if (jsonWebTokenUtil != null && customProperties != null) {
			jsonWebTokenUtil.addJsonWebToken(subject, customProperties, key);
		}
	}

	public static Principal cloneJsonWebToken(Subject subject) {
		JsonWebTokenUtil jsonWebTokenUtil = getJsonWebTokenUtil();
		if (jsonWebTokenUtil != null) {
			return jsonWebTokenUtil.cloneJsonWebToken(subject);
		} else
			return null;
	}

	private static JsonWebTokenUtil getJsonWebTokenUtil() {
		if (isJavaVersionAtLeast18()) {
			return JsonWebTokenUtilRef.getService();
		} else {
			return null;
		}

	}

	private static boolean isJavaVersionAtLeast18() {
		return JavaInfo.majorVersion() >= 8;
	}

	@Reference(service = JsonWebTokenUtil.class, name = JSON_WEB_TOKEN_UTIL_REF, cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
	protected void setJsonWebTokenUtil(ServiceReference<JsonWebTokenUtil> ref) {
		if (isJavaVersionAtLeast18()) {
			JsonWebTokenUtilRef.setReference(ref);
		}
	}

	protected void unsetJsonWebTokenUtil(ServiceReference<JsonWebTokenUtil> ref) {
		if (isJavaVersionAtLeast18()) {
			JsonWebTokenUtilRef.unsetReference(ref);
		}
	}

	@Activate
	protected void activate(ComponentContext cc) {
		if (isJavaVersionAtLeast18()) {
			JsonWebTokenUtilRef.activate(cc);
		}
		if (tc.isDebugEnabled()) {
			Tr.debug(tc, "MpJwtHelper service is activated");
		}
	}

	@Modified
	protected void modified(Map<String, Object> props) {
	}

	@Deactivate
	protected void deactivate(ComponentContext cc) {
		if (isJavaVersionAtLeast18()) {
			JsonWebTokenUtilRef.deactivate(cc);
		}
		if (tc.isDebugEnabled()) {
			Tr.debug(tc, "MpJwtHelper service is activated");
		}
	}
}
