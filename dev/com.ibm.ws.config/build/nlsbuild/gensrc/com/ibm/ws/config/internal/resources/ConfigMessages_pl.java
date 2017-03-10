/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:39 EST 2017
 */

package com.ibm.ws.config.internal.resources;

public class ConfigMessages_pl extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "audit.dropin.being.processed", "CWWKG0093A: Przetwarzanie zasobu element\u00f3w upuszczanych konfiguracji: {0}" },
      { "audit.include.being.processed", "CWWKG0028A: Przetwarzanie obejmowa\u0142o zas\u00f3b konfiguracji: {0}" },
      { "config.validator.activeValue", "Dla w\u0142a\u015bciwo\u015bci {0} zostanie ustawiona warto\u015b\u0107 {1}." },
      { "config.validator.activeValueNull", "W\u0142a\u015bciwo\u015b\u0107 {0} zostanie ustawiona na brak warto\u015bci." },
      { "config.validator.activeValueSecure", "W\u0142a\u015bciwo\u015b\u0107 {0} zostanie ustawiona na warto\u015b\u0107 zdefiniowan\u0105 w elemencie {1}." },
      { "config.validator.attributeConflict", "W przypadku w\u0142a\u015bciwo\u015bci {0} wyst\u0119puje konflikt warto\u015bci:" },
      { "config.validator.foundConflictInstance", "Znaleziono konflikt ustawie\u0144 dla {1} instancji konfiguracji {0}." },
      { "config.validator.foundConflictSingleton", "Znaleziono konflikt ustawie\u0144 dla konfiguracji {0}." },
      { "config.validator.valueConflict", "Warto\u015b\u0107 {0} jest ustawiona w elemencie {1}." },
      { "config.validator.valueConflictNull", "W elemencie {0} nie jest ustawiona \u017cadna warto\u015b\u0107." },
      { "config.validator.valueConflictSecure", "W elemencie {0} jest ustawiona warto\u015b\u0107 bezpieczna." },
      { "copyright", "\nLicensed Material - Property of IBM\n(C) COPYRIGHT International Business Machines Corp. 2010 - Wszelkie prawa zastrze\u017cone.\nU\u017cytkownik\u00f3w z instytucji rz\u0105dowych USA obowi\u0105zuj\u0105 warunki\numowy GSA ADP Schedule Contract z IBM Corp." },
      { "error.ExtendsAliasMustExtend", "CWWKG0097E: Utrwalona to\u017csamo\u015b\u0107 {0} okre\u015bla atrybut ibm:extendsAlias o warto\u015bci {1} i dlatego musi okre\u015bla\u0107 atrybut ibm:extends." },
      { "error.alias.collision", "CWWKG0026E: Co najmniej dwie definicje metatypu wsp\u00f3\u0142u\u017cytkuj\u0105 t\u0119 sam\u0105 utrwalon\u0105 to\u017csamo\u015b\u0107 (PID) lub alias. To\u017csamo\u015b\u0107 PID lub alias elementu {0} s\u0105 wsp\u00f3\u0142u\u017cytkowane przez definicje klasy obiektu {1}." },
      { "error.attribute.validation.exception", "CWWKG0075E: Warto\u015b\u0107 {2} nie jest poprawn\u0105 warto\u015bci\u0105 atrybutu {1} elementu konfiguracji {0}. Komunikat sprawdzania poprawno\u015bci: {3}." },
      { "error.breaking.include.conflict", "CWWKG0088E: Element konfiguracji {1} jest okre\u015blony w dw\u00f3ch r\u00f3\u017cnych zasobach konfiguracji: {0} i {2}." },
      { "error.cannot.read.location", "CWWKG0090E: Zas\u00f3b konfiguracji {0} nie istnieje lub nie mo\u017cna go odczyta\u0107. " },
      { "error.config.update.disk", "CWWKG0024E: Konfiguracja serwera {0} nie zosta\u0142a zaktualizowana na dysku. B\u0142\u0105d: {1}" },
      { "error.config.update.event", "CWWKG0025E: Zdarzenia aktualizacji nie zosta\u0142y wyemitowane dla konfiguracji serwera {0}. B\u0142\u0105d: {1}" },
      { "error.config.update.exception", "CWWKG0074E: Nie mo\u017cna zaktualizowa\u0107 konfiguracji dla {0} z unikalnym identyfikatorem {2}, poniewa\u017c wyst\u0105pi\u0142 wyj\u0105tek: {1}." },
      { "error.config.update.init", "CWWKG0015E: System nie mo\u017ce zaktualizowa\u0107 co najmniej jednej konfiguracji. B\u0142\u0105d: {0}" },
      { "error.configValidator.error", "CWWKG0047E: Wyst\u0105pi\u0142 b\u0142\u0105d podczas pr\u00f3by sprawdzenia dokumentu konfiguracji: {0}, {1}." },
      { "error.configValidator.keyInfoMissing", "CWWKG0049E: W obr\u0119bie podpisu zawartego w dokumencie konfiguracji nie znaleziono elementu KeyInfo: {0}." },
      { "error.configValidator.parseFailed", "CWWKG0045E: Nie mo\u017cna przeprowadzi\u0107 analizy dokumentu konfiguracji: {0}, {1}." },
      { "error.configValidator.protectedSectionModified", "CWWKG0053E: Zmodyfikowano sekcj\u0119 dokumentu konfiguracji zabezpieczon\u0105 podpisem: {0}." },
      { "error.configValidator.signatureMissing", "CWWKG0048E: Dokument konfiguracji nie zawiera podpisu: {0}." },
      { "error.configValidator.signatureNotValid", "CWWKG0054E: Podpis zawarty w dokumencie konfiguracji nie jest poprawny: {0}." },
      { "error.configValidator.signerNotAuthorized", "CWWKG0050E: Dokument konfiguracji zosta\u0142 podpisany przy u\u017cyciu nieautoryzowanej jednostki: {0}." },
      { "error.configValidator.unmarshalFailed", "CWWKG0046E: Nie mo\u017cna rozdzieli\u0107 podpisu zawartego w dokumencie konfiguracji: {0}, {1}." },
      { "error.configValidator.x509CertificateMissing", "CWWKG0052E: W obr\u0119bie podpisu zawartego w dokumencie konfiguracji nie znaleziono elementu X509Certificate: {0}." },
      { "error.configValidator.x509DataMissing", "CWWKG0051E: W obr\u0119bie podpisu zawartego w dokumencie konfiguracji nie znaleziono elementu X509Data: {0}." },
      { "error.conflicting.rename.attribute", "CWWKG0068E: Nie mo\u017cna zmieni\u0107 nazwy atrybutu {0} na {1} w utrwalonej to\u017csamo\u015bci {2}, poniewa\u017c nazwa tego atrybutu zosta\u0142a ju\u017c zmieniona przez rozszerzony metatyp." },
      { "error.dsExists", "CWWKG0039E: Element wyznaczony z {0} zosta\u0142 ju\u017c zarejestrowany." },
      { "error.extendsAlias.collision", "CWWKG0100E: Atrybut ibm:extendsAlias {2} jest duplikowany w pojedynczej hierarchii rozszerze\u0144. Identyfikatory PID to {0} i {1}. Dokonaj takiej zmiany, aby ka\u017cdy atrybut ibm:extendsAlias by\u0142 unikalny w ramach hierarchii rozszerze\u0144." },
      { "error.factoryOnly", "CWWKG0061E: Utrwalona to\u017csamo\u015b\u0107 {0} nie jest utrwalon\u0105 to\u017csamo\u015bci\u0105 fabryki, wi\u0119c nie mo\u017ce rozszerza\u0107 utrwalonej to\u017csamo\u015bci {1}." },
      { "error.factoryOnly.extendsAlias", "CWWKG0096E: Utrwalona to\u017csamo\u015b\u0107 {0} nie jest utrwalon\u0105 to\u017csamo\u015bci\u0105 fabryki, wi\u0119c nie mo\u017ce mie\u0107 atrybutu ibm:extendsAlias o warto\u015bci {1}. Zmie\u0144 konfiguracj\u0119 utrwalonej to\u017csamo\u015bci tak, aby by\u0142a u\u017cywana utrwalona to\u017csamo\u015b\u0107 fabryki, albo usu\u0144 atrybut ibm:extendsAlias z utrwalonej to\u017csamo\u015bci." },
      { "error.fileNotFound", "CWWKG0040E: Nie znaleziono pliku {0}." },
      { "error.final.override", "CWWKG0060E: Nie mo\u017cna nadpisa\u0107 lub zmieni\u0107 nazwy atrybutu {0} dla utrwalonej to\u017csamo\u015bci {1}, poniewa\u017c jest on zadeklarowany jako final przez utrwalon\u0105 to\u017csamo\u015b\u0107 {2}." },
      { "error.include.location.not.specified", "CWWKG0089E: W elemencie konfiguracji do\u0142\u0105czenia okre\u015blonym w wierszu {0} zasobu {1} musi by\u0107 okre\u015blony atrybut po\u0142o\u017cenia." },
      { "error.invalid.boolean.attribute", "CWWKG0081E: Warto\u015b\u0107 {0} atrybutu boolowskiego {1} jest niepoprawna. Poprawne s\u0105 warto\u015bci true (prawda) oraz false (fa\u0142sz). U\u017cyta zostanie warto\u015b\u0107 domy\u015blna: {2}." },
      { "error.invalidArgument", "CWWKG0041E: Niepoprawny argument {0}. Warto\u015b\u0107 musi by\u0107 okre\u015blona." },
      { "error.invalidOCDRef", "B\u0141\u0104D: to\u017csamo\u015b\u0107 PID metatypu [{0}] okre\u015bla nieistniej\u0105cy identyfikator definicji klasy obiektu [{1}]" },
      { "error.missing.required.attribute", "CWWKG0058E: Brak wymaganego atrybutu {1} w elemencie {0} o unikalnym identyfikatorze {2}." },
      { "error.missing.required.attribute.singleton", "CWWKG0095E: Brak wymaganego atrybutu {1} w elemencie {0}." },
      { "error.missingSuper", "CWWKG0059E: Nie mo\u017cna przetworzy\u0107 utrwalonej to\u017csamo\u015bci {0}, poniewa\u017c rozszerza ona niedost\u0119pn\u0105 utrwalon\u0105 to\u017csamo\u015b\u0107 {1}." },
      { "error.ocdExists", "CWWKG0038E: Klasa obiektu z {0} zosta\u0142a ju\u017c zarejestrowana." },
      { "error.parentpid.and.childalias", "CWWKG0098E: Utrwalona to\u017csamo\u015b\u0107 {0} okre\u015bla atrybut {1} o warto\u015bci {2} i dlatego musi okre\u015bla\u0107 atrybut {3}." },
      { "error.parse.bundle", "CWWKG0002E: Analizator sk\u0142adni konfiguracji wykry\u0142 b\u0142\u0105d podczas przetwarzania pakunku, wersji lub utrwalonej to\u017csamo\u015bci (PID). B\u0142\u0105d: {0}, b\u0142\u0105d: {1}, przyczyna: {2}" },
      { "error.parse.server", "CWWKG0001E: Analizator sk\u0142adni konfiguracji wykry\u0142 b\u0142\u0105d podczas analizowania elementu g\u0142\u00f3wnego konfiguracji i przywo\u0142ywanych dokument\u00f3w konfiguracyjnych. B\u0142\u0105d: {0}" },
      { "error.prod.ext.features.not.found", "CWWKG0078E: Rozszerzenie produktu {0} nie zawiera \u017cadnych sk\u0142adnik\u00f3w." },
      { "error.prod.ext.not.defined", "CWWKG0080E: Rozszerzenie produktu o nazwie {0} nie istnieje." },
      { "error.prod.ext.not.found", "CWWKG0079E: Nie mo\u017cna znale\u017a\u0107 rozszerzenia produktu {0} w po\u0142o\u017ceniu {1}." },
      { "error.rename.attribute.missing", "CWWKG0067E: Nie mo\u017cna zmieni\u0107 nazwy definicji atrybutu {1} okre\u015blonej przez atrybut ibm:rename {2} w utrwalonej to\u017csamo\u015bci {0}." },
      { "error.schemaGenException", "CWWKG0036E: B\u0142\u0105d podczas generowania schematu: {0}" },
      { "error.schemaGenInvalidJarLocation", "CWWKG0037E: B\u0142\u0119dne po\u0142o\u017cenie pliku JAR." },
      { "error.specify.parentpid", "CWWKG0077E: Definicja metatypu dla {0} definiuje alias elementu potomnego, ale nie definiuje elementu nadrz\u0119dnego." },
      { "error.superFactoryOnly", "CWWKG0062E: Utrwalona to\u017csamo\u015b\u0107 {0} nie jest utrwalon\u0105 to\u017csamo\u015bci\u0105 fabryki, wi\u0119c nie mo\u017ce by\u0107 rozszerzana przez utrwalon\u0105 to\u017csamo\u015b\u0107 {1}." },
      { "error.syntax.parse.server", "CWWKG0014E: Analizator sk\u0142adni konfiguracji wykry\u0142 b\u0142\u0105d sk\u0142adniowy XML podczas analizowania elementu g\u0142\u00f3wnego konfiguracji i przywo\u0142ywanych dokument\u00f3w konfiguracyjnych. B\u0142\u0105d: {0}, plik: {1}, wiersz: {2}, kolumna: {3}" },
      { "error.targetRequired", "CWWKG0034E: Nale\u017cy okre\u015bli\u0107 plik docelowy" },
      { "error.unique.value.conflict", "CWWKG0031E: Warto\u015b\u0107 {1} okre\u015blona dla atrybutu unikalnego {0} jest ju\u017c u\u017cywana." },
      { "error.unknownArgument", "CWWKG0035E: Nieznana opcja: {0}" },
      { "error.variable.name.missing", "CWWKG0091E: Dla zmiennej w wierszu {0} zasobu {1} musi by\u0107 okre\u015blony atrybut nazwy." },
      { "error.variable.value.missing", "CWWKG0092E: Dla zmiennej w wierszu {0} zasobu {1} musi by\u0107 okre\u015blony atrybut warto\u015bci." },
      { "fatal.configValidator.documentNotValid", "CWWKG0044E: Nast\u0105pi\u0142o zamkni\u0119cie serwera, poniewa\u017c dokument konfiguracji nie zawiera poprawnego podpisu: {0}." },
      { "fatal.configValidator.dropinsEnabled", "CWWKG0056E: Nast\u0105pi\u0142o zamkni\u0119cie serwera, poniewa\u017c w\u0142\u0105czone s\u0105 elementy upuszczane." },
      { "frameworkShutdown", "CWWKG0010I: Serwer {0} jest zamykany z powodu poprzedniego b\u0142\u0119du inicjowania." },
      { "info.config.refresh.nochanges", "CWWKG0018I: Konfiguracja serwera nie zosta\u0142a zaktualizowana. Nie wykryto zmian funkcjonalnych." },
      { "info.config.refresh.start", "CWWKG0016I: Rozpoczynanie aktualizacji konfiguracji serwera." },
      { "info.config.refresh.stop", "CWWKG0017I: Konfiguracja serwera zosta\u0142a pomy\u015blnie zaktualizowana w ci\u0105gu {0} sek." },
      { "info.config.refresh.timeout", "CWWKG0027W: Przekroczenie limitu czasu podczas aktualizowania konfiguracji serwera." },
      { "info.configValidator.documentValid", "CWWKG0055I: Dokument konfiguracji zawiera poprawny podpis: {0}." },
      { "info.configValidator.validator", "CWWKG0043I: U\u017cywana klasa analizatora poprawno\u015bci konfiguracji: {0}." },
      { "info.ignore.invalid.optional.include", "CWWKG0006I: Niepoprawny kod @include? zasobu ({0}) zosta\u0142 zignorowany.  Wiersz: {1}, {2}" },
      { "info.ignore.unresolved.optional.include", "CWWKG0005I: Nierozstrzygni\u0119ty opcjonalny zas\u00f3b {0} ({1}) zosta\u0142 zignorowany. Wiersz: {2}, {3}" },
      { "info.prop.ignored", "CWWKG0003I: Operator nie jest okre\u015blony albo podana warto\u015b\u0107 to NULL lub warto\u015b\u0107 pusta. W\u0142a\u015bciwo\u015b\u0107 zosta\u0142a zignorowana. W\u0142a\u015bciwo\u015b\u0107: {0}, plik: {1}" },
      { "info.unsupported.api", "CWWKG0004I: Ten interfejs API nie jest obs\u0142ugiwany: {0}" },
      { "missing.metatype.file", "CWWKG0073W: W pakunku {0} nie mo\u017cna znale\u017a\u0107 plik\u00f3w lokalizacji metatypu." },
      { "schemagen.alias.required", "CWWKG0022E: Alias konfiguracji jest wymagany dla konfiguracji zagnie\u017cd\u017conej {0}." },
      { "schemagen.bad.reference.extension", "CWWKG0029E: Atrybut {0} nie ma rozszerzenia ibm:reference lub rozszerzenie to nie okre\u015bla warto\u015bci pid." },
      { "schemagen.bad.reference.pid", "CWWKG0030E: Odwo\u0142anie pid {0} wymienione w rozszerzeniu ibm:reference nie istnieje." },
      { "schemagen.duplicate.pid", "CWWKG0021E: Ta sama utrwalona to\u017csamo\u015b\u0107 konfiguracji (PID) {0} jest zdefiniowana w wielu plikach metatype.xml." },
      { "schemagen.invalid.child", "CWWKG0023E: Konfiguracja potomna {0} musi by\u0107 konfiguracj\u0105 fabryki." },
      { "schemagen.invalid.extension.pid", "CWWKG0066E: Utrwalona to\u017csamo\u015b\u0107 metatypu {0} pr\u00f3buje rozszerzy\u0107 nieistniej\u0105c\u0105 utrwalon\u0105 to\u017csamo\u015b\u0107 {1}." },
      { "schemagen.invalid.parent", "CWWKG0020E: Konfiguracja nadrz\u0119dna {0} okre\u015blona w atrybucie {1} nie jest poprawna." },
      { "schemagen.invalid.type.override", "CWWKG0064E: Niepoprawne przes\u0142oni\u0119cie typu atrybutu przez atrybut {0} w metatypie {1}. Zamiast niego zostanie u\u017cyty oryginalny typ {2}." },
      { "schemagen.no.attrib.desc", "CWWKG0071W: Atrybut {0} definicji klasy obiektu {1} w pakunku {2} nie ma opisu atrybutu." },
      { "schemagen.no.attrib.name", "CWWKG0072W: Atrybut {0} definicji klasy obiektu {1} w pakunku {2} nie ma nazwy atrybutu." },
      { "schemagen.noextensions", "CWWKG0019E: Konfiguracja nadrz\u0119dna {0} okre\u015blona w atrybucie {1} nie obs\u0142uguje rozszerze\u0144." },
      { "schemagen.non.factorypid.extension", "CWWKG0065E: Utrwalona to\u017csamo\u015b\u0107 {0} nieb\u0119d\u0105ca to\u017csamo\u015bci\u0105 fabryki pr\u00f3buje rozszerzy\u0107 inny metatyp." },
      { "schemagen.rename.attribute.missing", "CWWKG0063E: Nie mo\u017cna zmieni\u0107 nazwy definicji atrybutu {1} okre\u015blonej przez atrybut ibm:rename {2} w utrwalonej to\u017csamo\u015bci {0}." },
      { "schemagen.unresolved.attrib.desc", "CWWKG0069W: Atrybut {0} definicji klasy obiektu {1} w pakunku {2} ma nierozstrzygni\u0119ty opis atrybutu." },
      { "schemagen.unresolved.attrib.name", "CWWKG0070W: Atrybut {0} definicji klasy obiektu {1} w pakunku {2} ma nierozstrzygni\u0119t\u0105 nazw\u0119 atrybutu." },
      { "warn.bad.reference.filter", "CWWKG0086W: Filtr odwo\u0142a\u0144 {0} nie jest poprawny." },
      { "warn.bundle.factory.noinstance", "CWWKG0009W: Konfiguracja {0} w pakunku {1} okre\u015bla konfiguracj\u0119 fabryki bez identyfikatora." },
      { "warn.cannot.resolve.optional.include", "CWWKG0084W: Nie mo\u017cna rozpozna\u0107 do\u0142\u0105czonego opcjonalnego pliku konfiguracyjnego: {0}" },
      { "warn.config.delete.failed", "CWWKG0012W: System nie mo\u017ce usun\u0105\u0107 konfiguracji {0}." },
      { "warn.config.delete.failed.multiple", "CWWKG0013W: System nie usun\u0105\u0142 konfiguracji {0}. Znaleziono wiele zgodnych konfiguracji." },
      { "warn.config.invalid.using.default.value", "CWWKG0083W: Wyst\u0105pi\u0142o niepowodzenie sprawdzania poprawno\u015bci podczas przetwarzania w\u0142a\u015bciwo\u015bci [{0}] o warto\u015bci [{1}]. Domy\u015blna u\u017cywana warto\u015b\u0107: {2}. " },
      { "warn.config.invalid.value", "CWWKG0032W: Okre\u015blono nieoczekiwan\u0105 warto\u015b\u0107 w\u0142a\u015bciwo\u015bci [{0}], warto\u015b\u0107 = [{1}]. Oczekiwano nast\u0119puj\u0105cych warto\u015bci: {2}." },
      { "warn.config.validate.failed", "CWWKG0011W: Sprawdzenie poprawno\u015bci konfiguracji nie powiod\u0142o si\u0119. {0}" },
      { "warn.configValidator.refreshFailed", "CWWKG0057W: Nie za\u0142adowano nowej konfiguracji, poniewa\u017c podpis jest niepoprawny." },
      { "warn.file.delete.failed", "CWWKG0007W: System nie mo\u017ce usun\u0105\u0107 zasobu {0}" },
      { "warn.file.mkdirs.failed", "CWWKG0008W: System nie mo\u017ce utworzy\u0107 katalog\u00f3w {0}." },
      { "warn.parse.circular.include", "CWWKG0042W: Do\u0142\u0105czone zasoby konfiguracji tworz\u0105 zale\u017cno\u015b\u0107 cykliczn\u0105: {0}." },
      { "warning.invalid.boolean.attribute", "CWWKG0082W: Warto\u015b\u0107 {0} dla atrybutu boolowskiego {1} zostanie zinterpretowana jako false (fa\u0142sz)." },
      { "warning.multiple.matches", "CWWKG0087W: Warto\u015b\u0107 [{1}] okre\u015blona dla atrybutu odwo\u0142ania [{0}] nie jest poprawna, poniewa\u017c jest zgodna z wieloma konfiguracjami." },
      { "warning.old.config.still.in.use", "CWWKG0076W: Poprzednia konfiguracja dla elementu {0} o identyfikatorze {1} jest nadal w u\u017cyciu." },
      { "warning.pid.not.found", "CWWKG0033W: Warto\u015b\u0107 [{1}] okre\u015blona dla atrybutu odniesienia [{0}] nie zosta\u0142a znaleziona w konfiguracji." },
      { "warning.unexpected.server.element", "CWWKG0085W: Dokument konfiguracji serwera zawiera zagnie\u017cd\u017cony element serwera. Zagnie\u017cd\u017cona konfiguracja zostanie zignorowana." },
      { "warning.unrecognized.merge.behavior", "CWWKG0094W: Warto\u015b\u0107 {0}, kt\u00f3ra zosta\u0142a okre\u015blona dla zachowania scalania w elemencie konfiguracji do\u0142\u0105czenia, nie jest poprawna. W systemie zostanie u\u017cyta domy\u015blna warto\u015b\u0107 scalania." }
   };
}
