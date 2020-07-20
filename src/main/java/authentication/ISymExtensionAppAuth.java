package authentication;

import model.AppAuthResponse;
import model.UserInfo;

import java.security.PublicKey;
import java.security.cert.CertificateException;

public interface ISymExtensionAppAuth {
    AppAuthResponse appAuthenticate();
    AppAuthResponse sessionAppAuthenticate(String appToken, String... podSessionAuthUrl);
    UserInfo verifyJWT(String jwt, String... podSessionAuthUrl);
}
