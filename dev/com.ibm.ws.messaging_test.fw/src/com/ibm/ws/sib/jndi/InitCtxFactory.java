/* ************************************************************************** */
/* COMPONENT_NAME: WAS.compensation                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/*                                                              */
/*  
 /*  ============================================================================*/
/*  IBM Confidential OCO Source Materials                                     */
/*                                                                            */
/*  Copyright IBM Corp. 2012
 /*                                                                            */
/*  The source code for this program is not published or otherwise divested   */
/*  of its trade secrets, irrespective of what has been deposited with the    */
/*  U.S. Copyright Office.                                                    */
/*  ============================================================================*/
/*                                                                */
/*                                                                            */
/*  DESCRIPTION:                                                              */
/*  Initial context factory for the flat context tutorial factory             */
/*                                                                            */
/*  Change History:                                                           */
/*                                                                            */
/*  Date      Programmer Defect    Description                                */
/*  --------  ---------- ------    -----------                                */
/*  01/28/03  dvines@uk. 000000    Creation                                   */
/*  05/26/04  djvines    205610    Provide an setUp method to set system props*/
/*                                                                            */
/* ************************************************************************** */

package com.ibm.ws.sib.jndi;

import javax.naming.Context;

/* ************************************************************************** */
/**
 * Initial context factory for the hierarchical context tutorial factory
 * 
 * @author David Vines
 * 
 */
/* ************************************************************************** */
public class InitCtxFactory implements javax.naming.spi.InitialContextFactory {

    private static javax.naming.Context _ctx = new HierCtx(new java.util.Hashtable());

    public javax.naming.Context getInitialContext(java.util.Hashtable env) {
        return _ctx;
    }

    /* -------------------------------------------------------------------------- */
    /*
     * setUp method
     * /* --------------------------------------------------------------------------
     */
    /**
     * This method sets the environment so that the naming service will use this
     * factory if does not already have an initial context and to use our
     * own javaURLContextFactory in the event that someone else (e.g. was.naming
     * in a full build) overrides the standard behaviour.
     */
    public static void setUp() {
        System.setProperty(
                           Context.INITIAL_CONTEXT_FACTORY,
                           InitCtxFactory.class.getName());

        System.setProperty(
                           Context.URL_PKG_PREFIXES,
                           "com.ibm.ws.sib.jndi");

    }
}
