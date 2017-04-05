package com.ibm.websphere.servlet.response;

import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.http.HttpServletResponse;
/**
 * 
 * StoredResponseCompat is a interface to allow both return types to co-exist for getHeaderNames
 * 
 * @ibm-api
 */
public interface StoredResponseCompat extends HttpServletResponse {
	public <T extends Enumeration & Collection> T getHeaderNames();
}
