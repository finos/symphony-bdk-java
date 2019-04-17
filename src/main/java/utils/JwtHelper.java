package utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.LoggerFactory;

import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
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
     * @return The JWT token
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
    private static PrivateKey parseRSAPrivateKey(final String pemPrivateKey) throws GeneralSecurityException {
        if (!pemPrivateKey.contains(PEM_PRIVATE_START) && !pemPrivateKey.contains(PEM_RSA_PRIVATE_START)) {
            throw new GeneralSecurityException("Invalid private key.");
        }

        String privKeyPEM = pemPrivateKey
            .replace(PEM_PRIVATE_START, "")
            .replace(PEM_PRIVATE_END, "")
            .replace(PEM_RSA_PRIVATE_START, "")
            .replace(PEM_RSA_PRIVATE_END, "")
            .replaceAll("\\n", "\n")
            .replaceAll("\\s", "");

        if (pemPrivateKey.contains(PEM_PRIVATE_START)) {
            try {
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privKeyPEM));
                KeyFactory kf = KeyFactory.getInstance("RSA");
                return kf.generatePrivate(spec);
            } catch (InvalidKeySpecException e) {
                throw new GeneralSecurityException("Invalid PKCS#8 private key.");
            }
        } else {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            PEMParser pemParser = new PEMParser(new CharArrayReader(pemPrivateKey.toCharArray()));
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            try {
                KeyPair kp = converter.getKeyPair((PEMKeyPair) pemParser.readObject());
                return kp.getPrivate();
            } catch (IOException e) {
                throw new GeneralSecurityException("Invalid PKCS#1 private key.");
            }
        }
    }

    public static Object validateJwt(String jwt, String certificate){
        // Get the public key from the cert
        PublicKey publicKey;
        try {
            X509Certificate x509Certificate =
                CertificateUtils.parseX509Certificate(certificate);
            publicKey = x509Certificate.getPublicKey();
            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setVerificationKey(publicKey)
                .setSkipAllValidators()
                .build();
            // validate and decode the jwt
            JwtClaims jwtDecoded = jwtConsumer.processToClaims(jwt);
            return jwtDecoded.getClaimValue("user");
        } catch (GeneralSecurityException | InvalidJwtException e) {
            LoggerFactory.getLogger(JwtHelper.class).error("Error with decoding jwt", e);
            return null;
        }
    }
}
