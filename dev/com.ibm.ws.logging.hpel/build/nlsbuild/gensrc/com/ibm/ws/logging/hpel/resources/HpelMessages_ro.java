/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:33 EST 2017
 */

package com.ibm.ws.logging.hpel.resources;

public class HpelMessages_ro extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "HPEL_ErrorClosingZipStream", "HPEL0123W: A ap\u0103rut o eroare \u00een timpul \u00eenchiderii unui flux zip." },
      { "HPEL_ErrorReadingFileOffset", "HPEL0120E: A e\u015fuat extragerea offset-ului curent din flux pentru fi\u015fierul \"{0}\": {2}." },
      { "HPEL_ErrorReadingLogRecord", "HPEL0121E: A e\u015fuat citirea \u00eenregistr\u0103rii de istoric din fi\u015fierul \"{0}\": {1}." },
      { "HPEL_ErrorReadingLogRecordDoSkip", "HPEL0125W: Nu s-a putut g\u0103si o \u00eenregistrare bun\u0103 la offset-ul {0}. Se sar {1} octe\u0163i p\u00e2n\u0103 la urm\u0103toarea \u00eenregistrare din fi\u015fierul {2}." },
      { "HPEL_ErrorSettingFileOffset", "HPEL0119E: \u00cencercare incorect\u0103 de a pozi\u0163iona fluxul pentru fi\u015fierul \"{0}\" la offset-ul {1} unde o \u00eenregistrate de istoric a fost citit\u0103 cu succes anterior: {2}." },
      { "HPEL_ErrorSkippingFailedLogRecord", "HPEL0122E: Nu s-a putut s\u0103ri la urm\u0103toarea \u00eenregistrare dup\u0103 o eroare \u00een fi\u015fier \"{0}\": {1}." },
      { "HPEL_ErrorWhileSerializingHeader", "HPEL0105E: Excep\u0163ie \u00een timpul conversiei antetului \u00eentr-o matrice de octe\u0163i." },
      { "HPEL_ErrorWhileSerializingRecord", "HPEL0107E: Excep\u0163ie \u00een timpul conversiei \u00eenregistr\u0103rii de istoric \u00eentr-o matrice de octe\u0163i." },
      { "HPEL_ExceptionInPeriodicFlush", "HPEL0103W: Excep\u0163ie \u00een timpul epur\u0103rii periodice a fluxului istoric." },
      { "HPEL_FileSystem_Space_Warning", "HPEL0161W: Sistemul de fi\u015fiere la {0} are nevoie de {1} octe\u0163i pentru necesit\u0103\u0163ile de jurnalizare, dar i-au mai r\u0103mas doar {2} octe\u0163i." },
      { "HPEL_HeaderWithoutProcessId", "HPEL0104E: Antetul nu con\u0163ine informa\u0163ii de ID proces. Verifica\u0163i c\u0103 proprietatea {0} este setat\u0103 acolo." },
      { "HPEL_InconsistencyInHeaderRecordSize", "HPEL0116W: A e\u015fuat verificarea dimensiunii \u00eenregistr\u0103rii Antet istoric. Copia final\u0103 a unei dimensiuni a antetului \u00eenregistr\u0103rii ({0}) la offset-ul ({1}) estediferit de o copie de pornire ({2}). Aceasta poate indica o problem\u0103 cu fi\u015fierul istoric \"{3}\"." },
      { "HPEL_InconsistencyInLogRecordSize", "HPEL0117W: Copia de la coad\u0103 cu dimensiunea \u00eenregistr\u0103rii de mesaj ({0}) de la offset-ul ({1}) este diferit\u0103 de cea de la \u00eenceput ({2}). Aceasta poate indica o problem\u0103 cu fi\u015fierul istoric \"{3}\"." },
      { "HPEL_IncorrectSwitchHour", "HPEL0113W: Valoarea specificat\u0103 {0} este incorect\u0103 pentru o or\u0103 din zi. Se va folosi \u00een schimb valoarea {1}." },
      { "HPEL_LogHeaderWasNotSet", "HPEL0106E: Eroare invocare: \u00cencercare ilegal\u0103 de a exporta o \u00eenregistrare istoric \u00eenainte de a seta informa\u0163iile pentru proces." },
      { "HPEL_NoHeaderRecordInFileHead", "HPEL0114E: A e\u015fuat citirea \u00eenregistr\u0103rii de antet la \u00eenceputul fi\u015fierului \"{0}\": {1}" },
      { "HPEL_NoRecordAtLocation", "HPEL0108W: Nu s-au g\u0103sit \u00eenregistr\u0103ri la loca\u0163ia specificat\u0103." },
      { "HPEL_NoRecordsInFile", "HPEL0115E: Nu s-au g\u0103sit \u00eenregistr\u0103ri \u00een fi\u015fierul \"{0}\"." },
      { "HPEL_NotRepositoryFileNoProcessId", "HPEL0112E: Fi\u015fierul \"{0}\" folosit \u00een argument nu apar\u0163ine acestei magazii. Nu se poate extrage ID-ul de proces." },
      { "HPEL_NotRepositoryFileNoTimestamp", "HPEL0110E: Fi\u015fierul \"{0}\" folosit \u00een argument nu apar\u0163ine acestei magazii. Nu se poate extrage amprenta de timp." },
      { "HPEL_NotRepositoryLocation", "HPEL0109E: Loca\u0163ia specificat\u0103 nu apar\u0163ine acestei magazii." },
      { "HPEL_OffsetBeyondFileSize", "HPEL0118E: \u00cencercare incorect\u0103 de a pozi\u0163iona fluxul pentru fi\u015fierul \"{0}\" la offset-ul {1} care este peste dimensiunea sa: {2}." },
      { "HPEL_RepositoryFileMissing", "HPEL0111W: Fi\u015fierul \"{0}\" lipse\u015fte din magazie. Cel mai probabil a fost \u00eenl\u0103turat de managerul de p\u0103strare." },
      { "HPEL_RepositoryPointerNotInRepository", "HPEL0124W: Pointer-ul de magazie specificat nu apar\u0163ine niciunei magazii. Cel mai probabil fi\u015fierul c\u0103tre care se indic\u0103 a fost \u015fters deja." },
      { "HPEL_WrongBufferSizeValue", "HPEL0101W: Dimensiunea de buffer {0} specificat\u0103 \u00een proprietatea sistem {1} nu este un num\u0103r. Se folose\u015fte {2} \u00een loc." },
      { "HPEL_WrongFlushPeriodValue", "HPEL0102W: Perioada de epurare {0} specificat\u0103 \u00een proprietatea sistem {1} nu este un num\u0103r. Se folose\u015fte {2} \u00een loc." },
      { "IllegalArgInConstructingPatternLevel", "HPEL0150W: Argumente ilegale \u00een construirea unui element \u00een lista restr\u00e2ns\u0103 {0}" },
      { "InvalidPatternString", "HPEL0151I: \u015eirul Model/Nivel {0} nu poate fi parsat corespunz\u0103tor \u00eentr-un model loggerName \u015fi un nivel" }
   };
}
