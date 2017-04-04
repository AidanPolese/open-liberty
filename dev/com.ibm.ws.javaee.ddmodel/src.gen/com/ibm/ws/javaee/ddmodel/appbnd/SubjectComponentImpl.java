// NOTE: This is a generated file. Do not edit it directly.
package com.ibm.ws.javaee.ddmodel.appbnd;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Component(configurationPid = "com.ibm.ws.javaee.dd.appbnd.Subject",
     configurationPolicy = ConfigurationPolicy.REQUIRE,
     immediate=true,
     property = "service.vendor = IBM")
public class SubjectComponentImpl implements com.ibm.ws.javaee.dd.appbnd.Subject {
private Map<String,Object> configAdminProperties;
private com.ibm.ws.javaee.dd.appbnd.Subject delegate;
     protected java.lang.String name;
     protected java.lang.String access_id;

     @Activate
     protected void activate(Map<String, Object> config) {
          this.configAdminProperties = config;
          access_id = (java.lang.String) config.get("access-id");
          name = (java.lang.String) config.get("name");
     }

     @Override
     public java.lang.String getName() {
          if (delegate == null) {
               return name == null ? null : name;
          } else {
               return name == null ? delegate.getName() : name;
          }
     }

     @Override
     public java.lang.String getAccessId() {
          if (delegate == null) {
               return access_id == null ? null : access_id;
          } else {
               return access_id == null ? delegate.getAccessId() : access_id;
          }
     }
     public Map<String,Object> getConfigAdminProperties() {
          return this.configAdminProperties;
     }

     public void setDelegate(com.ibm.ws.javaee.dd.appbnd.Subject delegate) {
          this.delegate = delegate;
     }
}
