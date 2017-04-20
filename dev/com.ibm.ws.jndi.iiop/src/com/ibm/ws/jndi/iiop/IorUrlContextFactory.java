package com.ibm.ws.jndi.iiop;

import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

import javax.naming.spi.ObjectFactory;

import org.osgi.service.component.annotations.Component;

import com.ibm.wsspi.application.lifecycle.ApplicationRecycleComponent;

@Component(configurationPolicy=IGNORE,property={"service.vendor=ibm","osgi.jndi.url.scheme=ior"})
public class IorUrlContextFactory extends UrlContextFactory implements ObjectFactory, ApplicationRecycleComponent {}
