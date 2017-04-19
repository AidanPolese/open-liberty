/*
 * Copyright 2012 International Business Machines Corp.
 * 
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.ws.jbatch.cdi.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.jbatch.container.cdi.CDIBatchArtifactFactory;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;

/**
 * Note: BatchCDIInjectionExtension - gets control on BeforeBeanDiscovery event (i.e
 * when CDI is starting up, before it scans for beans).  Injects BatchProducerBean
 * into the CDI framework.
 * 
 * BatchProducerBean - provides @Producer methods for @BatchProperty, JobContext and StepContext.
 * 
 * Use CDI to load batch artifacts such as JobContext, StepContext, BatchProperties,
 * and any other CDI beans configured by the app.
 */
@Component(configurationPolicy = ConfigurationPolicy.IGNORE)
public class CDIBatchArtifactFactoryImpl implements CDIBatchArtifactFactory {

	private final static Logger logger = Logger.getLogger(CDIBatchArtifactFactoryImpl.class.getName());

	/**
	 * Use CDI to load the artifact with the given ID.
	 * 
	 * @return the loaded artifact; or null if CDI is not enabled for the app.
	 */
	@Override
	public Object load(String batchId) {

		Object loadedArtifact = getArtifactById(batchId);

		if (loadedArtifact != null) {
		    logger.finest("load: batchId: " + batchId 
		                    + ", artifact: " + loadedArtifact
		                    + ", artifact class: " + loadedArtifact.getClass().getCanonicalName());
		}

		return loadedArtifact;
	}

	/**
	 * @return the CDI bean reference for the given id
	 */
	private Object getArtifactById(String id) {

		BeanManager bm = getBeanManager();

		Bean bean = (bm != null) ? getBean(bm, id) : null;
		
		return (bean != null)
		            ? bm.getReference(bean, bean.getBeanClass(), bm.createCreationalContext(bean))
		            : null;
	}

	/**
	 * @return the BeanManager, located at java:comp/BeanManager, if one is installed
	 *         (meaning the app uses CDI).
	 */
	@FFDCIgnore(NameNotFoundException.class)
	protected BeanManager getBeanManager() {
	    try {
	        InitialContext initialContext = new InitialContext();
	        return (BeanManager) initialContext.lookup("java:comp/BeanManager");
	    } catch (NameNotFoundException nnfe) {
	        // FFDC ignore.
	        // CDI not loaded.  Return null and inject artifacts ourselves.
	        return null;

	    } catch (NamingException ne) {
	        return null;
	    }
	}
	
	/**
	 * @param id Either the EL name of the bean, or its fully qualified class name.
	 * 
	 * @return the bean for the given artifact id.
	 */
	protected Bean<?> getBean(BeanManager bm, String id) {
	    
	    // Get all beans with the given EL name (id).  EL names are applied via @Named.
        // If the bean is not annotated with @Named, then it does not have an EL name
        // and therefore can't be looked up that way.  Instead, assume the given id is
        // a fully-qualified class name and lookup the bean of that class type. 
	    Set<Bean<?>> beans = bm.getBeans(id);
        
	    return (beans.isEmpty()) 
	                ? getBeanForClassName(bm, id)
	                : beans.iterator().next(); 
	}
	                
	    
	/**
	 * Use the given BeanManager to lookup the set of CDI-registered beans with 
	 * the given class name.
	 * 
	 * @return the bean with the given className.
	 */
	@FFDCIgnore(ClassNotFoundException.class)
	protected Bean<?> getBeanForClassName(BeanManager bm, String className) {
	    
	    try {
	        Class<?> clazz = getContextClassLoader().loadClass(className);
	        return findBeanForClass( bm.getBeans(clazz), clazz );
	    } catch (ClassNotFoundException cnfe) {
	        // Ignore it.
	        logger.finest("getBeansForClassName: ClassNotFoundException for " + className + ": " + cnfe);
	        return null;
	    }
	}
	
	/**
	 * @return thread context classloader
	 */
	protected ClassLoader getContextClassLoader() {
	    return AccessController.doPrivileged( new PrivilegedAction<ClassLoader> () { 
	        public ClassLoader run() {
	            return Thread.currentThread().getContextClassLoader();
	        }
	    });
	}
	
	/**
	 * @return the bean within the given set whose class matches the given clazz.
	 */
	protected Bean<?> findBeanForClass(Set<Bean<?>> beans, Class<?> clazz) {
	    for (Bean<?> bean : beans) {
	        if ( bean.getBeanClass().equals(clazz) ) {
	            return bean;
	        }
	    }
	    return null;
	}

}
