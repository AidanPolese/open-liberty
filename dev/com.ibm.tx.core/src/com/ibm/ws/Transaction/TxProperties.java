package com.ibm.ws.Transaction;

/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2003,2008    */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/*  DESCRIPTION:                                                              */
/*                                                                            */
/*  Change History:                                                           */
/*                                                                            */
/*  Date      Programmer    Defect    Description                             */
/*  --------  ----------    ------    -----------                             */
/*  12/11/03  hursdlg      LIDB2775   Merge zOS and distributed code          */
/*  23-03-04  mdobbie     LIDB3133-23 Added SPI classification                */
/*  29-03-04  hursdlg       196258    Added single process flag               */
/*  28-04-04  hursdlg      LIDB2775   Added jta2 private interop flag         */
// 07/04/12 johawkes LIDB4171-35    Componentization
// 07/04/12 johawkes 430278         Further componentization
// 08/02/06   mallam        496147    Shareable LTC 6.1 compatibility
/* ************************************************************************** */

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * <p> This class is private to WAS.
 * Any use of this class outside the WAS Express/ND codebase
 * is not supported.
 * 
 */
public class TxProperties
{
    public final static String LTC_KEY = "ltc.always.required";
    public final static String NATIVE_KEY = "native.contexts.used";
    public final static String SINGLE_KEY = "single.process";
    public final static String JTA2INTEROP_KEY = "jta2.interop.supported";
    public final static boolean isZOS = false; // False on Liberty

    private final static Properties props = new Properties();

    static
    {
        final ClassLoader cl = TxProperties.class.getClassLoader();;
        final InputStream stream = cl.getResourceAsStream("transaction.properties");

        try
        {
            if (stream != null)
                props.load(stream);
        } catch (IOException ioe)
        {
            throw new IllegalStateException(ioe.getMessage());
        }
    }

    public final static boolean LTC_ALWAYS_REQUIRED =
                    Boolean.valueOf(props.getProperty(LTC_KEY, (isZOS ? "true" : "false"))).booleanValue();

    public final static boolean NATIVE_CONTEXTS_USED =
                    Boolean.valueOf(props.getProperty(NATIVE_KEY, (isZOS ? "true" : "false"))).booleanValue();

    // Indicator that WebSphere is running in a single process or multiple servant processes
    public final static boolean SINGLE_PROCESS =
                    Boolean.valueOf(props.getProperty(SINGLE_KEY, (isZOS ? "false" : "true"))).booleanValue();

    // Indicator that WebSphere supports JTA2 private interop protocol
    public final static boolean JTA2_INTEROP_SUPPORTED =
                    Boolean.valueOf(props.getProperty(JTA2INTEROP_KEY, (isZOS ? "false" : "true"))).booleanValue();

    // Indicator that WebSphere supports SHAREABLE LTC containment (WAS6.1 compatibility)
    // ... set via custom property in TxServiceImpl
    public static boolean SHAREABLE_LTC = false;
}
