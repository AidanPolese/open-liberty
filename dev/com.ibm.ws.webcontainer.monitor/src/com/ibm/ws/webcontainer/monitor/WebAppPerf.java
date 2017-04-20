// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.monitor;

public interface WebAppPerf {
    public void onApplicationAvailableForService();
    public void onApplicationUnavailableForService();
    public void onApplicationStart();
    public void onApplicationEnd();
    public void onServletStartService(String servletName, String url);
    public void onServletFinishService(String servletName, long responseTime, String url);
    public void onServletStartInit(String j2eeName, String servletName);
    public void onServletFinishInit(String servletName);
    public void onServletStartDestroy(String servletName);
    public void onServletFinishDestroy(String servletName);
    public void onServletUnloaded(String servletName);
    public void onServletAvailableForService(String servletName);
    public void onServletUnavailableForService(String servletName);
    public void onServletInitError(String servletName);
    public void onServletServiceError(String servletName);
    public void onServletServiceDenied(String servletName);
    public void onServletDestroyError(String servletName);
    public void onAsyncContextComplete(String servletName, long responseTime , String url);
}
