/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package test.jsonp.bundle;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.spi.JsonProvider;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * OSGi service with a dependency on JsonProvider.
 */
@Component(configurationPolicy = ConfigurationPolicy.IGNORE, immediate = true)
public class ServiceThatRequiresJsonp {

    @Reference
    private JsonProvider jsonpProvider;

    @Activate
    protected void activate(ComponentContext context) throws Exception {
        System.out.println("TEST3: JsonProvider obtained from declarative services is " + jsonpProvider.getClass().getName());

        StringWriter sw = new StringWriter();
        JsonWriter w = jsonpProvider.createWriter(sw);
        Map<String, Object> disc = new HashMap<String, Object>();
        disc.put("type", "driver");
        disc.put("weight", 171);
        disc.put("speed", 14);
        disc.put("glide", 5);
        disc.put("turn", -1);
        disc.put("fade", 2);
        JsonObject o = jsonpProvider.createObjectBuilder(disc).build();
        w.writeObject(o);
        w.close();
        String json = sw.toString();

        System.out.println("TEST4: JSON generated " + json);
    }
}
