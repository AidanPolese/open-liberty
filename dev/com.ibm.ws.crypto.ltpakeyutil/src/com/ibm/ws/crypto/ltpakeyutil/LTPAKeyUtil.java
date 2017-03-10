/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.crypto.ltpakeyutil;


public final class LTPAKeyUtil {

  public static byte[] encrypt(byte[] data, byte[] key, String cipher) throws Exception {
    return LTPACrypto.encrypt(data, key, cipher);
  }

  public static byte[] decrypt(byte[] msg, byte[] key, String cipher) throws Exception {
    return LTPACrypto.decrypt(msg, key, cipher);
  }

  public static boolean verifyISO9796(byte[][] key, byte[] data, int off, int len, byte[] sig, int sigOff, int sigLen) throws Exception {
    return LTPACrypto.verifyISO9796(key, data, off, len, sig, sigOff, sigLen);
  }

  public static byte[] signISO9796(byte[][] key, byte[] data, int off, int len) throws Exception {
    return LTPACrypto.signISO9796(key, data, off, len);
  }

  public static void setRSAKey(byte[][] key) {
    LTPACrypto.setRSAKey(key);
  }

  public static byte[][] getRawKey(LTPAPrivateKey privKey) {
    return privKey.getRawKey();
  }

  public static byte[][] getRawKey(LTPAPublicKey pubKey) {
    return pubKey.getRawKey();
  }

  public static LTPAKeyPair generateLTPAKeyPair() {
    return LTPADigSignature.generateLTPAKeyPair();
  }

  public static byte[] generate3DESKey() {
    return LTPACrypto.generate3DESKey();
  }

}