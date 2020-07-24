package authentication;

import model.AppAuthResponse;
import model.UserInfo;

import java.security.PublicKey;
import java.security.cert.CertificateException;

public interface ISymExtensionAppAuth {

    /**
     * Authenticate an extension app by using the auto generated appToken
     *
     * @return Authentication Information containing appToken, SymphonyToken, AppId and expire time.
     */
    AppAuthResponse appAuthenticate();

    /**
     * Authenticate an extension app by using specific appToken and podSessionAuthUrl
     *
     * @param appToken appToken given to extension app Auth session
     * @param podSessionAuthUrl authentication target host, if no value given, using the url in config.json
     *
     * @return Authentication Information containing appToken, SymphonyToken, AppId and expire time.
     */
    AppAuthResponse sessionAppAuthenticate(String appToken, String... podSessionAuthUrl);

    /**
     * Verify a JWT
     *
     * @param jwt JWT need to be verified
     * @param podSessionAuthUrl authentication target host, if no value given, using the url in config.json
     *
     * @return User information extracted from jwt
     */
    UserInfo verifyJWT(String jwt, String... podSessionAuthUrl);

    /**
     * Validate tokens caching in tokens repository
     *
     * @param appToken appToken
     * @param symphonyToken symphonyToken
     *
     * @return Boolean if tokens exists in token repository
     */
    Boolean validateTokens(String appToken, String symphonyToken);
}
