/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:26 EST 2017
 */

package com.ibm.ws.kernel.boot.resources;

public class LauncherOptions_pl extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "action-desc.create", "\tUtw\u00f3rz nowy serwer, je\u015bli okre\u015blony serwer nie istnieje. Do okre\u015blenia\n\tszablonu u\u017cywanego przy tworzeniu nowego serwera mo\u017cna u\u017cy\u0107 opcji --template\n\t." },
      { "action-desc.debug", "\tUruchom okre\u015blony serwer na pierwszym planie w konsoli po nawi\u0105zaniu po\u0142\u0105czenia przez debuger\n\tz portem debugowania (warto\u015b\u0107 domy\u015blna: 7777)." },
      { "action-desc.dump", "\tZrzu\u0107 informacje diagnostyczne z serwera do archiwum. Mo\u017cna\n\tu\u017cy\u0107 opcji --archive.  Opcji --include mo\u017cna u\u017cy\u0107 z warto\u015bciami\n\t\"heap\", \"system\" i \"thread\"." },
      { "action-desc.help", "\tWydrukuj informacje pomocnicze." },
      { "action-desc.javadump", "\tZrzu\u0107 informacje diagnostyczne z maszyny JVM serwera. Opcji --include\n\tmo\u017cna u\u017cy\u0107 z warto\u015bciami \"heap\" i \"system\"." },
      { "action-desc.list", "\tWy\u015bwietl list\u0119 zdefiniowanych serwer\u00f3w aplikacji profilu Liberty." },
      { "action-desc.package", "\tUtw\u00f3rz pakiet serwera w formie archiwum. Mo\u017cna u\u017cy\u0107 opcji --archive.    \n\tOpcji --include mo\u017cna u\u017cy\u0107 z warto\u015bciami \u201eall\u201d, \u201eusr\u201d, \u201eminify\u201d, \n\t\u201ewlp\u201d, \u201erunnable\u201d, \u201eall,runnable\u201d oraz \u201eminify,runnable\u201d. Warto\u015bci \n\t\u201erunnable\u201d i \u201eall,runnable\u201d s\u0105 r\u00f3wnoznaczne. Warto\u015b\u0107 \u201erunnable\u201d \n\tdzia\u0142a tylko z archiwami typu jar." },
      { "action-desc.run", "\tUruchom okre\u015blony serwer na pierwszym planie w konsoli." },
      { "action-desc.start", "\tUruchom okre\u015blony serwer." },
      { "action-desc.status", "\tSprawd\u017a status okre\u015blonego serwera." },
      { "action-desc.stop", "\tZatrzymaj dzia\u0142aj\u0105c\u0105 instancj\u0119 okre\u015blonego serwera." },
      { "action-desc.version", "\tWy\u015bwietl informacje o wersji serwera i wyjd\u017a." },
      { "action-key.create", "    --create" },
      { "action-key.debug", "    --debug" },
      { "action-key.dump", "    --dump" },
      { "action-key.help", "    --help" },
      { "action-key.javadump", "    --javadump" },
      { "action-key.list", "    --list" },
      { "action-key.package", "    --package" },
      { "action-key.run", "    --run" },
      { "action-key.start", "    --start" },
      { "action-key.status", "    --status" },
      { "action-key.stop", "    --stop" },
      { "action-key.version", "    --version" },
      { "briefUsage", "Sk\u0142adnia: java [opcje JVM] -javaagent:bin/tools/ws-javaagent.jar \\        \n\t-jar bin/tools/ws-server.jar nazwaSerwera [dzia\u0142ania] [opcje]  " },
      { "javaAgent.desc", "\tOpcja maszyny JVM umo\u017cliwiaj\u0105ca okre\u015blenie agenta na potrzeby instrumentacji.   \n\t\u015arodowisko wykonawcze u\u017cywa instrumentacji do gromadzenia danych \u015bledzenia \n\ti innych informacji debugowania. Plik bootstrap-agent.jar znajduje si\u0119 w tym \n\tsamym katalogu co plik jar u\u017cywany do uruchomienia \u015brodowiska wykonawczego." },
      { "javaAgent.key", "    -javaagent:bin/tools/ws-javaagent.jar" },
      { "option-desc.archive", "\tOkre\u015bl archiwum docelowe do wygenerowania przez dzia\u0142anie package\n\tlub dump. Element docelowy mo\u017cna okre\u015bli\u0107 za pomoc\u0105 \u015bcie\u017cki pe\u0142nej lub\n\twzgl\u0119dnej. Je\u015bli ta opcja zostanie pomini\u0119ta, plik archiwum zostanie \n\tutworzony w katalogu wyj\u015bciowym serwera. Rozszerzenie nazwy pliku      \n\tdocelowego mo\u017ce wp\u0142ywa\u0107 na format wygenerowanego archiwum.       \n\tDomy\u015blnym formatem archiwum dla dzia\u0142ania pakowania jest pax \n\tw systemie z/OS i zip na wszystkich innych platformach.                                    \n\tU\u017cycie formatu jar archiwum spowoduje wygenerowanie samorozpakowuj\u0105cego \n\tpliku jar podobnego do oryginalnego archiwum instalatora.                                      \n\tFormat jar archiwum w po\u0142\u0105czeniu z warto\u015bci\u0105 runnable w opcji \n\t--include generuje mo\u017cliwy do uruchomienia plik jar, kt\u00f3ry mo\u017ce\n\turuchomi\u0107 serwer Liberty z poziomu pliku jar za pomoc\u0105 komendy java -jar." },
      { "option-desc.clean", "\tUsu\u0144 z pami\u0119ci podr\u0119cznej wszystkie informacje odnosz\u0105ce si\u0119 do tej instancji serwera." },
      { "option-desc.include", "\tLista warto\u015bci rozdzielonych przecinkami. Poprawne warto\u015bci zale\u017c\u0105\n\tod wykonywanej akcji." },
      { "option-desc.os", "\tOkre\u015bla systemy operacyjne, kt\u00f3re maj\u0105 by\u0107 obs\u0142ugiwane przez \n\tpakiet serwera. Podaj list\u0119 rozdzielan\u0105 przecinkami. Warto\u015bci\u0105 domy\u015bln\u0105 jest any,     \n\tco wskazuje, \u017ce serwer mo\u017ce by\u0107 wdra\u017cany w dowolnym systemie operacyjnym                     \n\tobs\u0142ugiwanym przez kod \u017ar\u00f3d\u0142owy.                                      \n\tAby wskaza\u0107, \u017ce dany system operacyjny nie ma by\u0107 obs\u0142ugiwany, nale\u017cy\n\tpoprzedzi\u0107 go znakiem minus (-). List\u0119 warto\u015bci odpowiadaj\u0105cych systemom operacyjnym\n\tmo\u017cna znale\u017a\u0107 w serwisie WWW OSGi Alliance pod adresem URL:                  \n\thttp://www.osgi.org/Specifications/Reference#os                      \n\tTa opcja dotyczy tylko operacji pakowania i mo\u017ce by\u0107 u\u017cywana   \n\ttylko z opcj\u0105 --include=minify. Je\u015bli system operacyjny zostanie   \n\twykluczony, nie mo\u017cna b\u0119dzie go p\u00f3\u017aniej doda\u0107, je\u015bli operacja minimalizacji         \n\tarchiwum zostanie powt\u00f3rzona. " },
      { "option-desc.template", "\tOkre\u015bl nazw\u0119 szablonu do u\u017cycia przy tworzeniu nowego serwera. " },
      { "option-key.archive", "    --archive=\"\u015bcie\u017cka do docelowego pliku archiwum\"" },
      { "option-key.clean", "    --clean" },
      { "option-key.include", "    --include=warto\u015b\u0107,warto\u015b\u0107,..." },
      { "option-key.os", "    --os=warto\u015b\u0107,warto\u015b\u0107,..." },
      { "option-key.template", "    --template=\"nazwaSzablonu\"" },
      { "processName.desc", "\tUnikalna nazwa serwera. Nazw\u0119 mo\u017cna utworzy\u0107 ze znak\u00f3w\n\talfanumerycznych Unicode (np. A-Za-z0-9), podkre\u015blenia \n\t(_), \u0142\u0105cznika (-), znaku plus (+) i kropki (.). Nazwa serwera\n\tnie mo\u017ce rozpoczyna\u0107 si\u0119 od \u0142\u0105cznika (-) ani kropki (.)." },
      { "processName.key", "    nazwaSerwera" },
      { "scriptUsage", "Sk\u0142adnia: {0} dzia\u0142anie nazwaSerwera [opcje]" },
      { "use.actions", "Dzia\u0142ania:" },
      { "use.jvmarg", "Opcje maszyny JVM:" },
      { "use.options", "Opcje:" }
   };
}
