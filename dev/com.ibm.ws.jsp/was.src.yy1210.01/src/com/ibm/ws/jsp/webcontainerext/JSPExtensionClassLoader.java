//IBM Confidential OCO Source Material
//  5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//  The source code for this program is not published or otherwise divested
//  of its trade secrets, irrespective of what has been deposited with the
//  U.S. Copyright Office.
// CHANGE HISTORY
// Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
// PK50834        08/21/2007     sartoris            NPE when using securityManager variable in the _loadClass method
// PK71207        09/03/2008     sartoris            LINKAGEERROR IF JSP CONTAINER ATTEMPT TO CALL THE CLASSLOADER'S DEFINED CLASS METHOD

package com.ibm.ws.jsp.webcontainerext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;

import com.ibm.ws.jsp.Constants;
import com.ibm.wsspi.jsp.context.JspClassloaderContext;

public class JSPExtensionClassLoader extends URLClassLoader {
    private PermissionCollection permissionCollection = null;
    private CodeSource codeSource = null;
    private String className = null;
    private String packageName = null;
    private ClassLoader parent = null;
    private JspClassloaderContext jspClassloaderContext = null;

    public JSPExtensionClassLoader(URL[] urls,
                                   JspClassloaderContext jspClassloaderContext,
                                   String className,
                                   CodeSource codeSource,
                                   PermissionCollection permissionCollection) {
        super(urls, jspClassloaderContext.getClassLoader());
        this.jspClassloaderContext = jspClassloaderContext;
        this.permissionCollection = permissionCollection;
        this.codeSource = codeSource;
        this.className = className;
        this.parent = jspClassloaderContext.getClassLoader();
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        return (loadClass(name, false));
    }

    public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (System.getSecurityManager() != null){
            final String tmpName = name;
            final boolean tmpResolve = resolve;
            try{
                return (Class) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                        public Object run() throws ClassNotFoundException {
                                return _loadClass(tmpName, tmpResolve);
                        }
                    });
            }catch (PrivilegedActionException pae){
                throw (ClassNotFoundException)pae.getException();
            }
        }
        else{
            return _loadClass(name, resolve);
        }
    }

    private Class _loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class clazz = null;

        clazz = findLoadedClass(name);
        if (clazz != null) {
            if (resolve)
                resolveClass(clazz);
            return (clazz);
        }

        int dot = name.lastIndexOf('.');
        if (System.getSecurityManager() != null) {
            if (dot >= 0) {
                try {
                    //PK50834 switching from using securityManager variable to direct call to System
                    System.getSecurityManager().checkPackageAccess(name.substring(0, dot));
                }
                catch (SecurityException se) {
                    String error = "Security Violation, attempt to use " + "Restricted Class: " + name;
                    throw new ClassNotFoundException(error);
                }
            }
        }

        // Class is in a package other than the ones we know; delegate to thread context class loader
        if (name.startsWith(Constants.JSP_PACKAGE_PREFIX) == false &&
            name.startsWith(Constants.JSP_FIXED_PACKAGE_NAME) == false &&
            name.startsWith(Constants.OLD_JSP_PACKAGE_NAME) == false &&
            name.startsWith(Constants.TAGFILE_PACKAGE_NAME) == false) {
            clazz = parent.loadClass(name);
            if (resolve)
                resolveClass(clazz);
            return clazz;
        }
        else {
            String classFile = null;

            if (name.startsWith(Constants.JSP_FIXED_PACKAGE_NAME + "." + className)) {
                classFile = name.substring(Constants.JSP_FIXED_PACKAGE_NAME.length() + 1) + ".class";
            }
            else if (name.startsWith(Constants.OLD_JSP_PACKAGE_NAME+ "." + className)) {
                classFile = name.substring(Constants.OLD_JSP_PACKAGE_NAME.length() + 1) + ".class";
            }
            else {
                classFile = name.replace('.', File.separatorChar) + ".class";
            }
            byte[] cdata = loadClassDataFromFile(classFile);
            if (cdata != null) {
                    
                //PK71207 start
                // add try/catch block to catch an Error like java.lang.LinkageError if two threads are trying to call defineClass with the same class name.
                // We will try to find the loaded class again as if that error occurs means it should be loaded by the first thread.

                try {
                    if (System.getSecurityManager() != null) {
                        ProtectionDomain pd = new ProtectionDomain(codeSource, permissionCollection);
                        clazz = defClass(name, cdata, cdata.length, pd);
                    }
                    else {
                        clazz = defClass(name, cdata, cdata.length, null);
                    }
                }
                catch (Error e) {
                    clazz = findLoadedClass(name);
                    if (clazz != null) {
                        if (resolve)
                            resolveClass(clazz);
                        return (clazz);
                    }
                    cdata = loadClassDataFromFile(classFile); 
                    if (cdata==null) {
                        if (parent != null) {
                            clazz = parent.loadClass(classFile);
                            if (clazz != null) {
                                if (resolve)
                                    resolveClass(clazz);
                                return clazz;
                            }
                        }
                    }
                    throw e;
                }
                //PK71207 end
            }
            else {
                if (parent != null) {
                    clazz = parent.loadClass(classFile);
                }
            }
            if (clazz != null) {
                if (resolve)
                    resolveClass(clazz);
                return clazz;
            }
        }

        throw new ClassNotFoundException(name);
    }

    private final Class defClass(String className, byte[] classData, int length, ProtectionDomain pd) {
        if (jspClassloaderContext.isPredefineClassEnabled()) {
            classData = jspClassloaderContext.predefineClass(className, classData);
        }
        if (pd != null) {
            return defineClass(className, classData, 0, classData.length, pd);
        }
        else {
            return defineClass(className, classData, 0, classData.length);
        }
    }

    /**
     * Load JSP class data from file.
     */
    protected byte[] loadClassDataFromFile(String fileName) {
        byte[] classBytes = null;
        try {
            InputStream in = getResourceAsStream(fileName);
            if (in == null) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buf[] = new byte[1024];
            for (int i = 0;(i = in.read(buf)) != -1;)
                baos.write(buf, 0, i);
            in.close();
            baos.close();
            classBytes = baos.toByteArray();
        }
        catch (Exception ex) {
            return null;
        }
        return classBytes;
    }

    public URL getResource(String name) {
        URL resourceURL = findResource(name);
        if (resourceURL != null) {
            return resourceURL;
        }
        return parent.getResource(name);
    }

    public InputStream getResourceAsStream(String name) {
        try {
            URL resourceURL = getResource(name);
            if (resourceURL == null) {
                return null;
            }
            return resourceURL.openStream();
        }
        catch (java.net.MalformedURLException malURL) {
            return null;
        }
        catch (IOException io) {
            return null;
        }
    }
}
