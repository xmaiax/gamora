package br.fmaia.gamora.utils;

public class SecurityUtils {

  private final String CONST_MD5 = "MD5", CONST_SHA1 = "SHA1";

  protected org.apache.log4j.Logger logger;
  public org.apache.log4j.Logger getLogger() { return this.logger; }

  private static SecurityUtils instance = new SecurityUtils();
  public static SecurityUtils getInstance() { return instance; }

  private SecurityUtils() { }

  private java.security.MessageDigest getInstanceAlgoritmo(String algoritmo) {
    try {
      return java.security.MessageDigest.getInstance(algoritmo);
    }
    catch (java.security.NoSuchAlgorithmException e) {
      this.logger.fatal(String.format("Algoritmo '%s' n\u00e3o encontrado no sistema.", algoritmo));
      System.exit(-1);
    }
    return null;
  }

  private String converterEntradaPeloMessageDigest(
      java.security.MessageDigest messageDigest,
      String entrada) {
    if(entrada == null || entrada.length() < 1) {
      this.logger.error("Erro ao criptografar 'null'");
      return null;
    }
    byte[] array = messageDigest.digest(entrada.getBytes());
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < array.length; ++i) {
      sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
    }
    return sb.toString();
  }

  public String string2MD5(String entrada) {
    return this.converterEntradaPeloMessageDigest(this.getInstanceAlgoritmo(CONST_MD5), entrada);
  }

  public String string2SHA1(String entrada) {
    return this.converterEntradaPeloMessageDigest(this.getInstanceAlgoritmo(CONST_SHA1), entrada);
  }

}
