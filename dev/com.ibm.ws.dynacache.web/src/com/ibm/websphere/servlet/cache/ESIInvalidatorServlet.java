//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//Change Activity:
//
// $PQ91098 (248534) H28W601, 20050302,PDEK; Rollup of ESI Invalidator fix
// $267776 H28W601, 20050412, PDEK; Prevent ESI Invalidator from starting
// $273311 H28W602, 20050506, EEK; Undo 267776 changes

package com.ibm.websphere.servlet.cache;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.cache.servlet.ESIProcessor;

public class ESIInvalidatorServlet extends HttpServlet implements ExternalCacheAdapter {

	private static final long serialVersionUID = 8842671181312469591L;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ESIProcessor.run(request.getRemoteHost(), request.getInputStream(), response.getOutputStream());
	}

	public void setAddress(String address) {
	}

	public void writePages(Iterator externalCacheEntries) {
	}

	public void invalidatePages(Iterator urls) {
	}

	public synchronized void invalidateIds(Iterator ids) {
		ESIProcessor.invalidateIds(ids);
	}

	public void preInvoke(ServletCacheRequest sreq, HttpServletResponse sresp) {
	}

	public void postInvoke(ServletCacheRequest sreq, HttpServletResponse sresp) {
	}

	public void clear() {
		ESIProcessor.clearCaches();
	}

}
