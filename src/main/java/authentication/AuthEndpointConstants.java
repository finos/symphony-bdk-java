package authentication;

public final class AuthEndpointConstants {
    public static final String SESSION_AUTH_PATH = "/sessionauth/v1/authenticate";
    public static final String SESSION_AUTH_PATH_RSA = "/login/pubkey/authenticate";

    public static final String KEY_AUTH_PATH = "/keyauth/v1/authenticate";
    public static final String KEY_AUTH_PATH_RSA = "/relay/pubkey/authenticate";

    public static final String SESSION_APP_AUTH_PATH = "/sessionauth/v1/app/authenticate";
    public static final String SESSION_APP_AUTH_PATH_RSA = "/login/pubkey/app/authenticate";

    public static final String SESSION_EXT_APP_AUTH_PATH = "/sessionauth/v1/authenticate/extensionApp";
    public static final String SESSION_EXT_APP_AUTH_PATH_RSA = "/login/v1/pubkey/app/authenticate/extensionApp";

    public static final String OBO_USER_ID_AUTH_PATH = "/sessionauth/v1/app/user/{uid}/authenticate";
    public static final String OBO_USER_ID_AUTH_PATH_RSA = "/login/pubkey/app/user/{uid}/authenticate";

    public static final String OBO_USER_NAME_AUTH_PATH = "/sessionauth/v1/app/username/{username}/authenticate";
    public static final String OBO_USER_NAME_AUTH_PATH_RSA = "/login/pubkey/app/username/{username}/authenticate";

    public static final String POD_CERT_PATH = "/sessionauth/v1/app/pod/certificate";
    public static final String LOGOUT_PATH = "/sessionauth/v1/logout";

    public static final int WAIT_TIME = 3000;
    public static final int TIMEOUT = 30;
    public static final int MAX_AUTH_RETRY = 5;
    public static final Long JWT_EXPIRY_MS = 300000L;

    private AuthEndpointConstants() {
    }
}
