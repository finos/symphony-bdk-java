package com.symphony.bdk.core.auth.jwt;

import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apiguardian.api.API;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * JWT helper class, used to :
 * <ul>
 *   <li>load a private key</li>
 *   <li>generated a signed JWT for a given user</li>
 * </ul>
 */
@API(status = API.Status.INTERNAL)
public class JwtHelper {

  // Expiration of the jwt, 5 minutes maximum, use a little less than that in case of different clock skews
  public static final Long JWT_EXPIRATION_MILLIS = 240_000L;

  // PKCS#8 format
  private static final String PEM_PRIVATE_START = "-----BEGIN PRIVATE KEY-----";
  private static final String PEM_PRIVATE_END = "-----END PRIVATE KEY-----";

  // PKCS#1 format
  private static final String PEM_RSA_PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";

  // X509 certificate
  private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
  private static final String END_CERTIFICATE = "-----END CERTIFICATE-----";

  private static final ObjectMapper mapper = new ObjectMapper();


  /**
   * Creates a JWT with the provided user name and expiration date, signed with the provided private key.
   *
   * @param user       the username to authenticate; will be verified by the pod
   * @param expiration of the authentication request in milliseconds; cannot be longer than the value defined on the
   *                   pod
   * @param privateKey the private RSA key to be used to sign the authentication request; will be checked on the pod
   *                   against
   *                   the public key stored for the user
   * @return a signed JWT for a specific user (or subject)
   */
  public static String createSignedJwt(String user, long expiration, Key privateKey) {
    return Jwts.builder()
        .setSubject(user)
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(SignatureAlgorithm.RS512, privateKey)
        .compact();
  }

  /**
   * Creates a RSA Private Key from a PEM String. It supports PKCS#1 and PKCS#8 string formats.
   *
   * @param pemPrivateKey RSA Private Key content
   * @return a {@link PrivateKey} instance
   * @throws GeneralSecurityException On invalid Private Key
   */
  public static PrivateKey parseRsaPrivateKey(final String pemPrivateKey) throws GeneralSecurityException {

    // PKCS#8 format
    if (pemPrivateKey.contains(PEM_PRIVATE_START)) {
      return parsePKCS8PrivateKey(pemPrivateKey);
    }
    // PKCS#1 format
    else if (pemPrivateKey.contains(PEM_RSA_PRIVATE_START)) {
      return parsePKCS1PrivateKey(pemPrivateKey);
    }
    // format not detected
    else {
      throw new GeneralSecurityException(
          "Invalid private key. Header not recognized, only PKCS8 and PKCS1 format are allowed.");
    }
  }

  /**
   * Validates a jwt against a certificate.
   *
   * @param jwt
   * @param certificate string of the X.509 certificate content in pem format.
   * @return the content of jwt clain "user" if jwt is successfully validated.
   * @throws AuthInitializationException if certificate or jwt are invalid.
   */
  public static UserClaim validateJwt(String jwt, String certificate) throws AuthInitializationException {
    final Certificate x509Certificate = parseX509Certificate(certificate);

    try {
      final Claims body = Jwts.parser().setSigningKey(x509Certificate.getPublicKey()).parseClaimsJws(jwt).getBody();

      return mapper.convertValue(body.get("user"), UserClaim.class);
    } catch (JwtException e) {
      throw new AuthInitializationException("Unable to validate jwt", e);
    }
  }

  /**
   * Extract the expiration date (in seconds) from the input jwt. If the jwt uses the Beare prefix, it
   * will be removed before parsing. This function is not validating  the jwt signature.
   *
   * @param jwt to be parsed
   * @return expiration date in seconds
   * @throws JsonProcessingException if parsing fails
   */
  public static Long extractExpirationDate(String jwt) throws JsonProcessingException, AuthUnauthorizedException {
    String claimsObj = extractDecodedClaims(dropBearer(jwt));
    ObjectNode claims = mapper.readValue(claimsObj, ObjectNode.class);
    if(claims.has("exp") && claims.get("exp").isNumber()) {
      return claims.get("exp").asLong();
    }
    throw new AuthUnauthorizedException("Unable to find expiration date in the common jwt.");
  }

  private static String extractDecodedClaims(String jwt) throws AuthUnauthorizedException {
    String[] jwtSplit = jwt.split("\\.");
    if (jwtSplit.length < 3) {
      throw new AuthUnauthorizedException("Unable to parse jwt");
    }
    return new String(Base64.getDecoder().decode(jwtSplit[1]));
  }

  private static String dropBearer(String jwt) {
    return jwt.replace("Bearer ", "");
  }

  private static PrivateKey parsePKCS1PrivateKey(String pemPrivateKey) throws GeneralSecurityException {
    try (final PemReader pemReader = new PemReader(new StringReader(pemPrivateKey))) {
      final PemObject privateKeyObject = pemReader.readPemObject();
      final RSAPrivateKey rsa = RSAPrivateKey.getInstance(privateKeyObject.getContent());
      final RSAPrivateCrtKeyParameters privateKeyParameter = new RSAPrivateCrtKeyParameters(
          rsa.getModulus(),
          rsa.getPublicExponent(),
          rsa.getPrivateExponent(),
          rsa.getPrime1(),
          rsa.getPrime2(),
          rsa.getExponent1(),
          rsa.getExponent2(),
          rsa.getCoefficient()
      );

      return new JcaPEMKeyConverter().getPrivateKey(PrivateKeyInfoFactory.createPrivateKeyInfo(privateKeyParameter));
    } catch (IOException | IllegalStateException | IllegalArgumentException e) {
      throw new GeneralSecurityException(
          "Unable to parse the private key. Check if the format of your private key is correct.", e);
    }
  }

  private static PrivateKey parsePKCS8PrivateKey(String pemPrivateKey) throws GeneralSecurityException {
    try {
      final String privateKeyString = pemPrivateKey.replace(PEM_PRIVATE_START, "")
          .replace(PEM_PRIVATE_END, "")
          .replace("\\n", "\n")
          .replaceAll("\\s", "");

      final byte[] keyBytes = Base64.getDecoder().decode(privateKeyString.getBytes(StandardCharsets.UTF_8));

      return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
      throw new GeneralSecurityException(
          "Unable to parse the pem private key. Check if the format of your private key is correct.", e);
    }
  }

  protected static Certificate parseX509Certificate(String certificate) throws AuthInitializationException {
    try {
      String sanitizedBase64Der = certificate
          .replace(BEGIN_CERTIFICATE, "")
          .replace(END_CERTIFICATE, "")
          .replaceAll("\\s", "");

      byte[] decoded = Base64.getDecoder().decode(sanitizedBase64Der);

      return CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(decoded));
    } catch (CertificateException e) {
      throw new AuthInitializationException(
          "Unable to parse the certificate. Check if the format of your certificate is correct.", e);
    }
  }
}
