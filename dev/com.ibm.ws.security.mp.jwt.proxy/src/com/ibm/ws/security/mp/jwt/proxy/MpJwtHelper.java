/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.mp.jwt.proxy;

import java.security.Principal;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.eclipse.microprofile.jwt.JsonWebToken;
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

@Component(service = MpJwtHelper.class, name = "MpJwtHelper", immediate = true, property = "service.vendor=IBM")
public class MpJwtHelper {
	private static final TraceComponent tc = Tr.register(MpJwtHelper.class);
	static final String JSON_WEB_TOKEN_REF = "JsonWebToken";
	protected final static AtomicServiceReference<JsonWebToken> JsonWebTokenRef = new AtomicServiceReference<JsonWebToken>(
			JSON_WEB_TOKEN_REF);

	public static Principal getJsonWebTokenPricipal(Subject subject) {
		if (getJsonWebTokenService() == null) {
			return null;
		}

		Set<JsonWebToken> jsonWebTokenPrincipal = subject.getPrincipals(JsonWebToken.class);

		if (jsonWebTokenPrincipal.size() > 1) {
			multipleJsonWebTokenPrincipalsError(jsonWebTokenPrincipal);
		}

		if (!jsonWebTokenPrincipal.isEmpty()) {
			return jsonWebTokenPrincipal.iterator().next();
		}

		return null;
	}

	public static void addJsonWebToken(Subject subject, Hashtable<String, ?> customProperties, String key) {
		if (customProperties != null && getJsonWebTokenService() != null) {
			JsonWebToken jsonWebToken = (JsonWebToken) customProperties.get(key);
			if (jsonWebToken != null) {
				subject.getPrincipals().add(jsonWebToken);
			}
		}
	}

	public static Principal cloneJsonWebTokenPrincipal(Subject subject) {
		if (isJavaVersionAtLeast18()) {
			return getJsonWebTokenService();
		} else {
			return null;
		}
	}

	/**
	 * @param principals
	 */

	private static void multipleJsonWebTokenPrincipalsError(Set<JsonWebToken> principals) {
		String principalNames = null;
		for (JsonWebToken principal : principals) {
			if (principalNames == null)
				principalNames = principal.getName();
			else
				principalNames = principalNames + ", " + principal.getName();
		}
		// throw new IllegalStateException(Tr.formatMessage(tc,
		// "SEC_TOO_MANY_PRINCIPALS", principalNames));
	}

	private static JsonWebToken getJsonWebTokenService() {
		return JsonWebTokenRef.getService();
	}

	public static boolean isJavaVersionAtLeast18() {
		return JavaInfo.majorVersion() >= 8;
	}

	@Reference(service = JsonWebToken.class, name = JSON_WEB_TOKEN_REF, cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.STATIC)
	protected void setJsonWebToken(ServiceReference<JsonWebToken> ref) {
		if (isJavaVersionAtLeast18()) {
			JsonWebTokenRef.setReference(ref);
		}
	}

	protected void unsetJsonWebToken(ServiceReference<JsonWebToken> ref) {
		if (isJavaVersionAtLeast18()) {
			JsonWebTokenRef.unsetReference(ref);
		}
	}

	@Activate
	protected void activate(ComponentContext cc) {
		if (isJavaVersionAtLeast18()) {
			JsonWebTokenRef.activate(cc);
		}
	}

	@Modified
	protected void modified(Map<String, Object> props) {
	}

	@Deactivate
	protected void deactivate(ComponentContext cc) {
		if (isJavaVersionAtLeast18()) {
			JsonWebTokenRef.deactivate(cc);
		}
	}
}
