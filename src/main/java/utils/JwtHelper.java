package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import model.UserInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.LoggerFactory;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Pattern;

public class JwtHelper {
    private static final Pattern PKCS_1_REGEX = Pattern.compile("-{5}BEGIN RSA PRIVATE KEY-{5}.*-{5}END RSA PRIVATE KEY-{5}", Pattern.DOTALL);
    private static final Pattern PKCS_8_REGEX = Pattern.compile("-{5}BEGIN PRIVATE KEY-{5}.*-{5}END PRIVATE KEY-{5}", Pattern.DOTALL);
    private static final String PKCS_PADDING_REGEX = "[\\r\\n]?-{5}(BEGIN|END) (RSA )?PRIVATE KEY-{5}[\\r\\n]?";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates a JWT with the provided user name and expiration date, signed with the provided private key.
     *
     * @param user       the username to authenticate; will be verified by the pod
     * @param expiration of the authentication request in milliseconds; cannot be longer than the value defined on the pod
     * @param privateKey the private RSA key to be used to sign the authentication request; will be checked on the pod against
     *                   the public key stored for the user
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

    public static PrivateKey parseRSAPrivateKey(final InputStream pemPrivateKeyFile) throws IOException, GeneralSecurityException {
        return parseRSAPrivateKey(IOUtils.toString(pemPrivateKeyFile, Charset.defaultCharset()));
    }

    /**
     * Create a RSA Private Ket from a PEM String. It supports PKCS#1 and PKCS#8 string formats
     */
    private static PrivateKey parseRSAPrivateKey(String pemPrivateKey) throws GeneralSecurityException {
        pemPrivateKey = pemPrivateKey.trim();
        boolean isPkcs1 = PKCS_1_REGEX.matcher(pemPrivateKey).matches();
        boolean isPkcs8 = PKCS_8_REGEX.matcher(pemPrivateKey).matches();
        if (!isPkcs1 && !isPkcs8) {
            throw new GeneralSecurityException("Invalid private key.");
        }

        pemPrivateKey = pemPrivateKey
            .replaceAll(PKCS_PADDING_REGEX, "")
            .replaceAll("\\n", "\n")
            .replaceAll("\\s", "");
        byte[] pemPrivateKeyBytes = Base64.getDecoder().decode(pemPrivateKey);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        if (isPkcs8) {
            try {
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pemPrivateKeyBytes);
                return keyFactory.generatePrivate(spec);
            } catch (InvalidKeySpecException e) {
                throw new GeneralSecurityException("Invalid PKCS#8 private key.");
            }
        } else {
            try {
                DerInputStream derReader = new DerInputStream(pemPrivateKeyBytes);
                DerValue[] seq = derReader.getSequence(0);
                BigInteger modulus = seq[1].getBigInteger();
                BigInteger publicExp = seq[2].getBigInteger();
                BigInteger privateExp = seq[3].getBigInteger();
                BigInteger prime1 = seq[4].getBigInteger();
                BigInteger prime2 = seq[5].getBigInteger();
                BigInteger exp1 = seq[6].getBigInteger();
                BigInteger exp2 = seq[7].getBigInteger();
                BigInteger crtCoefficient = seq[8].getBigInteger();
                RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(
                    modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoefficient);
                return keyFactory.generatePrivate(keySpec);
            } catch (IOException e) {
                throw new GeneralSecurityException("Invalid PKCS#1 private key.");
            }
        }
    }

    public static UserInfo validateJwt(String jwt, String certificate) {
        try {
            PublicKey publicKey = CertificateUtils.parseX509Certificate(certificate).getPublicKey();
            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setVerificationKey(publicKey)
                .setSkipAllValidators()
                .build();
            JwtClaims jwtDecoded = jwtConsumer.processToClaims(jwt);
            Object userInfoObject = jwtDecoded.getClaimValue("user");
            return objectMapper.readValue(objectMapper.writeValueAsString(userInfoObject), UserInfo.class);
        } catch (GeneralSecurityException | InvalidJwtException | IOException e) {
            LoggerFactory.getLogger(JwtHelper.class).error("Error with decoding jwt", e);
            return null;
        }
    }
}
