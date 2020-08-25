package com.symphony.bdk.core.service;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.model.UserV2;
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for implementing the bot ingo services.
 * BotInfoService can help the bot retrieving the information about itself by using session API.
 */
@Slf4j
public class BotInfoService {

    private SessionApi sessionApi;
    private final AuthSession authSession;
    private static UserV2 userInfo;

    public BotInfoService(ApiClient podClient, AuthSession authSession) {
        this.sessionApi = new SessionApi(podClient);
        this.authSession = authSession;
    }

    /**
     * Get the information about the bot itself
     *
     * @return information about the bot itself
     */
    public UserV2 getBotInfo() throws ApiException {
        if (userInfo == null) {
            log.debug("Call session api to get bot info");
            userInfo = this.sessionApi.v2SessioninfoGet(authSession.getSessionToken());
        }
        return userInfo;
    }

    protected void setSessionApi(SessionApi sessionApi) {
        this.sessionApi = sessionApi;
    }

}
