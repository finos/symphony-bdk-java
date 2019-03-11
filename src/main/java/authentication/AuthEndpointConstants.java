package authentication;

public final class AuthEndpointConstants {
    public static final String SESSIONAUTHPATH = "/sessionauth/v1/authenticate";
    public static final String KEYAUTHPATH = "/keyauth/v1/authenticate";
    public static final String HTTPSPREFIX = "https://";
    public static final String LOGOUTPATH = "/sessionauth/v1/logout";
    public static final String RSASESSIONAUTH = "/login/pubkey/authenticate";
    public static final String RSAKMAUTH = "/relay/pubkey/authenticate";
    public static final String SESSIONAPPAUTH =
            "/sessionauth/v1/app/authenticate";
    public static final String SESSIONEXTAPPAUTH =
            "/sessionauth/v1/authenticate/extensionApp";
    public static final String RSASESSIONEXTAPPAUTH =
            "/login/v1/pubkey/app/authenticate/extensionApp";
    public static final String OBOUSERAUTH =
            "/sessionauth/v1/app/user/{uid}/authenticate";
    public static final String OBOUSERAUTHUSERNAME =
            "/sessionauth/v1/app/username/{username}/authenticate";
    public static final String PODCERT = "/sessionauth/v1/app/pod/certificate";
    public static final int WAITIME = 3000;
    public static final int TIMEOUT = 30;
    public static final int MAX_AUTH_RETRY = 5;
    public static final Long JWTEXP = 300000L;

    private AuthEndpointConstants() {
    }
}
