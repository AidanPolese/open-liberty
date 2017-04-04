/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

/**
 * An <code>EJSWrapperBase</code> wraps an EJB for the purposes of invoking
 * lifecycle methods. This is a dynamic wrapper and is never generated, which
 * means that it does not interpose on any method calls. Instead, it exists
 * only to allow data to be passed around the container. These wrappers are
 * created as needed.
 **/
public class LifecycleInterceptorWrapper extends EJSWrapperBase
{
    // These method names are used if a lifecycle interceptor method is not
    // declared directly on the bean class.  Note that these method names will
    // eventually be used by EJBMDOrchestrator to find transaction attributes in
    // XML, so they should not be names that a customer can specify (i.e., we've
    // used a space, which cannot correspond to an actual method name)
    public static final String[] METHOD_NAMES =
    {
     "PostConstruct lifecycle interceptor",
     "PreDestroy lifecycle interceptor",
     "PrePassivate lifecycle interceptor",
     "PostActivate lifecycle interceptor",
    };

    // For TEBeanLifeCycleInfo.
    public static final String[] TRACE_NAMES =
    {
     "PostConstruct",
     "PreDestroy",
     "PrePassivate",
     "PostActivate",
    };

    private static final Class<?>[] NO_PARAMS = new Class<?>[0];

    public static final Class<?>[][] METHOD_PARAM_TYPES =
    {
     NO_PARAMS,
     NO_PARAMS,
     NO_PARAMS,
     NO_PARAMS,
    };

    public static final String[] METHOD_SIGNATURES =
    {
     "()",
     "()",
     "()",
     "()",
    };

    public static final String[] METHOD_JDI_SIGNATURES =
    {
     "()V",
     "()V",
     "()V",
     "()V",
    };

    /**
     * Index into {@link #methodInfos} corresponding to PostConstruct lifecycle
     * interceptors.
     */
    public static final int MID_POST_CONSTRUCT = 0;

    /**
     * Index into {@link #methodInfos} corresponding to PreDestroy lifecycle
     * interceptors.
     */
    public static final int MID_PRE_DESTROY = 1;

    /**
     * Index into {@link #methodInfos} corresponding to PrePassivate lifecycle
     * interceptors.
     */
    public static final int MID_PRE_PASSIVATE = 2;

    /**
     * Index into {@link #methodInfos} corresponding to PostActivate lifecycle
     * interceptors.
     */
    public static final int MID_POST_ACTIVATE = 3;

    /**
     * The number of methods required for this wrapper.
     */
    public static final int NUM_METHODS = 4;

    public LifecycleInterceptorWrapper(EJSContainer c, BeanO beanO)
    {
        this.container = c;
        this.wrapperManager = c.wrapperManager;
        this.ivCommon = null;
        this.isManagedWrapper = false;
        this.ivInterface = WrapperInterface.LIFECYCLE_INTERCEPTOR; // F743-1751CodRev

        this.beanId = beanO.beanId;
        this.bmd = beanO.home.beanMetaData;
        this.methodInfos = bmd.lifecycleInterceptorMethodInfos;
        this.methodNames = bmd.lifecycleInterceptorMethodNames;
        this.isolationAttrs = null;
        this.ivPmiBean = beanO.home.pmiBean;
    }
}
