/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.app.classpath;

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
@LocalBean
public class CheckJvmAppClasspathPackagesBean {

    private static ClassLoader JVM_APP_LOADER = ClassLoader.getSystemClassLoader();

    @PostConstruct
    public void printJvmAppClasspathPackages() {
        Method m;
        try {
            m = ClassLoader.class.getDeclaredMethod("getPackages");
            m.setAccessible(true);
            Package[] pkgs = (Package[]) m.invoke(JVM_APP_LOADER);
            for (Package p : pkgs) {
                System.out.println("AppLoader can load: " + p.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
