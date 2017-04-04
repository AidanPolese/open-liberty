/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.bval;

import com.ibm.ws.javaee.dd.bval.ValidationConfig;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.adaptable.module.adapters.EntryAdapter;
import com.ibm.wsspi.artifact.ArtifactEntry;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

public class ValidationConfigEntryAdapter implements EntryAdapter<ValidationConfig> {

    @Override
    public ValidationConfig adapt(Container root,
                                  OverlayContainer rootOverlay,
                                  ArtifactEntry artifactEntry,
                                  Entry entryToAdapt) throws UnableToAdaptException {

        String path = artifactEntry.getPath();
        ValidationConfig validationConfig = (ValidationConfig) rootOverlay.getFromNonPersistentCache(path, ValidationConfig.class);
        if (validationConfig == null) {
            try {
                ValidationConfigDDParser ddParser = new ValidationConfigDDParser(root, entryToAdapt);
                validationConfig = ddParser.parse();
            } catch (ParseException e) {
                throw new UnableToAdaptException(e);
            }

            rootOverlay.addToNonPersistentCache(path, ValidationConfig.class, validationConfig);
        }

        return validationConfig;
    }

}
