package com.ibm.ws.filetransfer.routing.archiveExpander;

import java.io.File;
import java.io.IOException;

public interface UnixModeHelper {

  public void setPermissions(File f, int unixMode) throws IOException;
}