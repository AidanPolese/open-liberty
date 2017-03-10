/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2015
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.kernel.boot.archive.internal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.kernel.boot.archive.UnixModeHelper;

class UnixModeHelperImpl implements UnixModeHelper {

    @Override
    @FFDCIgnore({ UnsupportedOperationException.class, IOException.class })
    public int getUnixMode(File f) {
        int result = 0;
        try {
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(f.toPath(), LinkOption.NOFOLLOW_LINKS);
            for (PosixFilePermission perm : permissions) {
                switch (perm) {
                    case OTHERS_EXECUTE:
                        result += 1;
                        break;
                    case OTHERS_WRITE:
                        result += 2;
                        break;
                    case OTHERS_READ:
                        result += 4;
                        break;
                    case GROUP_EXECUTE:
                        result += 8;
                        break;
                    case GROUP_WRITE:
                        result += 16;
                        break;
                    case GROUP_READ:
                        result += 32;
                        break;
                    case OWNER_EXECUTE:
                        result += 64;
                        break;
                    case OWNER_WRITE:
                        result += 128;
                        break;
                    case OWNER_READ:
                        result += 256;
                        break;
                }
            }
        } catch (IOException e) {
            result = -1;
        } catch (UnsupportedOperationException e) {
            result = -1;
        }
        return result;
    }

}