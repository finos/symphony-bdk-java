package utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Date;

public class JwtHelper {
    // PKCS#8 format
    private static final String PEM_PRIVATE_START = "-----BEGIN PRIVATE KEY-----";
    private static final String PEM_PRIVATE_END = "-----END PRIVATE KEY-----";

    // PKCS#1 format
    private static final String PEM_RSA_PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";
    private static final String PEM_RSA_PRIVATE_END = "-----END RSA PRIVATE KEY-----";

    /**
     * Creates a JWT with the provided user name and expiration date, signed with the provided private key.
     * @param user the username to authenticate; will be verified by the pod
     * @param expiration of the authentication request in milliseconds; cannot be longer than the value defined on the pod
     * @param privateKey the private RSA key to be used to sign the authentication request; will be checked on the pod against
     * the public key stored for the user
     */
    public static String createSignedJwt(String user, long expiration, Key privateKey) {

        return Jwts.builder()
                .setSubject(user)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.RS512, privateKey)
                .compact();
    }

    public static PrivateKey parseRSAPrivateKey(final File pemPrivateKeyFile) throws IOException, GeneralSecurityException {
        return parseRSAPrivateKey(FileUtils.readFileToString(pemPrivateKeyFile, Charset.defaultCharset()));
    }

    /**
     * Create a RSA Private Ket from a PEM String. It supports PKCS#1 and PKCS#8 string formats
     */
    private static PrivateKey parseRSAPrivateKey(final String pemPrivateKey)
            throws GeneralSecurityException {
        try {
            if (pemPrivateKey.contains(PEM_PRIVATE_START)) { // PKCS#8 format
                String privateKeyString = pemPrivateKey
                        .replace(PEM_PRIVATE_START, "")
                        .replace(PEM_PRIVATE_END, "")
                        .replace("\\n", "\n")
                        .replaceAll("\\s", "");
                byte[] keyBytes = Base64.decodeBase64(privateKeyString.getBytes("UTF-8"));
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory fact = KeyFactory.getInstance("RSA");
                return fact.generatePrivate(keySpec);

            } else if (pemPrivateKey.contains(PEM_RSA_PRIVATE_START)) {  // PKCS#1 format
                String privateKeyString = pemPrivateKey
                        .replace(PEM_RSA_PRIVATE_START, "")
                        .replace(PEM_RSA_PRIVATE_END, "")
                        .replace("\\n", "\n")
                        .replaceAll("\\s", "");

                DerInputStream derReader = new DerInputStream(Base64.decodeBase64(privateKeyString));

                DerValue[] seq = derReader.getSequence(0);

                if (seq.length < 9) {
                    throw new GeneralSecurityException("Could not parse a PKCS1 private key.");
                }

                // skip version seq[0];
                BigInteger modulus = seq[1].getBigInteger();
                BigInteger publicExp = seq[2].getBigInteger();
                BigInteger privateExp = seq[3].getBigInteger();
                BigInteger prime1 = seq[4].getBigInteger();
                BigInteger prime2 = seq[5].getBigInteger();
                BigInteger exp1 = seq[6].getBigInteger();
                BigInteger exp2 = seq[7].getBigInteger();
                BigInteger crtCoef = seq[8].getBigInteger();

                RSAPrivateCrtKeySpec keySpec =
                        new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);

                KeyFactory factory = KeyFactory.getInstance("RSA");

                return factory.generatePrivate(keySpec);
            }
            throw new GeneralSecurityException("Invalid private key.");

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            throw new GeneralSecurityException(e);
        }
    }
}
