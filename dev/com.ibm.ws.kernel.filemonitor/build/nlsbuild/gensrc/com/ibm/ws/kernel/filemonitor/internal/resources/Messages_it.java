/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:36 EST 2017
 */

package com.ibm.ws.kernel.filemonitor.internal.resources;

public class Messages_it extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "UNABLE_TO_DELETE_CACHE_FILE", "CWWKE0404E: Impossibile eliminare il file della cache {0}." },
      { "badDiskCache", "CWWKE0402W: Impossibile assegnare l''ubicazione della cache specificata, per cui tutte le informazioni relative ai file monitorati saranno archiviate in memoria. {0}={1}" },
      { "badFilter", "CWWKE0400W: Il parametro specificato non indica un filtro di nome file valido. {0}={1}" },
      { "badInterval", "CWWKE0401W: Il parametro specificato non rappresenta un intervallo di monitoraggio valido. {0}={1}" },
      { "createMonitorException", "CWWKE0403W: Si \u00e8 verificata un''eccezione durante la creazione di un monitor per {0}. Il monitor per questa risorsa \u00e8 disabilitato. Il messaggio dell''eccezione era: {1}" },
      { "fileMonitorDisabled", "CWWKE0406W: Si \u00e8 verificata pi\u00f9 di un''eccezione {0} durante la notifica di modifiche a un monitor. La classe di monitor \u00e8 {1}. Questo FileMonitor \u00e8 stato disabilitato. " },
      { "fileMonitorException", "CWWKE0405W: Si \u00e8 verificata un''eccezione durante la notifica a un monitor circa le seguenti creazioni, modifiche ed eliminazioni di file: {0}, {1}, {2}. La classe di monitor \u00e8 {3}. Il messaggio dell''eccezione era: {4}" }
   };
}
