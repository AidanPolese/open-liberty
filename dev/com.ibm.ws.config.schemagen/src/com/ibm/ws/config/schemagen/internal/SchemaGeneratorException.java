package com.ibm.ws.config.schemagen.internal;

/**
 * This subclass of RuntimeException is used in the tool to differenciate between how to handle
 * RuntimeExceptions with nice messages that are probably user error and ones which don't have
 * pretty error messages and are probably product bugs.
 */
public class SchemaGeneratorException extends RuntimeException {

  public SchemaGeneratorException() {
  }

  public SchemaGeneratorException(String message) {
    super(message);
  }

  public SchemaGeneratorException(Throwable cause) {
    super(cause);
  }

  public SchemaGeneratorException(String message, Throwable cause) {
    super(message, cause);
  }

}
