//IBM Confidential OCO Source Material
//  5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//  The source code for this program is not published or otherwise divested
//  of its trade secrets, irrespective of what has been deposited with the
//  U.S. Copyright Office.
//Change history:
//Defect 359657 "61SVT: Steady increase of JVM heap size causes OutOfMemoryErr"  2006/04/03 Scott Johnson


package com.ibm.ws.jsp.runtime;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.util.ThreadPool;
import com.ibm.ws.util.ThreadPoolListener;
import com.ibm.ws.util.WSThreadLocal;

public class JspThreadPoolListener implements ThreadPoolListener {
    static private Logger logger;
	private static final String CLASS_NAME="com.ibm.ws.jsp.runtime.JspThreadPoolListener";
    static {
        logger = Logger.getLogger("com.ibm.ws.jsp");
    }

	private static Hashtable threadLocals=new Hashtable();
	JspThreadPoolListener(){
		super();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
            logger.logp(Level.FINER, CLASS_NAME, "JspThreadPoolListener", "created JspThreadPoolListener");
        }
	}
	/*	JspThreadPoolListener(int initialCapacity) {
		super(initialCapacity);
	}
	JspThreadPoolListener(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}
	JspThreadPoolListener(Map<? extends K,? extends V> map) {
		super(map);
	}
*/	
    public void threadPoolCreated(ThreadPool tp) {
    }

    public void threadCreated(ThreadPool tp, int poolSize) {
    }

    public void threadStarted(ThreadPool tp, int activeThreads, int maxThreads) {
    }

    public void threadReturned(ThreadPool tp, int activeThreads, int maxThreads) {
    }

    public void threadDestroyed(ThreadPool tp, int poolSize) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
            logger.logp(Level.FINER, CLASS_NAME, "threadDestroyed", "tp= ["+tp+"]  poolSize= ["+poolSize+"]");
        }
        Integer threadId = new Integer(Thread.currentThread().hashCode());
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
            logger.logp(Level.FINER, CLASS_NAME, "threadDestroyed", "threadId= ["+threadId+"]");
        }
        WSThreadLocal thr = (WSThreadLocal) JspThreadPoolListener.threadLocals.get(threadId);
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
            logger.logp(Level.FINER, CLASS_NAME, "threadDestroyed", "thr from Hashtable= ["+thr+"]");
        }
        if (thr!=null) {
            Map threadPoolMap = null;
            if ((threadPoolMap = (Map) thr.get()) != null) {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
                    logger.logp(Level.FINER, CLASS_NAME, "threadDestroyed", "map from threadLocal : ["+threadPoolMap+"]");
                }
                for (Iterator itr = threadPoolMap.keySet().iterator(); itr.hasNext();) {
                	String webAppKey=(String)itr.next();
                    HashMap webAppPool = (HashMap)threadPoolMap.get(webAppKey);
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
                        logger.logp(Level.FINER, CLASS_NAME, "threadDestroyed", "webAppKey = [" + webAppKey+"]");
                        logger.logp(Level.FINER, CLASS_NAME, "threadDestroyed", "map from webAppPoolMap = [" + webAppPool+"]");
                    }
                    if (webAppPool!=null) {
    	                for (Iterator itr2 = webAppPool.keySet().iterator(); itr2.hasNext();) {
    	                    String tagKey = (String)itr2.next();
    	                    TagArray tagArray = (TagArray)webAppPool.get(tagKey);
    	                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
    	                        logger.logp(Level.FINER, CLASS_NAME, "threadDestroyed", "tagKey = [" + tagKey+"]");
    	                        logger.logp(Level.FINER, CLASS_NAME, "threadDestroyed", "tagArray = [" + tagArray+"]");
    	                    }
    	                    if (tagArray!=null) {
    		                    tagArray.releaseTags();
    		                    tagArray = null;     
    		                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
    		                        logger.logp(Level.FINER, CLASS_NAME, "threadDestroyed", "released tags and set tagArray to null");
    		                    }
    	                    }
    	                }
    	                webAppPool.clear();
    	                webAppPool = null;
                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
                            logger.logp(Level.FINER, CLASS_NAME, "threadDestroyed", "cleared webAppPool and setwebAppPool map to null");
                        }
                    }
                }
                threadPoolMap=null;
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
                    logger.logp(Level.FINER, CLASS_NAME, "threadDestroyed", "cleared threadPoolMap and set threadPoolMap to null");
                }
            }
            
        }
    }
    
    public static void addThreadLocal(WSThreadLocal wst, Integer threadId) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
            logger.logp(Level.FINER, CLASS_NAME, "addThreadLocal", "wst= ["+wst+"]");
            logger.logp(Level.FINER, CLASS_NAME, "addThreadLocal", "threadId= ["+threadId+"]");
        }
    	threadLocals.put(threadId, wst);
    }
    
}
