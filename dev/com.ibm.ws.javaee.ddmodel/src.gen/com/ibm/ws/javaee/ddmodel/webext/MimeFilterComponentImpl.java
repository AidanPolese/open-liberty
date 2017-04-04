// NOTE: This is a generated file. Do not edit it directly.
package com.ibm.ws.javaee.ddmodel.webext;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Component(configurationPid = "com.ibm.ws.javaee.dd.webext.MimeFilter",
     configurationPolicy = ConfigurationPolicy.REQUIRE,
     immediate=true,
     property = "service.vendor = IBM")
public class MimeFilterComponentImpl implements com.ibm.ws.javaee.dd.webext.MimeFilter {
private Map<String,Object> configAdminProperties;
private com.ibm.ws.javaee.dd.webext.MimeFilter delegate;
     protected java.lang.String target;
     protected java.lang.String mime_type;

     @Activate
     protected void activate(Map<String, Object> config) {
          this.configAdminProperties = config;
          target = (java.lang.String) config.get("target");
          mime_type = (java.lang.String) config.get("mime-type");
     }

     @Override
     public java.lang.String getTarget() {
          if (delegate == null) {
               return target == null ? null : target;
          } else {
               return target == null ? delegate.getTarget() : target;
          }
     }

     @Override
     public java.lang.String getMimeType() {
          if (delegate == null) {
               return mime_type == null ? null : mime_type;
          } else {
               return mime_type == null ? delegate.getMimeType() : mime_type;
          }
     }
     public Map<String,Object> getConfigAdminProperties() {
          return this.configAdminProperties;
     }

     public void setDelegate(com.ibm.ws.javaee.dd.webext.MimeFilter delegate) {
          this.delegate = delegate;
     }
}
