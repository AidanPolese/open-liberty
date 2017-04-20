/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.anno.test.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Common_Data {
    public static String putIntoPath(String rootPath, String childPath) {
        return rootPath + File.separator + childPath;
    }

    public static String putIntoPath(String projectPath, String dataPath, String path) {
        return Common_Data.putIntoPath(projectPath, Common_Data.putIntoPath(dataPath, path));
    }

    public static List<String> putIntoPath(String rootPath, List<String> childPaths) {
        List<String> adjustedPaths = new ArrayList<String>();

        for (String nextChildPath : childPaths) {
            adjustedPaths.add(Common_Data.putIntoPath(rootPath, nextChildPath));
        }

        return adjustedPaths;
    }

    public static List<String> putInPath(String projectPath, String dataPath, List<String> paths) {
        List<String> adjustedPaths = new ArrayList<String>();

        for (String nextPath : paths) {
            adjustedPaths.add(Common_Data.putIntoPath(projectPath, dataPath, nextPath));
        }

        return adjustedPaths;
    }
}
