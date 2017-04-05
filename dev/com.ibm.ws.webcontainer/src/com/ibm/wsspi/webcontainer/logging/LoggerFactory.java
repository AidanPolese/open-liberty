// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.wsspi.webcontainer.logging;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;


public class LoggerFactory{
    
    private static boolean override;
    private static ClassLoader classloader;
    private static String classname;
    private static LoggerFactory helper;
    public static final String MESSAGES = "com.ibm.ws.webcontainer.resources.Messages";

    private LoggerFactory(){

    }

    public static synchronized LoggerFactory getInstance(){
        if(helper != null){
            return helper;
        }

        if(override){
            try {
                helper = (LoggerFactory)Class.forName (classname,false, getClassLoader()).newInstance();
            }catch(ClassNotFoundException e) {
                helper = new LoggerFactory();
            } catch(IllegalAccessException e) {
                helper = new LoggerFactory();
            } catch(InstantiationException e) {
                helper = new LoggerFactory();
            }
            return helper;
        }
        else{
            return helper = new LoggerFactory();
        }
    }       

        
    public Logger getLogger(final String name, final String bundle) {

    	// We used to return a WebContainerLogger, but we now don't want anything except 
        // normal Logger behaviour, since the logging code handles printing out 
        // exceptions, so just return the same logger as the WebContainerLogger would wrap
        
         return AccessController.doPrivileged(
                new PrivilegedAction<Logger>() {
                    public Logger run() {
                                return Logger.getLogger(name, bundle);
                    }
            });
    }

    public Logger getLogger(String name) {
    	return getLogger(name, MESSAGES);
    }
    

    public static ClassLoader getClassLoader() {
        return classloader==null ? LoggerHelper.class.getClassLoader() : classloader ;
    }
    public static void setClassloader(ClassLoader loader) {
        classloader = loader;
    }
    
    public static void setClassname(String name) {
        classname = name;
    }
    
    public static void setOverride(boolean value) {
        override = value;
    }
        
}

