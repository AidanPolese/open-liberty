/* **************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                             */
/*                                                                              */
/* IBM Confidential OCO Source Material                                         */
/* 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2006 */
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
/*  06/03/16  mallam      354884      Creation                                  */
/* **************************************************************************** */
package com.ibm.ws.recoverylog.utils;

import java.io.File;
import java.util.StringTokenizer;


import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.ws.recoverylog.spi.TraceConstants;

public final class DirUtils
{
    private static final TraceComponent tc = Tr.register(DirUtils.class, TraceConstants.TRACE_GROUP, null);


    /**
     * Replaces forward and backward slashes in the source string with 'File.separator'
     * characters.
    */
    public static String createDirectoryPath(String source)
    {
       if (tc.isEntryEnabled()) Tr.entry(tc, "createDirectoryPath",source);

       String directoryPath = null;

       if (source != null)
       {
           directoryPath = "";

           final StringTokenizer tokenizer = new StringTokenizer(source,"\\/");

           while (tokenizer.hasMoreTokens())
           {
             final String pathChunk = tokenizer.nextToken();

             directoryPath += pathChunk;

             if (tokenizer.hasMoreTokens())
             {
               directoryPath += File.separator;
             }

           }
       }

       if (tc.isEntryEnabled()) Tr.exit(tc, "createDirectoryPath",directoryPath);
       return directoryPath;
    }

}
