/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:49 EST 2017
 */

package com.ibm.ws.config.utility.resources;

public class UtilityOptions_cs extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "config.options", "\nAkce:\nfind \n\tNaj\u00edt konfigura\u010dn\u00ed \u00fasek k\u00f3du v \u00falo\u017ei\u0161ti. \n\ninstall \n\tSt\u00e1hnout konfigura\u010dn\u00ed \u00fasek k\u00f3du z \u00falo\u017ei\u0161t\u011b nebo pou\u017e\u00edt lok\u00e1ln\u00ed \n\tkonfigura\u010dn\u00ed \u00fasek k\u00f3du pro substituci prom\u011bnn\u00e9. \n\nVolby:\n--info \n\tVypsat v\u0161echny volby prom\u011bnn\u00e9 v konfigura\u010dn\u00edm \u00faseku k\u00f3du. Vr\u00e1tit\n\tpr\u00e1zdn\u00fd seznam, pokud konfigura\u010dn\u00ed \u00fasek k\u00f3du nem\u00e1 \u017e\u00e1dn\u00e9 prom\u011bnn\u00e9\n\tpro nahrazen\u00ed.\n\n--V[prom\u011bnn\u00e1]=value \n\tM\u016f\u017eete nahradit prom\u011bnn\u00e9 konfigura\u010dn\u00edho \u00faseku k\u00f3du nalezen\u00edm \n\ttvolby --info s va\u0161imi vstupn\u00edmi hodnotami. Prom\u011bnn\u00e9 jsou\n\tidentifikov\u00e1ny touto obslu\u017enou rutinou pomoc\u00ed --V[prom\u011bnn\u00e1]. \n\n--createConfigFile=path \n\tVoliteln\u00e9. \u00dasek k\u00f3du je zaps\u00e1n do ur\u010den\u00e9ho souboru\n\tnam\u00edsto obrazovky konzoly. P\u0159idejte poskytnut\u00fd \u00fasek k\u00f3du\n\tdo konfigurace server.xml pro zahrnut\u00ed ur\u010den\u00e9ho souboru. \n\n--encoding=[xor|aes] \n\tVoliteln\u00e9. Ur\u010dete k\u00f3dov\u00e1n\u00ed hesla \u00falo\u017ei\u0161t\u011b kl\u00ed\u010d\u016f. Podporovan\u00e1\n\tk\u00f3dov\u00e1n\u00ed jsou xor a aes. V\u00fdchoz\u00ed k\u00f3dov\u00e1n\u00ed je xor. \n\tP\u0159\u00edkazem --listCustom k\u00f3dov\u00e1n\u00ed securityUtility zjist\u00edte, zda se podporuje\n\tn\u011bkter\u00e9 z dal\u0161\u00edch vlastn\u00edch \u0161ifrov\u00e1n\u00ed.\n\n--key=key \n\tVoliteln\u00e9. Zadejte kl\u00ed\u010d, kter\u00fd m\u00e1 b\u00fdt pou\u017eit p\u0159i k\u00f3dov\u00e1n\u00ed pomoc\u00ed AES. Tento\n\t\u0159et\u011bzec je ha\u0161ov\u00e1n, aby vytvo\u0159il \u0161ifrovac\u00ed kl\u00ed\u010d, kter\u00fd se\n\tpou\u017eije k za\u0161ifrov\u00e1n\u00ed a de\u0161ifrov\u00e1n\u00ed hesla. Voliteln\u011b dodejte kl\u00ed\u010d\n\tserveru definov\u00e1n\u00edm prom\u011bnn\u00e9 wlp.password.encryption.key,\n\tjej\u00ed\u017e hodnotou je kl\u00ed\u010d. Pokud nen\u00ed tato volba uvedena, pou\u017eijte se\n\tv\u00fdchoz\u00ed kl\u00ed\u010d. \n\n--useLocalFile=file \n\tPou\u017eijte konfigura\u010dn\u00ed \u00fasek k\u00f3du z lok\u00e1ln\u00edho syst\u00e9mu soubor\u016f. Mus\u00edte \n\tur\u010dit cestu k souboru. Tato volba nahrazuje ur\u010den\u00ed \n\tn\u00e1zvu konfigura\u010dn\u00edho \u00faseku k\u00f3du.\n\tNap\u0159. configUtility --useLocalFile=file [volby]" }
   };
}
