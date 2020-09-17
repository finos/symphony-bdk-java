package com.symphony.bdk.core.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apiguardian.api.API;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
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

  // Expiration of the jwt
  public static final Long JWT_EXPIRATION_MILLIS = 300_000L;

	// PKCS#8 format
	private static final String PEM_PRIVATE_START = "-----BEGIN PRIVATE KEY-----";
	private static final String PEM_PRIVATE_END = "-----END PRIVATE KEY-----";

	// PKCS#1 format
	private static final String PEM_RSA_PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";

	/**
	 * Creates a JWT with the provided user name and expiration date, signed with the provided private key.
	 * @param user the username to authenticate; will be verified by the pod
	 * @param expiration of the authentication request in milliseconds; cannot be longer than the value defined on the
	 * pod
	 * @param privateKey the private RSA key to be used to sign the authentication request; will be checked on the pod
	 * against
	 * the public key stored for the user
	 * @return a signed JWT for a specific user (or subject)
	 */
	public String createSignedJwt(String user, long expiration, Key privateKey) {
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
	public PrivateKey parseRsaPrivateKey(final String pemPrivateKey) throws GeneralSecurityException {

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
			throw new GeneralSecurityException("Invalid private key. Header not recognized.");
		}
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
		} catch (IOException e) {
			throw new GeneralSecurityException("Invalid private key.", e);
		}
	}

	private static PrivateKey parsePKCS8PrivateKey(String pemPrivateKey) throws InvalidKeySpecException, NoSuchAlgorithmException {

		final String privateKeyString = pemPrivateKey
			.replace(PEM_PRIVATE_START, "")
			.replace(PEM_PRIVATE_END, "")
			.replace("\\n", "\n")
			.replaceAll("\\s", "");

		final byte[] keyBytes = Base64.getDecoder().decode(privateKeyString.getBytes(StandardCharsets.UTF_8));

		return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
	}
}
