/* **************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                             */
/*                                                                              */
/* IBM Confidential OCO Source Material                                         */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2012 */
/* The source code for this program is not published or otherwise divested      */
/* of its trade secrets, irrespective of what has been deposited with the       */
/* U.S. Copyright Office.                                                       */
/*                                                                              */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                */
/*                                                                              */
/*  Change History:                                                             */
/*                                                                              */
/*  Date      Programmer  Defect      Description                               */
/*  --------  ----------  ------      -----------                               */
/*  12-05-24  timmccor    734766      Enhance FFDC error reporting              */
/* **************************************************************************** */
package com.ibm.tx.jta.util.logging;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.tx.util.logging.FFDCFilterer;

public class TxFFDCFilter implements FFDCFilterer
{
    private Logger _logger;

    private static String FFDCFile = System.getProperty("com.ibm.tx.FFDCFile", "ffdc.xml");
    private static String FFDCLoggerName = System.getProperty("com.ibm.tx.FFDCLoggerName");
    private static String FFDCLoggerResourceBundle = System.getProperty("com.ibm.tx.FFDCLoggerResourceBundle");

    public TxFFDCFilter() throws Exception
    {
        this(FFDCFile);
    }

    public TxFFDCFilter(String file) throws Exception
    {
        if (FFDCLoggerName == null)
        {
            _logger = Logger.getAnonymousLogger();
        }
        else
        {
            _logger = Logger.getLogger(FFDCLoggerName, FFDCLoggerResourceBundle);
        }

        _logger.setLevel(Level.ALL);

        _logger.addHandler(new FileHandler(file));
    }

    public void processException(Throwable e, String s1, String s2, Object o)
    {
        _logger.logp(Level.SEVERE, s1, s2, o.toString(), e);
    }

    public void processException(Throwable e, String s1, String s2)
    {
        _logger.logp(Level.SEVERE, s1, s2, "", e);
    }

    public void processException(Throwable th, String sourceId, String probeId,
			Object[] objectArray)
	{
		_logger.logp(Level.SEVERE, sourceId, probeId, objectArray.toString(), th);
	}

	public void processException(Throwable th, String sourceId, String probeId,
			Object callerThis, Object[] objectArray)
	{
		_logger.logp(Level.SEVERE, callerThis + " " + sourceId, probeId, objectArray.toString(), th);
	}
}
