/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package test.config.extensions.schema.generator;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import com.ibm.websphere.metatype.SchemaGenerator;
import com.ibm.websphere.metatype.SchemaGeneratorOptions;

public class GeneratorTest {

    private SchemaGenerator schemaGenerator = null;

    public void activate(ComponentContext compContext) {

        BundleContext bundleContext = compContext.getBundleContext();
        List<Bundle> metatypeBundles = new ArrayList<Bundle>();
        for (Bundle bundle : bundleContext.getBundles()) {
            if (bundle.getSymbolicName().startsWith("test.config.extensions"))
                metatypeBundles.add(bundle);
        }

        SchemaGeneratorOptions options = new SchemaGeneratorOptions();
        options.setEncoding("UTF-8");
        options.setBundles(metatypeBundles.toArray(new Bundle[] {}));

        // generate schema. We don't do anything with the generated schema, but use this to generate
        // the error messages that 
        try {
            if (schemaGenerator != null)
                schemaGenerator.generate(new ByteArrayOutputStream(), options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deactivate(ComponentContext context) {
        //NOP
    }

    public void setSchemaGenerator(SchemaGenerator generator) {
        this.schemaGenerator = generator;
    }
}
